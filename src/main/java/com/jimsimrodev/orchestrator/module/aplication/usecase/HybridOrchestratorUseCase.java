package com.jimsimrodev.orchestrator.module.aplication.usecase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public HybridOrchestratorUseCase(IntelligenceModelPort intelligenceModelPort,
            KnowledgeBasePort knowledgeBasePort) {

        this.intelligenceModelPort = intelligenceModelPort;
        this.knowledgeBasePort = knowledgeBasePort;
    }

    @Override
    public AnswerResponseDTO execute(QuestionRequestDTO questionRequestDTO) {
        var fuente = intelligenceModelPort.classify(questionRequestDTO.pregunta());

        var contexto = "";

        // if (fuente.contains("RAG")) {
        contexto = knowledgeBasePort.busqueda(questionRequestDTO.pregunta());
        // } else {
        /// contexto = "Se deber ir al nodo web";
        // }

        String respuestaFinal = intelligenceModelPort.respuestaFinal(fuente, contexto, questionRequestDTO.pregunta());

        Path ruta = Path.of(System.getProperty("user.dir"), "resultado_agente.md");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy - HH:mm:ss");

        String time = LocalDateTime.now().format(formatter);

        String contenido = System.lineSeparator() + "## Esta respuesta fue creada el " + time + System.lineSeparator()
                + respuestaFinal + System.lineSeparator();

        try {
            Files.writeString(ruta, contenido, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new AnswerResponseDTO(fuente, respuestaFinal);
    }
}
