package com.jimsimrodev.orchestrator.module.infra.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {

   // @Value("${app.documents-path:/app/documents}")
    //private String pathDocument;

    private final EmbeddingStore<TextSegment> store;
    private final EmbeddingModel embeddingModel;

    public DocumentIngestionService(EmbeddingStore<TextSegment> store, EmbeddingModel embeddingModel){
        this.store = store;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() {
        // 1. Cargamos los documentos

        List<Document> documents = ClassPathDocumentLoader.loadDocuments("documents");

        // 2. Configuramos el splitter
        DocumentSplitter splitter = DocumentSplitters.recursive(1000, 200);

        // 3. Creamos el ingestor y ejecutamos
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel) // Usamos el modelo inyectado
                .embeddingStore(store)         // Usamos el store inyectado
                .build();

        ingestor.ingest(documents);
        System.out.println("=== Documentos indexados correctamente ===");
    }
}
