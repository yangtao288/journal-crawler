package com.etl.sample;

public class Author {
    private String name;
    private String deptName;

    public Author(String name, String deptName) {
        this.name = name;
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return String.format("Author{name='%s', deptName='%s'}", name, deptName);
    }

    // Getters
    public String getName() { return name; }
    public String getDeptName() { return deptName; }
}