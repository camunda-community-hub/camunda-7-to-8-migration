package main

import (
	"bytes"
	"encoding/base64"
	"encoding/csv"
	"encoding/json"
	"errors"
	"io"
	"log"
	"net/http"
	"os"
	"regexp"
	"slices"
	"strings"
)

var dmnFix = regexp.MustCompile(`camunda:diagramRelationId=".*"`)

var webModelerFileTypes = []string{"bpmn", "dmn", "form", "connector_template"}

// cawemo auth data
var cawemoBaseUrl = "https://cawemo.com"
var cawemoUserId = ""
var cawemoApiKey = ""

// web modeler auth data
var modelerBaseUrl = "https://modeler.cloud.camunda.io"
var modelerAuthUrl = "https://login.cloud.camunda.io/oauth/token"
var modelerTokenAudience = "api.cloud.camunda.io"
var modelerCredentialsType = "json"
var modelerClientId = ""
var modelerClientSecret = ""

var token = ""

var logLevel = "info"

func main() {
	cawemoUserId = os.Getenv("CAWEMO_USER_ID")
	cawemoApiKey = os.Getenv("CAWEMO_API_KEY")
	modelerClientId = os.Getenv("CAMUNDA_CONSOLE_CLIENT_ID")
	modelerClientSecret = os.Getenv("CAMUNDA_CONSOLE_CLIENT_SECRET")
	validateCredentialsErr := validateCredentials()
	if validateCredentialsErr != nil {
		log.Fatalln(validateCredentialsErr.Error())
	}
	fetchWebModelerTokenErr := fetchWebModelerToken()
	if fetchWebModelerTokenErr != nil {
		log.Fatalln(fetchWebModelerTokenErr.Error())
	}
	handleProjectsErr := handleProjects()
	if handleProjectsErr != nil {
		log.Fatalln(handleProjectsErr.Error())
	}
}

func validateCredentials() error {
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
		return errors.New(strings.Join(errorMessages, "\n"))
	}
	return nil
}

func handleProjects() error {
	projects, err := getCawemoProjects()
	if err != nil {
		return err
	}
	for _, projectAny := range projects {
		err := handleProject(projectAny.(map[string]any))
		if err != nil {
			if err != nil {
				log.Println("Error while handling project, skipping: " + err.Error())
			}
		}
	}
	return nil
}

func handleProject(project map[string]any) error {
	projectId := project["id"].(string)
	projectName := project["name"].(string)
	log.Println("Project " + projectName)
	projectType := project["type"].(string)
	if projectType == "CATALOG" {
		return errors.New("Skipping catalog project " + projectId)
	}
	webModelerProjectId, stateErr := checkState(projectId, "project")
	if stateErr != nil {
		return stateErr
	}
	if webModelerProjectId == "" {
		projectName := project["name"].(string)
		webModelerProject, err := createWebModelerProject(projectName)
		if err != nil {
			return err
		}
		webModelerProjectId = webModelerProject["id"].(string)
		stateErr := setState(projectId, webModelerProjectId, "project")
		if stateErr != nil {
			return stateErr
		}
	}
	return handleFiles(projectId, webModelerProjectId)
}

func handleFiles(cawemoProjectId string, webModelerProjectId string) error {
	files, err := getCawemoFiles(cawemoProjectId)
	if err != nil {
		return err
	}
	for _, fileAny := range files {
		err := handleFile(fileAny.(map[string]any), cawemoProjectId, webModelerProjectId)
		if err != nil {
			log.Println(" -> Error " + err.Error())
		}
	}
	return nil
}

func handleFile(file map[string]any, cawemoProjectId string, webModelerProjectId string) error {
	simplePath := file["simplePath"].(string)
	log.Print("File " + simplePath)
	fileId := file["id"].(string)
	rawFileType := file["type"].(string)
	filetype, filetypeError := determineFileType(rawFileType)
	if filetypeError != nil {
		return filetypeError
	}
	webModelerFileId, stateErr := checkState(fileId, "file")
	if stateErr != nil {
		return stateErr
	}
	fileDetails, cawemoFileErr := getCawemoFile(fileId)
	if cawemoFileErr != nil {
		return cawemoFileErr
	}
	content := determineContent(fileDetails["content"].(string), filetype)
	revision := 0
	if webModelerFileId == "" {

		canonicalPath := file["canonicalPath"].([]any)
		parentId, folderErr := handleFolders(canonicalPath, webModelerProjectId)
		if folderErr != nil {
			return folderErr
		}
		// create file with milestones
		filename := file["name"].(string)
		webModelerFile, createFileErr := createWebModelerFile(filename, parentId, webModelerProjectId, content, filetype)
		if createFileErr != nil {
			return createFileErr
		}
		revision = int(webModelerFile["revision"].(float64))
		webModelerFileId = webModelerFile["id"].(string)
		stateErr := setState(fileId, webModelerFileId, "file")
		if stateErr != nil {
			return stateErr
		}
	} else {
		webModelerFile, err := getWebModelerFile(webModelerFileId)
		if err != nil {
			return err
		}
		metadata := webModelerFile["metadata"].(map[string]any)
		revision = int(metadata["revision"].(float64))
	}
	newRevision, handleMilestoneErr := handleMilestones(fileId, webModelerFileId, revision)
	if handleMilestoneErr != nil {
		return handleMilestoneErr
	}
	revision = newRevision
	_, updateWebModelerFileErr := updateWebModelerFile(webModelerFileId, content, revision)
	if updateWebModelerFileErr != nil {
		return updateWebModelerFileErr
	}
	log.Println(" -> done")
	return nil
}

