package com.alexander.diagrams.db;

import com.alexander.diagrams.model.Column;
import com.alexander.diagrams.model.ForeignKey;
import com.alexander.diagrams.model.PrimaryKey;
import com.alexander.diagrams.model.Table;
import com.alexander.diagrams.model.UniqueConstraint;

public interface DatabaseSyntaxParser {
    Table toTable(String line);

    Column toColumn(String line);

    PrimaryKey toPrimaryKey(String line);

    ForeignKey toForeignKey(String line);

    UniqueConstraint toUniqueConstraint(String line);
}
