package org.example;

public class OpBillConfigBuilder {
    private PatientFlowHelper patientFlowHelper;

    public OpBillConfigBuilder setPatientFlowHelper(PatientFlowHelper patientFlowHelper) {
        this.patientFlowHelper = patientFlowHelper;
        return this;
    }

}