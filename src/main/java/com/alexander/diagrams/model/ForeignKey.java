package com.alexander.diagrams.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ForeignKey {

    private String foreignKeyName;
    private String sourceTable;
    private String sourceColumn;
}