func determineFileType(filetype string) (string, error) {
	if slices.Contains(webModelerFileTypes, strings.ToLower(filetype)) {
		return strings.ToLower(filetype), nil
	}
	return "", errors.New("Cannot handle file type " + filetype)
}

func determineContent(content string, filetype string) string {
	if filetype == "dmn" {
		return dmnFix.ReplaceAllString(content, "")
	}
	return content
}

func handleMilestones(cawemoFileId string, webModelerFileId string, revision int) (int, error) {
	milestones, err := getCawemoMilestones(cawemoFileId)
	if err != nil {
		return -1, err
	}
	newRevision := revision
	for _, milestone := range milestones {
		innerRevision, err := handleMilestone(webModelerFileId, milestone.(map[string]any), newRevision)
		if err != nil {
			return -1, err
		}
		newRevision = innerRevision
	}
	return newRevision, nil
}

func handleMilestone(webModelerFileId string, milestone map[string]any, revision int) (int, error) {
	milestoneId := milestone["id"].(string)
	webModelerMilestoneId, err := checkState(milestoneId, "milestone")
	if err != nil {
		return -1, err
	}
	newRevision := revision
	if webModelerMilestoneId == "" {
		milestoneName := milestone["name"].(string)
		milestoneDetails, milestoneError := getCawemoMilestone(milestoneId)
		if milestoneError != nil {
			return -1, milestoneError
		}
		content := milestoneDetails["content"].(string)
		updatedFile, updateFileError := updateWebModelerFile(webModelerFileId, content, revision)
		if updateFileError != nil {
			return -1, updateFileError
		}
		newRevision = int(updatedFile["revision"].(float64))
		webModelerMilestone, createMilestoneError := createWebModelerMilestone(milestoneName, webModelerFileId)
		if createMilestoneError != nil {
			return -1, createMilestoneError
		}
		webModelerMilestoneId := webModelerMilestone["id"].(string)
		stateError := setState(milestoneId, webModelerMilestoneId, "milestone")
		if stateError != nil {
			return -1, stateError
		}
	}
	return newRevision, nil
}

func handleFolders(folders []any, webModelerProjectId string) (string, error) {
	parentId := ""

	for _, folderAny := range folders {
		innerParentId, err := handleFolder(folderAny.(map[string]any), parentId, webModelerProjectId)
		if err != nil {
			return "", err
		}
		parentId = innerParentId
	}
	return parentId, nil
}

func handleFolder(folder map[string]any, parentId string, webModelerProjectId string) (string, error) {
	cawemoFolderId := folder["id"].(string)
	webModelerFolderId, err := checkState(cawemoFolderId, "folder")
	if err != nil {
		return "", err
	}
	if webModelerFolderId == "" {
		name := folder["name"].(string)
		webModelerFolder, err := createWebModelerFolder(name, webModelerProjectId, parentId)
		if err != nil {
			return "", err
		}
		webModelerFolderId = webModelerFolder["id"].(string)
		stateErr := setState(cawemoFolderId, webModelerFolderId, "folder")
		if stateErr != nil {
			return "", err
		}
	}
	return webModelerFolderId, nil
}

// functions to interact with state

func setState(cawemoId string, webModelerId string, entityType string) error {
	line := []string{cawemoId, webModelerId, entityType}
	if !checkFilePresent() {
		initFile()
	}
	f, err := os.OpenFile("id-mappings.csv", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Println(err)
		return err
	}
	defer f.Close()
	csvWriter := csv.NewWriter(f)

	if err := csvWriter.Write(line); err != nil {
		log.Println(err)
		return err
	}
	csvWriter.Flush()
	log.Println(entityType + " created: " + cawemoId + " -> " + webModelerId)
	return nil
}

func checkFilePresent() bool {
	_, err := os.Stat("id-mappings.csv")
	return err == nil
}

func initFile() error {
	line := []string{"CawemoId", "WebModelerId", "EntityType"}
	f, err := os.OpenFile("id-mappings.csv", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Println(err)
		return err
	}
	defer f.Close()
	csvWriter := csv.NewWriter(f)

	if err := csvWriter.Write(line); err != nil {
		log.Println(err)
	}
	csvWriter.Flush()
	return nil
}

func checkState(cawemoId string, entityType string) (string, error) {
	// Open the file in read-only mode
	if !checkFilePresent() {
		initFile()
	}
	file, err := os.Open("id-mappings.csv")
	if err != nil {
		return "", err
	}
	defer file.Close()
	csvReader := csv.NewReader(file)
	for {
		line, err := csvReader.Read()
		if err == io.EOF {
			break
		}
		if err != nil {
			log.Println(err)
			return "", err
		}
		if line[0] == cawemoId && line[2] == entityType {
			webModelerId := line[1]
			log.Println(entityType + " already exists: " + cawemoId + " -> " + webModelerId)
			return webModelerId, nil
		}
	}
	return "", nil
}

