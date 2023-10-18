package com.jumani.rutaseg.domain;

public class Sort {
    private String field;
    private boolean ascending;

    public Sort(String field, boolean ascending) {
        this.field = field;
        this.ascending = ascending;
    }

    public String getField() {
        return field;
    }

    public boolean isAscending() {
        return ascending;
    }
}