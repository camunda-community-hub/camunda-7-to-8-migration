package org.camunda.community.migration.examples.worker;

import org.camunda.community.migration.adapter.EnableCamunda7Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCamunda7Adapter
public class C7WorkerWithListeners {
    public static Logger logger = LoggerFactory.getLogger(C7WorkerWithListeners.class);

    public static void main(String[] args) {
        SpringApplication.run(C7WorkerWithListeners.class);
    }
}
