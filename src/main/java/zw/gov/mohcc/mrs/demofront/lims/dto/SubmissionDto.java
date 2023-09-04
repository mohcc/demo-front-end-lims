package zw.gov.mohcc.mrs.demofront.lims.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;

@Data
public class SubmissionDto {
    
    @NotNull
    private LabContact submitter;
    @NotNull
    private LabContact verifier;
    @NotNull
    private LocalDate dateSubmitted;
    @NotNull
    private LocalDate dateVerified;
    private String reviewState;
}
