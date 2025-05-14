package org.demo.apachecamel.controllers;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class CamelController {

    @Autowired
    private ProducerTemplate template;

    @GetMapping("/v1/hello")
    public HashMap<String, Object> getHello(@RequestParam("name") String name) {
        Object response = template.requestBody("direct:getHelloWorld", name);

        if (response instanceof HashMap) {
            // cast to HashMap<String, Object>
            return (HashMap<String, Object>) response;
        } else {
            throw new IllegalStateException("Unexpected response type, expected a HashMap<String, Object>");
        }
    }

    @GetMapping("/v2/hello")
    public void  getHelloAndLogFile(@RequestParam("name") String name) {
            template.requestBody("direct:getHelloWorldV2", name);
    }

    @GetMapping("/v3/hello")
    public void  getHelloAndLogFile() {
        template.requestBody("direct:sendToExternalService");
    }

}

