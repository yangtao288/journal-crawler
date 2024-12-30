package com.etl.sample;

public class Affiliation {
    private String deptName;
    private String postalCode;
    private String city;

    public Affiliation(String deptName, String city, String postalCode) {
        this.deptName = deptName;
        this.city = city;
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return String.format("Affiliation{deptName='%s', city='%s', postalCode='%s'}",
                deptName, city, postalCode);
    }

    // Getters
    public String getDeptName() { return deptName; }
    public String getPostalCode() { return postalCode; }
    public String getCity() { return city; }
}