
package br.com.cashhub.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaService {

    private final RestClient restClient;

    public OllamaService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String gerarResposta(String prompt) {

        var resposta = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "model", "llama3.2:3b",
                        "prompt", prompt,
                        "stream", false
                ))
                .retrieve()
                .body(Map.class);

        if (resposta == null || resposta.get("response") == null) {
            throw new IllegalStateException(
                    "O Ollama não retornou uma resposta válida."
            );
        }

        return resposta.get("response").toString();
    }
}