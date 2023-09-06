package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import java.util.Collections;
import zw.gov.mohcc.mrs.fhir.lims.entities.Client;
import zw.gov.mohcc.mrs.fhir.lims.entities.Gender;
import zw.gov.mohcc.mrs.fhir.lims.entities.LimsPatient;
import zw.gov.mohcc.mrs.fhir.lims.entities.PatientIdentifier;

public class PatientReadOnlyForm extends FormLayout{
    
    private final Binder<LimsPatient> binder = new BeanValidationBinder<>(LimsPatient.class);
    
    private final TextField firstname=new TextField("Firstname");
    private final TextField surname=new TextField("Surname");
    private final TextField phoneMobile=new TextField("Phone Mobile");
    private final Checkbox consentToSms=new Checkbox("Consent to Sms");
    private final ComboBox<Gender> gender=new ComboBox<>("Gender");
    private final Checkbox birthDateMissing=new Checkbox("Birth Date Missing");
    private final Checkbox birthDateEstimated=new Checkbox("Birth Date Estimated");
    private final DatePicker birthDate=new DatePicker("Birth date");
    private final ComboBox<Client> primaryReferrer=new ComboBox<>("Primary Referrer");
    
    
    
    private final Grid<PatientIdentifier> patientIdentifiers=new Grid<>(PatientIdentifier.class);
    
    
    public PatientReadOnlyForm(LimsPatient limsPatient){
        
        firstname.setReadOnly(true);
        surname.setReadOnly(true);
        phoneMobile.setReadOnly(true);
        consentToSms.setReadOnly(true);
        gender.setReadOnly(true);
        gender.setItems(Gender.values());        
        birthDateMissing.setReadOnly(true);
        birthDateEstimated.setReadOnly(true);
        birthDate.setReadOnly(true);
        primaryReferrer.setReadOnly(true);
        if(limsPatient.getPrimaryReferrer()!=null){
            primaryReferrer.setItems(Collections.singletonList(limsPatient.getPrimaryReferrer()));
        }
         
        primaryReferrer.setItemLabelGenerator(Client::getName);
        
        
        
        if(limsPatient.getAdditionalIdentifiers()!=null){
            patientIdentifiers.setItems(limsPatient.getAdditionalIdentifiers());
        }
        
        binder.bindInstanceFields(this);
        
        
        binder.readBean(limsPatient);
        
        add(firstname,surname, phoneMobile, 
                consentToSms, 
                gender, birthDateMissing,  birthDateEstimated, birthDate,
                primaryReferrer
        );
        
    }
    
    
    
    
}
