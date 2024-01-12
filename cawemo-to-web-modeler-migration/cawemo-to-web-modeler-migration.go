package main

import (
	"bytes"
	"encoding/base64"
	"encoding/csv"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"os"
	"slices"
	"strings"
)

var webModelerFileTypes = []string{"bpmn", "dmn", "form", "connector_template"}

// cawemo auth data
var cawemoUserId = ""
var cawemoApiKey = ""

// web modeler auth data
var modelerClientId = ""
var modelerClientSecret = ""

var token = ""

var logLevel = "info"

func main() {
	cawemoUserId = os.Getenv("CAWEMO_USER_ID")
	cawemoApiKey = os.Getenv("CAWEMO_API_KEY")
	modelerClientId = os.Getenv("CAMUNDA_CONSOLE_CLIENT_ID")
	modelerClientSecret = os.Getenv("CAMUNDA_CONSOLE_CLIENT_SECRET")
	validateCredentials()
	fetchWebModelerToken()
	handleProjects()
}

func validateCredentials() {
	errorMessages := []string{}
	if cawemoUserId == "" {
		errorMessages = append(errorMessages, "No cawemo user id found, please set CAWEMO_USER_ID")
	}
	if cawemoApiKey == "" {
		errorMessages = append(errorMessages, "No cawemo api key found, please set CAWEMO_API_KEY")
	}
	if modelerClientId == "" {
		errorMessages = append(errorMessages, "No modeler client id found, please set CAMUNDA_CONSOLE_CLIENT_ID")
	}
	if modelerClientSecret == "" {
		errorMessages = append(errorMessages, "No modeler client secret found, please set CAMUNDA_CONSOLE_CLIENT_SECRET")
	}
	if len(errorMessages) > 0 {
		log.Fatalln(strings.Join(errorMessages, "\n"))
	}
}

func handleProjects() {
	var projects []any = getCawemoProjects()
	for _, projectAny := range projects {
		handleProject(projectAny.(map[string]any))
	}
}

func handleProject(project map[string]any) {
	projectId := project["id"].(string)
	projectType := project["type"].(string)
	if projectType == "CATALOG" {
		log.Println("Skipping catalog project " + projectId)
		return
	}
	webModelerProjectId := checkState(projectId, "project")
	if webModelerProjectId == "" {
		projectName := project["name"].(string)
		webModelerProject := createWebModelerProject(projectName)
		webModelerProjectId = webModelerProject["id"].(string)
		setState(projectId, webModelerProjectId, "project")
	}
	handleFiles(projectId, webModelerProjectId)

}

func handleFiles(cawemoProjectId string, webModelerProjectId string) {
	var files []any = getCawemoFiles(cawemoProjectId)
	for _, fileAny := range files {
		handleFile(fileAny.(map[string]any), cawemoProjectId, webModelerProjectId)
	}
}

func handleFile(file map[string]any, cawemoProjectId string, webModelerProjectId string) {
	fileId := file["id"].(string)
	rawFileType := file["type"].(string)
	filetype := determineFileType(rawFileType)
	if filetype == "" {
		log.Println("Cannot handle file type " + rawFileType)
		return
	}
	webModelerFileId := checkState(fileId, "file")
	fileDetails := getCawemoFile(fileId)
	content := fileDetails["content"].(string)
	revision := 0
	if webModelerFileId == "" {
		canonicalPath := file["canonicalPath"].([]any)
		parentId := handleFolders(canonicalPath, cawemoProjectId, webModelerProjectId)
		// create file with milestones
		filename := file["name"].(string)
		webModelerFile := createWebModelerFile(filename, parentId, webModelerProjectId, content, filetype)
		revision = int(webModelerFile["revision"].(float64))
		webModelerFileId = webModelerFile["id"].(string)
		setState(fileId, webModelerFileId, "file")
	} else {
		webModelerFile := getWebModelerFile(webModelerFileId)
		metadata := webModelerFile["metadata"].(map[string]any)
		revision = int(metadata["revision"].(float64))
	}
	revision = handleMilestones(fileId, webModelerFileId, revision)
	updateWebModelerFile(webModelerFileId, content, revision)
}

