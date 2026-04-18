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
                Actúa como un enrutador inteligente de consultas para un sistema híbrido.
                Tu tarea es analizar la intención del usuario y clasificar la fuente de información más adecuada.
               
                FUENTES DISPONIBLES:
                - 'RAG': Selecciona esta opción si la pregunta se refiere a documentos específicos del usuario, archivos PDF cargados, manuales internos, notas de clase o cualquier dato que parezca ser contenido privado/local.
                - 'Web': Selecciona esta opción si la pregunta es sobre cultura general, noticias de actualidad, ayuda con código (StackOverflow/GitHub), conceptos científicos generales o cualquier información que se encuentre en internet.
               
                REGLAS DE SALIDA:
                1. Responde ÚNICAMENTE con la palabra 'RAG' o 'Web'.
                2. No incluyas puntos finales, ni explicaciones, ni espacios adicionales.
                3. Si la pregunta es ambigua pero menciona "el documento", "el taller" o "mi archivo", prioriza 'RAG'.
               
                PREGUNTA DEL USUARIO:
                "%s"
               
                CLASIFICACIÓN:""",
                pregunta);

        return chatModel.chat(promt);
    }

    @Override
    public String respuestaFinal(String fuente, String contexto, String pregunta, String createdAt) {

        String promt = String.format(
                """
Actúa como un redactor técnico experto en Markdown. Tu objetivo es transformar el contexto proporcionado en una respuesta educativa, clara y profesional.

            ESTRUCTURA OBLIGATORIA DEL ARCHIVO (.md):

            1. ENCABEZADO DE METADATOS:
               - Inicia el documento con: `> **Fecha de creación:** %4$s`
               - Añade una línea horizontal `---` inmediatamente después.

            2. CUERPO DE LA RESPUESTA:
               - Título principal (#) que resuma la respuesta.
               - Introducción breve.
               - Usa subtítulos (##) para organizar los puntos clave.
               - Utiliza listas con viñetas (-) o numeradas para procesos.
               - Aplica negritas (**) para resaltar términos técnicos o conceptos vitales.
               - Si incluyes fórmulas o conceptos matemáticos, asegúrate de que se vean claros.

            3. CIERRE Y FUENTE:
               - Añade otra línea horizontal `---` al finalizar el contenido.
               - Finaliza el documento con: `**Fuente de información:** %1$s`

            REGLAS CRÍTICAS:
            - Idioma: Español.
            - Tono: Profesional y experto.
            - No inventes información fuera del contexto proporcionado.
            - No incluyes bloques de código de "Markdown" (```markdown), entrega el texto plano en formato Markdown directamente.

            DATOS PARA PROCESAR:
            Contexto: %2$s
            Pregunta: %3$s

            Respuesta en Markdown:
            """,
           fuente, contexto, pregunta, createdAt);

        return chatModel.chat(promt);
    }
}
