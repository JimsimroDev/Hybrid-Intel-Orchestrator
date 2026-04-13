package com.jimsimrodev.orchestrator.module.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntelligenceState {
    private String pregunta;
    private String fuente;
    private String contexto;
    private String respuesta;
}
