package com.alexander.diagrams.model;

import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class Table {

    private final String name;
    @Setter
    @Builder.Default
    private List<Column> columns = new LinkedList<>();
    @Setter
    @Builder.Default
    private List<ForeignKey> foreignKeys = new LinkedList<>();
    @Setter
    @Builder.Default
    private List<PrimaryKey> primaryKeys = new LinkedList<>();
}