// functions to fetch from Cawemo

func getCawemoProjects() ([]any, error) {
	response, err := fetchFromCawemo("GET", "projects", nil)
	return response.([]any), err
}

func getCawemoFiles(projectId string) ([]any, error) {
	response, err := fetchFromCawemo("GET", "projects/"+projectId+"/files", nil)
	return response.([]any), err
}

func getCawemoFile(fileId string) (map[string]any, error) {
	response, err := fetchFromCawemo("GET", "files/"+fileId, nil)
	return response.(map[string]any), err
}

func getCawemoMilestones(fileId string) ([]any, error) {
	response, err := fetchFromCawemo("GET", "files/"+fileId+"/milestones", nil)
	return response.([]any), err
}

func getCawemoMilestone(milestoneId string) (map[string]any, error) {
	response, err := fetchFromCawemo("GET", "milestones/"+milestoneId, nil)
	return response.(map[string]any), err
}

// functions to fetch from Web Modeler

func createWebModelerProject(name string) (map[string]any, error) {
	body := map[string]string{
		"name": name,
	}
	response, err := fetchFromWebModeler("POST", "projects", createBody(body))
	return response.(map[string]any), err
}

func createWebModelerFolder(name string, projectId string, parentId string) (map[string]any, error) {
	body := map[string]string{
		"name": name,
	}
	if projectId != "" {
		body["projectId"] = projectId
	}
	if parentId != "" {
		body["parentId"] = parentId
	}
	response, err := fetchFromWebModeler("POST", "folders", createBody(body))
	return response.(map[string]any), err
}
func createWebModelerFile(name string, folderId string, projectId string, content string, fileType string) (map[string]any, error) {
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
	response, err := fetchFromWebModeler("POST", "files", createBody(body))
	if err != nil {
		return nil, err
	}
	return response.(map[string]any), nil
}

func updateWebModelerFile(fileId string, content string, revision int) (map[string]any, error) {
	body := map[string]any{
		"content":  content,
		"revision": revision,
	}
	response, err := fetchFromWebModeler("PATCH", "files/"+fileId, createBody(body))
	return response.(map[string]any), err
}

func createWebModelerMilestone(name string, fileId string) (map[string]any, error) {
	body := map[string]string{
		"name":   name,
		"fileId": fileId,
	}
	response, err := fetchFromWebModeler("POST", "milestones", createBody(body))
	return response.(map[string]any), err
}

func getWebModelerFile(fileId string) (map[string]any, error) {
	response, err := fetchFromWebModeler("GET", "files/"+fileId, nil)
	return response.(map[string]any), err
}

// generic cawemo and web modeler functions

func fetchFromCawemo(method string, context string, body io.Reader) (any, error) {
	req, _ := http.NewRequest(method, cawemoBaseUrl+"/api/v1/"+context, body)
	req.Header.Add("Authorization", "Basic "+basicAuth(cawemoUserId, cawemoApiKey))
	req.Header.Set("Content-Type", "application/json")
	return fetch(req)
}

func fetchFromWebModeler(method string, context string, body io.Reader) (any, error) {
	req, _ := http.NewRequest(method, modelerBaseUrl+"/api/v1/"+context, body)
	req.Header.Add("Authorization", "Bearer "+token)
	req.Header.Set("Content-Type", "application/json")
	return fetch(req)
}

func fetchWebModelerToken() error {
	body := map[string]string{
		"grant_type":    "client_credentials",
		"audience":      modelerTokenAudience,
		"client_id":     modelerClientId,
		"client_secret": modelerClientSecret,
	}
	req, _ := http.NewRequest("POST", modelerAuthUrl, createBody(body))
	contentType, err := determineContentType()
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", contentType)
	response, err := fetch(req)
	if err != nil {
		return err
	}
	token = response.(map[string]any)["access_token"].(string)
	return nil
}

// helper functions

func createBody(body any) io.Reader {
	marshalled, _ := json.Marshal(body)
	return bytes.NewReader(marshalled)
}

func determineContentType() (string, error) {
	if modelerCredentialsType == "json" {
		return "application/json", nil
	}
	return "", errors.New("could not determine content type, unknown credentials type " + modelerCredentialsType)
}

func basicAuth(username, password string) string {
	auth := username + ":" + password
	return base64.StdEncoding.EncodeToString([]byte(auth))
}

func fetch(req *http.Request) (any, error) {
	client := &http.Client{}
	response, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	if response.StatusCode > 299 {
		return nil, errors.New("response code is not 2xx")
	}
	return formatResponse(*response)
}

func formatResponse(response http.Response) (any, error) {
	responseData, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}
	var result any
	json.Unmarshal(responseData, &result)
	if logLevel == "debug" {
		formatted, _ := json.MarshalIndent(result, "", "  ")
		log.Println(string(formatted))
	}
	return result, nil
}
