package com.alexander.diagrams.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Column {

    private final String name;
    private final String type;
    private final String scale;
    private final boolean notNull;
    private final boolean notDefault;
    private final boolean primary;
    private final boolean foreign;
    private final boolean autoIncrement;
    private final String defaultValue;
}
