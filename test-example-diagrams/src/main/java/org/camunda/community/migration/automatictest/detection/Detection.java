package org.camunda.community.migration.automatictest.detection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.camunda.community.migration.automatictest.testpkg.TestPkg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class search recursively in all folder to detect multiple tests */
public class Detection {
  static Logger logger = LoggerFactory.getLogger(Detection.class);

  File rootFolder;

  public Detection(File rootFolder) {
    this.rootFolder = rootFolder;
  }

  public List<TestPkg> detectTestPackage() {
    return detectRecursiveTestPackage(rootFolder);
  }

  private List<TestPkg> detectRecursiveTestPackage(File folder) {
    List<TestPkg> listPkg = new ArrayList<>();
    for (File fileInFolder : folder.listFiles()) {
      if (fileInFolder.isDirectory()) listPkg.addAll(detectRecursiveTestPackage(fileInFolder));
      else if (fileInFolder.isFile())
        // this folder is maybe a TestPackage
        try {
          TestPkg testPkg = TestPkg.createForFile(fileInFolder);
          if (testPkg != null) listPkg.add(testPkg);
        } catch (Exception e) {
          logger.info("Detection failed " + e.getMessage());
        }
    }
    return listPkg;
  }
}
