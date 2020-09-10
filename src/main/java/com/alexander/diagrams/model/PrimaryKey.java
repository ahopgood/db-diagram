package com.alexander.diagrams.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD", justification = "Lombok default method")
public class PrimaryKey {
    @Builder.Default
    private final List<String> keyName = new LinkedList<>();
}
