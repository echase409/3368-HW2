package sample;

import java.util.UUID;
import java.util.Random;

public class Student {
    private String fName, lName;
    private UUID id;
    private int age;
    private String major;
    private float gpa;

    @Override
    public String toString()
    {
        return this.lName + ", " + this.fName;
    }

    // Setters
    public void setFName(String firstName) {
        fName = firstName;
    }

    public void setLName(String lastName) {
        lName = lastName;
    }

    public void setId(UUID ID) {
        id = ID;
    }

    public void setAge(int Age) {
        age = Age;
    }

    public void setMajor(String Major) {
        major = Major;
    }

    public void setGpa(float GPA) {
        gpa = GPA;
    }

    // Getters
    public String getFName() {
        return fName;
    }

    public String getLName() {
        return lName;
    }

    public UUID getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getMajor() {
        return major;
    }

    public float getGpa() {
        return gpa;
    }
}