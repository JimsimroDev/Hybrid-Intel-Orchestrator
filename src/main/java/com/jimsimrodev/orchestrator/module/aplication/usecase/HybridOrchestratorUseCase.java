package com.jimsimrodev.orchestrator.module.aplication.usecase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.jimsimrodev.orchestrator.module.aplication.port.WebedgeBasePort;
import org.springframework.stereotype.Service;

import com.jimsimrodev.orchestrator.module.aplication.dto.AnswerResponseDTO;
import com.jimsimrodev.orchestrator.module.aplication.dto.QuestionRequestDTO;
import com.jimsimrodev.orchestrator.module.aplication.port.AskHybridQuestionPort;
import com.jimsimrodev.orchestrator.module.aplication.port.IntelligenceModelPort;
import com.jimsimrodev.orchestrator.module.aplication.port.KnowledgeBasePort;

@Service
public class HybridOrchestratorUseCase implements AskHybridQuestionPort {
    private final IntelligenceModelPort intelligenceModelPort;
    private final KnowledgeBasePort knowledgeBasePort;
    private final WebedgeBasePort webedgeBasePort;

    public HybridOrchestratorUseCase(IntelligenceModelPort intelligenceModelPort,
            KnowledgeBasePort knowledgeBasePort, WebedgeBasePort webedgeBasePort) {

        this.intelligenceModelPort = intelligenceModelPort;
        this.knowledgeBasePort = knowledgeBasePort;
        this.webedgeBasePort = webedgeBasePort;
    }

    @Override
    public AnswerResponseDTO execute(QuestionRequestDTO questionRequestDTO) {
        String pregunta = questionRequestDTO.pregunta();

        // RAG-first routing:
        // 1) Always try the vector store first.
        // 2) If RAG yields no usable context, fall back to Web search.
        // 3) If both fail, keep the original classifier decision for reporting.
        String contextoRag = "";
        try {
            contextoRag = knowledgeBasePort.busqueda(pregunta);
        } catch (RuntimeException ignored) {
            // Keep routing resilient; web fallback will handle issues.
        }

        boolean ragTieneContexto = contextoRag != null && !contextoRag.isBlank();

        String fuente;
        String contexto;

        if (ragTieneContexto) {
            fuente = "RAG";
            contexto = contextoRag;
        } else {
            fuente = "Web";
            contexto = webedgeBasePort.busqueda(pregunta);

            // If web also returns nothing, report the classifier decision (best-effort),
            // but still respond with whatever context we have.
            if (contexto == null || contexto.isBlank()) {
                fuente = intelligenceModelPort.classify(pregunta);
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy - HH:mm:ss");

        String createdAt = LocalDateTime.now().format(formatter);
        String respuestaFinal = intelligenceModelPort.respuestaFinal(fuente, contexto, pregunta, createdAt);

        Path ruta = Path.of(System.getProperty("user.dir"), "resultado_agente.md");

        try {
            Files.writeString(ruta, respuestaFinal, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new AnswerResponseDTO(fuente, respuestaFinal);
    }
}
