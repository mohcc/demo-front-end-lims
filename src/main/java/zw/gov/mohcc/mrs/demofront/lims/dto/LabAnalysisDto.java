package zw.gov.mohcc.mrs.demofront.lims.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.gov.mohcc.mrs.fhir.lims.entities.AnalysisService;
import zw.gov.mohcc.mrs.fhir.lims.entities.Instrument;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;
import zw.gov.mohcc.mrs.fhir.lims.entities.Method;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabAnalysisDto {

    @NotNull
    private AnalysisService analysis;
    @NotNull
    private String resultValue;
    private LocalDate dueDate;
    @NotNull
    private Instrument instrument;
    @NotNull
    private Method method;
    @NotNull
    private LabContact analyst;    
    private LabContact submitter;
    private LabContact verifier; //Confirm this!
    private Boolean critical;
    private String interpretationText;

}
