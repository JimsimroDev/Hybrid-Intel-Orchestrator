package com.jimsimrodev.orchestrator.module.infra.adapter.websearch.dto;

import java.util.List;

public record SerpResponse(List<OrganicResult> organic_results) {
}
