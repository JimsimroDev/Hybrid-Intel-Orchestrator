package com.jimsimrodev.orchestrator.module.aplication.port;

public interface IntelligenceModelPort {

    public String classify(String pregunta);

    public String respuestaFinal(String fuente, String contexto, String pregunta,String createdAt);
}
