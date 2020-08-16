package com.alexander.diagrams.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
public class Table {

    private final String name;
    @Setter
    private List<Column> columns;
    @Setter
    private List<ForeignKey> foreignKeys;
    @Setter
    private List<PrimaryKey> primaryKeys;
}
