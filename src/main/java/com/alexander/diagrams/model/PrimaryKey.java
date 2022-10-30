package com.alexander.diagrams.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@SuppressFBWarnings(value = {"UPM_UNCALLED_PRIVATE_METHOD","EI_EXPOSE_REP2","EI_EXPOSE_REP"},
    justification = "Lombok default method")
public class PrimaryKey {
    @Builder.Default
    private final List<String> keyName = new LinkedList<>();
}
