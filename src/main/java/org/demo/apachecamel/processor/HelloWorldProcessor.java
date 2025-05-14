package org.demo.apachecamel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.HashMap;

public class HelloWorldProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        String name = exchange.getIn().getBody(String.class);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }

        HashMap<String, String> response = new HashMap<>();
        response.put("hello", name);
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("message", "Bienvenue dans Apache Camel!");

        exchange.getMessage().setBody(response);
    }
}
