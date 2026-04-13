package com.jimsimrodev.orchestrator.module.infra.adapter.persistence;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.jimsimrodev.orchestrator.module.aplication.port.KnowledgeBasePort;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;

@Component
public class VectorStoreAdapter implements KnowledgeBasePort {

    private final EmbeddingStore<TextSegment> store;
    private final EmbeddingModel embeddingModel;

    // Spring inyecta automáticamente el almacén y el modelo de embeddings
    public VectorStoreAdapter(EmbeddingStore<TextSegment> store, EmbeddingModel embeddingModel) {
        this.store = store;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public String busqueda(String pregunta) {
        Embedding embedding = embeddingModel.embed(pregunta).content();

        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(embedding)
                .maxResults(4)
                .minScore(0.5)
                .build();

        EmbeddingSearchResult<TextSegment> result = store.search(searchRequest);

        return result.matches().stream().map(m -> m.embedded().text())
                .collect(Collectors.joining());
    }
}
