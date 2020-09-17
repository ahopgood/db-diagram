package com.alexander.diagrams.source;

import java.util.List;

public class DatabaseSource implements Source {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public List<String> next() {
        return null;
    }
}
