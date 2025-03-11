package org.camunda.community.migration.converter;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.xml.ModelInstance;

public enum DiagramType {
  BPMN(List.of(".bpmn", ".bpmn20.xml"), "application/bpmn+xml", Bpmn::readModelFromStream),
  DMN(List.of(".dmn", ".dmn11.xml"), "application/dmn+xml", Dmn::readModelFromStream),
  ;

  private final List<String> fileEndings;
  private final String contentType;
  private final Function<InputStream, ModelInstance> reader;

  DiagramType(
      List<String> fileEndings, String contentType, Function<InputStream, ModelInstance> reader) {
    this.fileEndings = fileEndings;
    this.contentType = contentType;
    this.reader = reader;
  }

  public static DiagramType fromFileName(String fileName) {
    for (DiagramType d : DiagramType.values()) {
      for (String fileEnding : d.getFileEndings()) {
        if (fileName.endsWith(fileEnding)) {
          return d;
        }
      }
    }
    throw new IllegalArgumentException("No matching file ending found for " + fileName);
  }

  public static boolean isValidFile(String fileName) {
    for (DiagramType d : DiagramType.values()) {
      for (String fileEnding : d.getFileEndings()) {
        if (fileName.endsWith(fileEnding)) {
          return true;
        }
      }
    }
    return false;
  }

  public List<String> getFileEndings() {
    return fileEndings;
  }

  public String getContentType() {
    return contentType;
  }

  public ModelInstance readDiagram(InputStream inputStream) {
    return reader.apply(inputStream);
  }
}
