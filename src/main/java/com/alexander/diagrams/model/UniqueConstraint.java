package com.alexander.diagrams.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UniqueConstraint {
    private final String indexName;
    private final List<String> columnNames;
}
