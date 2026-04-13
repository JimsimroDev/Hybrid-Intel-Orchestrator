package com.jimsimrodev.orchestrator.module.infra.adapter.ia;

import org.springframework.stereotype.Component;

import com.jimsimrodev.orchestrator.module.aplication.port.IntelligenceModelPort;

import dev.langchain4j.model.openai.OpenAiChatModel;

@Component
public class OpenAiAdapter implements IntelligenceModelPort {
    private final OpenAiChatModel chatModel;

    public OpenAiAdapter(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String classify(String pregunta) {
        String promt = String.format(
                """
                        Eres un clasificador. Dada la siguiente pregunta, decide si se debe
                        responder buscando en los DOCUMENTOS LOCALES (PDFs cargados) o
                        informaciones más generales en la WEB (internet).
                          Reglas:
                         - Responde SOLO con la palabra 'RAG' si la pregunta se refiere al contenido de documentos cargados
                         - Responde SOLO con la palabra 'Web' si la pregunta requiere información actualizada, de temas generales, de la web

                          Pregunta %s
                         Respuesta (RAG o Web):""",
                pregunta);

        return chatModel.chat(promt);
    }

    @Override
    public String respuestaFinal(String fuente, String contexto, String pregunta) {

        String promt = String.format(
                """
                        Eres un asistente experto. Genera una respuesta completa y bien
                        formateada en Markdown basándote en el contexto proporcionado.

                        Incluye: título (#), subtítulos (##), listas y negritas(*), Separadores visuales:
                         Puedes usar --- en una línea sola para crear una línea horizontal divisoria entre una respuesta y al final de la respuesta.
                         al final tambien agrega la fuente en negritas(*).
                        Responde siempre en español.

                        fuente: %s\n

                        Contexto: %s\n

                        Pregunta: %s\n

                        Respuesta en Markdown:""",
                fuente, contexto, pregunta);

        return chatModel.chat(promt);
    }
}
