package com.alexander.diagrams.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "")
public class UniqueConstraint {
    private final String indexName;
    private final List<String> columnNames;
}
