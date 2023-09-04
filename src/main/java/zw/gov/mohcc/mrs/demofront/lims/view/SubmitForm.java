package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import java.util.List;
import zw.gov.mohcc.mrs.demofront.lims.dto.SubmissionDto;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;

public class SubmitForm extends FormLayout{

    private final ComboBox<LabContact> submitter = new ComboBox<>("Submitter");
    private final ComboBox<LabContact> verifier = new ComboBox<>("Verifier");
    private final DatePicker dateSubmitted = new DatePicker("Date Submitted");
    private final DatePicker dateVerified = new DatePicker("Date Verified");
    private final TextField reviewState = new TextField("Review State");
    
    private final SubmissionDto submissionDto=new SubmissionDto();

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Close");
    
    private final Binder<SubmissionDto> binder = new BeanValidationBinder<>(SubmissionDto.class);


    public SubmitForm(List<LabContact> labContacts) {

        binder.bindInstanceFields(this);

        submitter.setItems(labContacts);
        verifier.setItems(labContacts);
        
        submitter.setItemLabelGenerator(LabContact::getFullname);
        verifier.setItemLabelGenerator(LabContact::getFullname);
        
        add(
                submitter,
                verifier,
                dateSubmitted,
                dateVerified,
                reviewState,
                createButtonLayout());
        
        
    }
    
    public void setSubmissionDto(SubmissionDto submissionDto){
        this.binder.readBean(submissionDto);
    }
    
    private Component createButtonLayout() {
        return null;
    }
    
    public void success() {
    }

    public void error() {
    }

}
