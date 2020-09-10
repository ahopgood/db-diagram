package com.alexander.diagrams.model;

import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PrimaryKey {
    @Builder.Default
    private final List<String> keyName = new LinkedList<>();
}
