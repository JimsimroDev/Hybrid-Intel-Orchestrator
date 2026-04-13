package com.jimsimrodev.orchestrator.module.aplication.port;

import com.jimsimrodev.orchestrator.module.aplication.dto.AnswerResponseDTO;
import com.jimsimrodev.orchestrator.module.aplication.dto.QuestionRequestDTO;

public interface AskHybridQuestionPort {
    AnswerResponseDTO execute(QuestionRequestDTO questionRequestDTO);
}
