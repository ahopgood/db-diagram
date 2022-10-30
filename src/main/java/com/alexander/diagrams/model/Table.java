package com.alexander.diagrams.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@SuppressFBWarnings(value = {"UPM_UNCALLED_PRIVATE_METHOD","EI_EXPOSE_REP2"}, justification = "Lombok default method")
public class Table {

    private final String name;
    @Setter
    @Builder.Default
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "")
    private List<Column> columns = new LinkedList<>();
    @Setter
    @Builder.Default
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "")
    private List<ForeignKey> foreignKeys = new LinkedList<>();
    @Setter
    @Builder.Default
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "")
    private List<PrimaryKey> primaryKeys = new LinkedList<>();
}
