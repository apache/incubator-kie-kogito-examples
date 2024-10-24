package org.kie.kogito.rules.embedded;

public class Applicant {
    private String id;
    private int age;

    public Applicant() {
    }

    public Applicant(String id, int age) {
        this.id = id;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
