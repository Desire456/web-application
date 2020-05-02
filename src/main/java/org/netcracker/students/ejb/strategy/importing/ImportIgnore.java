package org.netcracker.students.ejb.strategy.importing;

import java.util.List;

public class ImportIgnore implements ImportStrategy {
    @Override
    public List<Object> importData(String xml) {
        return null;
    }
}
