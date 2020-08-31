package com.alexander.diagrams.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