func determineFileType(filetype string) string {
	if slices.Contains(webModelerFileTypes, strings.ToLower(filetype)) {
		return filetype
	}
	return ""
}

func handleMilestones(cawemoFileId string, webModelerFileId string, revision int) int {
	milestones := getCawemoMilestones(cawemoFileId)
	newRevision := revision
	for _, milestone := range milestones {
		newRevision = handleMilestone(cawemoFileId, webModelerFileId, milestone.(map[string]any), newRevision)
	}
	return newRevision
}

func handleMilestone(cawemoFileId string, webModelerFileId string, milestone map[string]any, revision int) int {
	milestoneId := milestone["id"].(string)
	webModelerMilestoneId := checkState(milestoneId, "milestone")
	newRevision := revision
	if webModelerMilestoneId == "" {
		milestoneName := milestone["name"].(string)
		milestoneDetails := getCawemoMilestone(milestoneId)
		content := milestoneDetails["content"].(string)
		updatedFile := updateWebModelerFile(webModelerFileId, content, revision)
		newRevision = int(updatedFile["revision"].(float64))
		webModelerMilestone := createWebModelerMilestone(milestoneName, webModelerFileId)
		webModelerMilestoneId := webModelerMilestone["id"].(string)
		setState(milestoneId, webModelerMilestoneId, "milestone")
	}
	return newRevision
}

func handleFolders(folders []any, cawemoProjectId string, webModelerProjectId string) string {
	parentId := ""
	for _, folderAny := range folders {
		parentId = handleFolder(folderAny.(map[string]any), parentId, cawemoProjectId, webModelerProjectId)
	}
	return parentId
}

func handleFolder(folder map[string]any, parentId string, cawemoProjectId string, webModelerProjectId string) string {
	cawemoFolderId := folder["id"].(string)
	webModelerFolderId := checkState(cawemoFolderId, "folder")
	if webModelerFolderId == "" {
		name := folder["name"].(string)
		webModelerFolder := createWebModelerFolder(name, webModelerProjectId, parentId)
		webModelerFolderId = webModelerFolder["id"].(string)
		setState(cawemoFolderId, webModelerFolderId, "folder")
	}
	return webModelerFolderId
}

// functions to interact with state

func setState(cawemoId string, webModelerId string, entityType string) {
	line := []string{cawemoId, webModelerId, entityType}
	if !checkFilePresent() {
		initFile()
	}
	f, err := os.OpenFile("id-mappings.csv", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Println(err)
	}
	defer f.Close()
	csvWriter := csv.NewWriter(f)

	if err := csvWriter.Write(line); err != nil {
		log.Println(err)
	}
	csvWriter.Flush()
	log.Println(entityType + " created: " + cawemoId + " -> " + webModelerId)
}

func checkFilePresent() bool {
	_, err := os.Stat("id-mappings.csv")
	return err == nil
}

func initFile() {
	line := []string{"CawemoId", "WebModelerId", "EntityType"}
	f, err := os.OpenFile("id-mappings.csv", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Println(err)
	}
	defer f.Close()
	csvWriter := csv.NewWriter(f)

	if err := csvWriter.Write(line); err != nil {
		log.Println(err)
	}
	csvWriter.Flush()
}

func checkState(cawemoId string, entityType string) string {
	// Open the file in read-only mode
	file, err := os.Open("id-mappings.csv")
	if err != nil {
		return ""
	}
	defer file.Close()
	csvReader := csv.NewReader(file)
	for {
		line, err := csvReader.Read()
		if err == io.EOF {
			break
		}
		if err != nil {
			log.Fatalln(err)
		}
		if line[0] == cawemoId && line[2] == entityType {
			webModelerId := line[1]
			log.Println(entityType + " already exists: " + cawemoId + " -> " + webModelerId)
			return webModelerId
		}
	}
	return ""
}

// functions to fetch from Cawemo

func getCawemoProjects() []any {
	return fetchFromCawemo("GET", "projects", nil).([]any)
}

func getCawemoFiles(projectId string) []any {
	return fetchFromCawemo("GET", "projects/"+projectId+"/files", nil).([]any)
}

