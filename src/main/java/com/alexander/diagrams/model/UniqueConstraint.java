package com.alexander.diagrams.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UniqueConstraint {
    private final String indexName;
    private final String[] columnNames;
}
