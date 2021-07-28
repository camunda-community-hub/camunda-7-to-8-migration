package io.berndruecker.converter.example;

import org.springframework.stereotype.Component;

@Component
public class SampleBean {

    public void someMethod(String text) {
        System.out.println("SampleBean.someMethod('" + text + "')");
    }

}
