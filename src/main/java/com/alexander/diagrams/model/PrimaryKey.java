package com.alexander.diagrams.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PrimaryKey {
    private final String keyName;
}
