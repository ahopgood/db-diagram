package com.alexander.diagrams.plantuml;

import net.sourceforge.plantuml.FileFormat;

public enum OutputFileFormat {

    SVG {
        @Override
        FileFormat getPlantUmlFormat() {
            return FileFormat.SVG;
        }
    }, PNG {
        @Override
        FileFormat getPlantUmlFormat() {
            return FileFormat.PNG;
        }
    };

    abstract FileFormat getPlantUmlFormat();

}
