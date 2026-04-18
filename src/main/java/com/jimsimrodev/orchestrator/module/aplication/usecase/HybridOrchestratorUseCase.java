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
        var fuente = intelligenceModelPort.classify(questionRequestDTO.pregunta().trim().toUpperCase());

        var contexto = "";

         if (fuente.contains("RAG")) {
        contexto = knowledgeBasePort.busqueda(questionRequestDTO.pregunta());
         } else {
         contexto = webedgeBasePort.busqueda(questionRequestDTO.pregunta());
     }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy - HH:mm:ss");

        String createdAt = LocalDateTime.now().format(formatter);
        String respuestaFinal = intelligenceModelPort.respuestaFinal(fuente, contexto, questionRequestDTO.pregunta(), createdAt);

        Path ruta = Path.of(System.getProperty("user.dir"), "resultado_agente.md");


       // String contenido = System.lineSeparator() + "## Esta respuesta fue creada el " + createdAt + System.lineSeparator()
        //        + respuestaFinal + System.lineSeparator() + fuente + System.lineSeparator();

        try {
            Files.writeString(ruta, respuestaFinal, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new AnswerResponseDTO(fuente, respuestaFinal);
    }
}
