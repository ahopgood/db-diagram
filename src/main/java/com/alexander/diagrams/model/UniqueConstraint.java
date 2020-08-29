package com.alexander.diagrams.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UniqueConstraint {
    private final String indexName;
    private final List<String> columnNames;
}
