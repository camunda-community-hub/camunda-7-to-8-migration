package org.camunda.community.migration.converter.webapp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.camunda.community.migration.converter.DiagramConverterResultDTO;
import org.springframework.stereotype.Component;

@Component
public class ExcelWriter {

  private static final String FILE_PATH_TEMPLATE = "/MigrationAnalyzerResultTemplate.xlsx";
  private static final String FILE_PATH_OUTPUT = "MigrationAnalyzerResult.xlsx";

  private static final String SHEET_NAME = "AnalysisResults";

  /** Method solely to test Excel behavior when filling the template */
  public static void main(String[] args) throws IOException {
    DiagramConverterResultDTO result1 =
        new DiagramConverterResultDTO(
            "file1.xml",
            "Element A",
            "123",
            "Type X",
            "High",
            "MSG001",
            "This is a test message",
            "http://example.com");

    DiagramConverterResultDTO result2 =
        new DiagramConverterResultDTO(
            "file2.xml",
            "Element B",
            "456",
            "Type Y",
            "Medium",
            "MSG002",
            "Another test message",
            "http://example.com");

    try (FileOutputStream fos = new FileOutputStream(FILE_PATH_OUTPUT)) {
      new ExcelWriter().writeResultsToExcel(Arrays.asList(result1, result2), fos);
    }
    System.out.println("Data successfully written to Excel!");
  }

  public void writeResultsToExcel(
      List<DiagramConverterResultDTO> results, OutputStream outputStream) {
    InputStream resourceAsStream = ExcelWriter.class.getResourceAsStream(FILE_PATH_TEMPLATE);
    XSSFWorkbook workbook = null;
    Sheet sheet = null;
    try {
      workbook = new XSSFWorkbook(resourceAsStream);
      sheet = workbook.getSheet(SHEET_NAME);
      if (sheet == null) {
        workbook.close();
        throw new IllegalArgumentException(
            "Could not find sheet '"
                + SHEET_NAME
                + "' within template Excel file loaded from classpath: '"
                + FILE_PATH_TEMPLATE
                + "'");
      }
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Could not load Excel template from classpath using '" + FILE_PATH_TEMPLATE + "'", e);
    }

    int rowIndex = sheet.getLastRowNum() + 1; // Start writing after the last row

    for (DiagramConverterResultDTO result : results) {
      Row row = sheet.createRow(rowIndex++);

      row.createCell(0).setCellValue(result.getFilename());
      row.createCell(1).setCellValue(result.getElementName());
      row.createCell(2).setCellValue(result.getElementId());
      row.createCell(3).setCellValue(result.getElementType());
      row.createCell(4).setCellValue(result.getSeverity());
      row.createCell(5).setCellValue(result.getMessageId());
      row.createCell(6).setCellValue(result.getMessage());
      row.createCell(7).setCellValue(result.getLink());
    }

    try {
      workbook.write(outputStream);
      workbook.close();
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Could not create updated Excel file on given output stream.", e);
    }
  }
}
