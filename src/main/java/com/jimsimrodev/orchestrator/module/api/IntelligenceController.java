package com.jimsimrodev.orchestrator.module.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimsimrodev.orchestrator.module.aplication.dto.AnswerResponseDTO;
import com.jimsimrodev.orchestrator.module.aplication.dto.QuestionRequestDTO;
import com.jimsimrodev.orchestrator.module.aplication.usecase.HybridOrchestratorUseCase;

@RestController
@RequestMapping("/assistant")
public class IntelligenceController {
    private final HybridOrchestratorUseCase hybridOrchestratorUseCase;

    public IntelligenceController(HybridOrchestratorUseCase hybridOrchestratorUseCase) {
        this.hybridOrchestratorUseCase = hybridOrchestratorUseCase;
    }

    @PostMapping("/ask")
    public ResponseEntity<AnswerResponseDTO> askQuestion(@RequestBody QuestionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(hybridOrchestratorUseCase.execute(request));
    }

}