func getCawemoFile(fileId string) map[string]any {
	return fetchFromCawemo("GET", "files/"+fileId, nil).(map[string]any)
}

func getCawemoMilestones(fileId string) []any {
	return fetchFromCawemo("GET", "files/"+fileId+"/milestones", nil).([]any)
}

func getCawemoMilestone(milestoneId string) map[string]any {
	return fetchFromCawemo("GET", "milestones/"+milestoneId, nil).(map[string]any)
}

// functions to fetch from Web Modeler

func createWebModelerProject(name string) map[string]any {
	body := map[string]string{
		"name": name,
	}
	return fetchFromWebModeler("POST", "projects", createBody(body)).(map[string]any)
}

func createWebModelerFolder(name string, projectId string, parentId string) map[string]any {
	body := map[string]string{
		"name": name,
	}
	if projectId != "" {
		body["projectId"] = projectId
	}
	if parentId != "" {
		body["parentId"] = parentId
	}
	return fetchFromWebModeler("POST", "folders", createBody(body)).(map[string]any)
}

func createWebModelerFile(name string, folderId string, projectId string, content string, fileType string) map[string]any {
	body := map[string]string{
		"name":     name,
		"content":  content,
		"fileType": fileType,
	}
	if folderId != "" {
		body["folderId"] = folderId
	} else if projectId != "" {
		body["projectId"] = projectId
	}
	return fetchFromWebModeler("POST", "files", createBody(body)).(map[string]any)
}

func updateWebModelerFile(fileId string, content string, revision int) map[string]any {
	body := map[string]any{
		"content":  content,
		"revision": revision,
	}
	return fetchFromWebModeler("PATCH", "files/"+fileId, createBody(body)).(map[string]any)
}

func createWebModelerMilestone(name string, fileId string) map[string]any {
	body := map[string]string{
		"name":   name,
		"fileId": fileId,
	}
	return fetchFromWebModeler("POST", "milestones", createBody(body)).(map[string]any)
}

func getWebModelerFile(fileId string) map[string]any {
	return fetchFromWebModeler("GET", "files/"+fileId, nil).(map[string]any)
}

// generic cawemo and web modeler functions

func fetchFromCawemo(method string, context string, body io.Reader) any {
	req, _ := http.NewRequest(method, "https://cawemo.com/api/v1/"+context, body)
	req.Header.Add("Authorization", "Basic "+basicAuth(cawemoUserId, cawemoApiKey))
	return fetch(req)
}

func fetchFromWebModeler(method string, context string, body io.Reader) any {
	req, _ := http.NewRequest(method, "https://modeler.cloud.camunda.io/api/v1/"+context, body)
	req.Header.Add("Authorization", "Bearer "+token)
	return fetch(req)
}

func fetchWebModelerToken() {
	body := map[string]string{
		"grant_type":    "client_credentials",
		"audience":      "api.cloud.camunda.io",
		"client_id":     modelerClientId,
		"client_secret": modelerClientSecret,
	}
	req, _ := http.NewRequest("POST", "https://login.cloud.camunda.io/oauth/token", createBody(body))
	response := fetch(req).(map[string]any)
	token = response["access_token"].(string)
}

// helper functions

func createBody(body any) io.Reader {
	marshalled, _ := json.Marshal(body)
	return bytes.NewReader(marshalled)
}

func basicAuth(username, password string) string {
	auth := username + ":" + password
	return base64.StdEncoding.EncodeToString([]byte(auth))
}

func fetch(req *http.Request) any {
	req.Header.Set("Content-Type", "application/json")
	client := &http.Client{}
	response, err := client.Do(req)
	if err != nil {
		log.Fatalln(err)
	}
	if response.StatusCode > 299 {
		log.Fatalln(formatResponse(*response))
	}
	return formatResponse(*response)
}

func formatResponse(response http.Response) any {
	responseData, err := io.ReadAll(response.Body)
	if err != nil {
		log.Fatal(err)
	}
	var result any
	json.Unmarshal(responseData, &result)
	if logLevel == "debug" {
		formatted, _ := json.MarshalIndent(result, "", "  ")
		log.Println(string(formatted))
	}
	return result
}
