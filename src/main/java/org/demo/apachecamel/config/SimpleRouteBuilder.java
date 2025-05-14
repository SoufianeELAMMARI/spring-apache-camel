package org.demo.apachecamel.config;


import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.demo.apachecamel.processor.HelloWorldProcessor;
import org.demo.apachecamel.processor.HelloWorldV2Processor;
import org.springframework.stereotype.Component;

@Component
class SimpleRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        // Gestion des exceptions globales
        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Erreur rencontrée : ${exception.message}")
                .handled(true)
                .setBody(constant("Une erreur est survenue, veuillez réessayer plus tard."))
                .end();

        // Route principale Hello world
        from("direct:getHelloWorld")
                .routeId("getHelloWorld")
                .log(LoggingLevel.INFO, "Appel de getHelloWorld avec ${body}")
                .process(new HelloWorldProcessor())
                .end();

        from("direct:getHelloWorldV2")
                .routeId("getHelloWorldV2")
                .log(LoggingLevel.INFO, "Appel de getHelloWorld avec ${body}")
                .process(new HelloWorldV2Processor())
                .to("direct:logResponse")
                .end();


        // Cas d'utilisation : écrire le résultat dans un fichier
        from("direct:logResponse")
                .routeId("logResponseToFile")
                .log(LoggingLevel.INFO, "Enregistrement de la réponse dans un fichier")
                .to("file:///C:/Users/047861/Desktop/output_camel?fileName=response.txt&autoCreate=true")
                .end();


        // Cas d'utilisation : envoyer la réponse à un service REST POST  externe
        from("direct:sendToExternalService")
                .routeId("sendToExternalService")
                .log(LoggingLevel.INFO, "Envoi d'une requête GET à l'API externe")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .to("https://api.restful-api.dev/objects/7")
                .log(LoggingLevel.INFO, "Réponse reçue : ${body}")
                .to("direct:logResponse")
                .end();

        // Cas d'utilisation : envoyer la réponse à un service REST POST  externe
        from("direct:sendToExternalServicePost")
                .routeId("sendToExternalServicePost")
                .log(LoggingLevel.INFO, "Envoi de la réponse à un service REST externe")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("http://external-service/api/receive")
                .end();


       from("kafka:customer-updates")
                .unmarshal().json()
                .transform().simple("resource:classpath:sms-transform.txt")
                .to("jdbc:dataSource?statement=INSERT INTO sms_requests (phone_number, message) VALUES (:#phoneNumber, :#message)")
                .to("twilio:{{body[phoneNumber]}}?message={{body[message]}}");
    }
}
