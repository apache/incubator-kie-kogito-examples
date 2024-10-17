package org.kie.kogito.decisions.embedded;

public class LoanApplication {
    private String applicantId;
    private String explanation;
    private boolean approved;
       
    public LoanApplication() {
    }

    public LoanApplication(String applicantId) {
        this.applicantId = applicantId;
    }

    public LoanApplication(String applicantId, String explanation, boolean approved) {
        this.applicantId = applicantId;
        this.explanation = explanation;
        this.approved = approved;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    @Override
    public String toString() {
        return "LoanApplication [applicantId=" + applicantId + ", explanation=" + explanation + ", approved=" + approved
                + "]";
    }

    
}
