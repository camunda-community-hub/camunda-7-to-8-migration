package org.camunda.community.migration.converter.webapp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.community.migration.converter.DiagramCheckResult;
import org.camunda.community.migration.converter.DiagramConverterResultDTO;
import org.camunda.community.migration.converter.DiagramType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ConverterController {
  private static final Logger LOG = LoggerFactory.getLogger(ConverterController.class);
  private final DiagramConverterService bpmnConverter;
  private final BuildProperties buildProperties;
  private final ExcelWriter excelWriter;

  @Autowired
  public ConverterController(
      DiagramConverterService bpmnConverter,
      BuildProperties buildProperties,
      ExcelWriter excelWriter) {
    this.bpmnConverter = bpmnConverter;
    this.buildProperties = buildProperties;
    this.excelWriter = excelWriter;
  }

  /**
   * POST a list of BPMN or DMN models for analyzing tasks. Can be returned in various formats: -
   * JSON representation of a {@link List} of {@link DiagramCheckResult}s - Excel file, filled with
   * result data - CSV file containing result data
   */
  @PostMapping(
      value = "/check",
      produces = {
        MediaType.APPLICATION_JSON_VALUE,
        "text/csv",
        "application/excel",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
      },
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> check(
      @RequestParam("file") List<MultipartFile> diagramFiles,
      @RequestParam(value = "defaultJobType", required = false) String defaultJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion,
      @RequestParam(value = "defaultJobTypeEnabled", required = false, defaultValue = "true")
          Boolean defaultJobTypeEnabled,
      @RequestHeader(HttpHeaders.ACCEPT) String[] contentType) {

    ArrayList<DiagramCheckResult> resultList = new ArrayList<DiagramCheckResult>();

    // Check all files
    for (Iterator diagramFilesIterator = diagramFiles.iterator();
        diagramFilesIterator.hasNext(); ) {
      MultipartFile diagramFile = (MultipartFile) diagramFilesIterator.next();
      DiagramType diagramType = determineDiagramType(diagramFile);

      try (InputStream in = diagramFile.getInputStream()) {
        ModelInstance modelInstance = diagramType.readDiagram(in);

        DiagramCheckResult diagramCheckResult =
            bpmnConverter.check(
                diagramFile.getOriginalFilename(),
                modelInstance,
                defaultJobType,
                platformVersion,
                defaultJobTypeEnabled);
        resultList.add(diagramCheckResult);
      } catch (IOException e) {
        LOG.error("Error while reading input stream of diagram file", e);
        return ResponseEntity.badRequest().body(e.getMessage());
      }
    }

    // return response depending on the requested format
    if (jsonRequested(contentType)) { // JSON
      return ResponseEntity.ok(resultList);

    } else if (excelRequested(contentType)) { // EXCEL

      List<DiagramConverterResultDTO> data = bpmnConverter.createLineItemDTOList(resultList);

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      excelWriter.writeResultsToExcel(data, os);
      Resource file = new ByteArrayResource(os.toByteArray());

      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"migrationAnalyzer.xlsx\"")
          .header(
              HttpHeaders.CONTENT_TYPE,
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(file);

    } else if (csvRequested(contentType)) { // CSV

      StringWriter sw = new StringWriter();
      bpmnConverter.writeCsvFile(resultList, sw);
      Resource file = new ByteArrayResource(sw.toString().getBytes());
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"migrationAnalyzer.csv\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(file);

    } else {
      String errorMessage = "Invalid content type '" + String.join("', '", contentType) + "'";
      LOG.error("{}", errorMessage);
      return ResponseEntity.badRequest().body(errorMessage);
    }
  }

  private boolean csvRequested(String[] contentType) {
    return contentType != null && Arrays.asList(contentType).contains("text/csv");
  }

  private boolean jsonRequested(String[] contentType) {
    return contentType == null
        || contentType.length == 0
        || Arrays.asList(contentType).contains(MediaType.APPLICATION_JSON_VALUE);
  }

  private boolean excelRequested(String[] contentType) {
    return contentType != null && Arrays.asList(contentType).contains("application/excel")
        || Arrays.asList(contentType).contains("application/vnd.ms-excel")
        || Arrays.asList(contentType)
            .contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
  }

  /** POST method to actually convert a BPMN or DMN model. */
  @PostMapping(
      value = "/convert",
      produces = {"application/bpmn+xml", "application/dmn+xml"},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> getFile(
      @RequestParam("file") MultipartFile diagramFile,
      @RequestParam(value = "appendDocumentation", required = false, defaultValue = "false")
          Boolean appendDocumentation,
      @RequestParam(value = "defaultJobType", required = false) String defaultJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion,
      @RequestParam(value = "defaultJobTypeEnabled", required = false, defaultValue = "true")
          Boolean defaultJobTypeEnabled) {

    DiagramType diagramType = determineDiagramType(diagramFile);
    try (InputStream in = diagramFile.getInputStream()) {
      ModelInstance modelInstance = diagramType.readDiagram(in);
      bpmnConverter.convert(
          modelInstance,
          appendDocumentation,
          defaultJobType,
          platformVersion,
          defaultJobTypeEnabled);
      String xml = bpmnConverter.printXml(modelInstance.getDocument(), true);
      Resource file = new ByteArrayResource(xml.getBytes());
      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"converted-c8-" + diagramFile.getOriginalFilename() + "\"")
          .header(HttpHeaders.CONTENT_TYPE, diagramType.getContentType())
          .body(file);
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  /**
   * POST method to convert a list of BPMN or DMN models in one go. Returns a ZIP file with all the
   * contents
   */
  @PostMapping(
      value = "/convertBatch",
      produces = {"application/zip"},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> convertBatch(
      @RequestParam("file") List<MultipartFile> diagramFiles,
      @RequestParam(value = "appendDocumentation", required = false, defaultValue = "false")
          Boolean appendDocumentation,
      @RequestParam(value = "defaultJobType", required = false) String defaultJobType,
      @RequestParam(value = "platformVersion", required = false) String platformVersion,
      @RequestParam(value = "defaultJobTypeEnabled", required = false, defaultValue = "true")
          Boolean defaultJobTypeEnabled) {

    HashMap<String, Resource> resultList = new HashMap<String, Resource>();

    // Check all files
    for (Iterator diagramFilesIterator = diagramFiles.iterator();
        diagramFilesIterator.hasNext(); ) {
      MultipartFile diagramFile = (MultipartFile) diagramFilesIterator.next();

      DiagramType diagramType = determineDiagramType(diagramFile);
      try (InputStream in = diagramFile.getInputStream()) {

        ModelInstance modelInstance = diagramType.readDiagram(in);
        bpmnConverter.convert(
            modelInstance,
            appendDocumentation,
            defaultJobType,
            platformVersion,
            defaultJobTypeEnabled);
        String xml = bpmnConverter.printXml(modelInstance.getDocument(), true);
        Resource file = new ByteArrayResource(xml.getBytes());
        resultList.put("converted-c8-" + diagramFile.getOriginalFilename(), file);

      } catch (IOException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
      } catch (Exception e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
      }
    }

    // Creating byteArray stream, make it bufferable and passing this buffer to ZipOutputStream
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {
      for (Entry<String, Resource> file : resultList.entrySet()) {
        zipOutputStream.putNextEntry(new ZipEntry(file.getKey()));
        zipOutputStream.write(file.getValue().getContentAsByteArray());
        zipOutputStream.closeEntry();
      }
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"converted-diagrams.zip\"")
        .header(HttpHeaders.CONTENT_TYPE, "application/zip")
        .body(byteArrayOutputStream.toByteArray());
  }

  /**
   * GET method to retrieve the current version of the diagram converter tool
   *
   * @return
   */
  @GetMapping(value = "/version", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> getVersion() {
    String implementationVersion = buildProperties.getVersion();
    LOG.debug("Version: {}", implementationVersion);
    return ResponseEntity.ok().body(implementationVersion);
  }

  private DiagramType determineDiagramType(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      throw new IllegalArgumentException("No file provided");
    }
    return DiagramType.fromFileName(originalFilename);
  }
}
