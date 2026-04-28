package com.jimsimrodev.orchestrator.module.infra.adapter.websearch;

import com.jimsimrodev.orchestrator.module.aplication.port.WebedgeBasePort;
import com.jimsimrodev.orchestrator.module.infra.adapter.websearch.dto.SerpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class WebSearchAdapter implements WebedgeBasePort {

    @Value("${app.service.fast-api}")
    private String fastApi;

    @Override
    public String busqueda(String pregunta) {

        StringBuilder contexto = new StringBuilder();

       WebClient client = WebClient.builder()
                .baseUrl("https://serpapi.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        SerpResponse response = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.json")
                        .queryParam("q",pregunta)
                        .queryParam("api_key",fastApi)
                        .build())
                .retrieve()
                .bodyToMono(SerpResponse.class)
                .block();

        response.organic_results().forEach(result -> {
            contexto.append("Title: ").append(result.title()).append("\n")
                    .append("Snippet: ").append(result.snippet()).append("\n")
                    .append("Link: ").append(result.link()).append("\n\n");
        });

        return contexto.toString();
    }
}
