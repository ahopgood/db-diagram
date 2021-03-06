package com.alexander.diagrams.diagram;

import com.alexander.diagrams.model.Table;
import java.util.List;

public interface DiagramProducer {

    void generateDiagram(List<Table> tables);
}
