package org.example;

import org.testng.TestNG;

public class TestRunner {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{PatientRegisterToBillGenerate.class});
        testng.run();
    }
}
