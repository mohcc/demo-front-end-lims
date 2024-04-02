package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import org.springframework.beans.BeanUtils;
import zw.gov.mohcc.mrs.demofront.lims.dto.LabAnalysisDto;
import zw.gov.mohcc.mrs.fhir.lims.entities.AnalysisService;
import zw.gov.mohcc.mrs.fhir.lims.entities.Instrument;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabAnalysis;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;
import zw.gov.mohcc.mrs.fhir.lims.entities.Method;

public class LabAnalysisForm extends FormLayout {

    private final ComboBox<AnalysisService> analysis = new ComboBox<>("Analysis");
    private final ComboBox<Instrument> instrument = new ComboBox<>("Instrument");
    private final ComboBox<Method> method = new ComboBox<>("Method");
    private final TextField resultValue = new TextField("Result Value");
    private final DatePicker dueDate = new DatePicker("Due Date");
    private final ComboBox<LabContact> analyst = new ComboBox<>("Analyst");
    private final ComboBox<LabContact> submitter = new ComboBox<>("Submitter");
    private final ComboBox<LabContact> verifier = new ComboBox<>("Verifier");
    private final Checkbox critical = new Checkbox("Critical");
    private final TextField interpretationText = new TextField("Interpretation");

    private LabAnalysisDto labAnalysisDto = new LabAnalysisDto();

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Close");

    private final Binder<LabAnalysisDto> binder = new BeanValidationBinder<>(LabAnalysisDto.class);

    public LabAnalysisForm(List<LabContact> labContacts,
            List<Instrument> instruments, List<AnalysisService> analysisServices,
            List<Method> methods) {

        binder.bindInstanceFields(this);

        analyst.setItems(labContacts);
        submitter.setItems(labContacts);
        verifier.setItems(labContacts);
        analysis.setItems(analysisServices);
        instrument.setItems(instruments);
        method.setItems(methods);

        analysis.setItemLabelGenerator(AnalysisService::getTitle);
        instrument.setItemLabelGenerator(Instrument::getTitle);
        analyst.setItemLabelGenerator(LabContact::getFullname);
        submitter.setItemLabelGenerator(LabContact::getFullname);
        verifier.setItemLabelGenerator(LabContact::getFullname);
        method.setItemLabelGenerator(Method::getTitle);

        add(analysis,
                instrument,
                method,
                resultValue,
                dueDate,
                analyst,
                submitter,
                verifier,
                critical,
                interpretationText,
                createButtonLayout());

    }

    public void setLabAnalysis(LabAnalysis labAnalysis) {
        labAnalysisDto = new LabAnalysisDto();
        BeanUtils.copyProperties(labAnalysis, labAnalysisDto);
        binder.readBean(labAnalysisDto);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, cancel);
    }

    public void success() {

    }

    public void error() {

    }

    private void validateAndSave() {
        try {
            binder.writeBean(labAnalysisDto);
            LabAnalysis labAnalysis = new LabAnalysis();
            BeanUtils.copyProperties(labAnalysisDto, labAnalysis);
            fireEvent(new SaveEvent(this, labAnalysis));
        } catch (ValidationException ex) {
            ex.printStackTrace();
        }
    }

    // Events
    public static abstract class LabAnalysisFormEvent extends ComponentEvent<LabAnalysisForm> {

        private final LabAnalysis labAnalysis;

        protected LabAnalysisFormEvent(LabAnalysisForm source, LabAnalysis labAnalysis) {

            super(source, false);
            this.labAnalysis = labAnalysis;
        }

        public LabAnalysis getLabAnalysis() {
            return labAnalysis;
        }

    }

    public static class SaveEvent extends LabAnalysisFormEvent {

        public SaveEvent(LabAnalysisForm source, LabAnalysis labAnalysis) {
            super(source, labAnalysis);
        }
    }

    public static class CloseEvent extends LabAnalysisFormEvent {

        public CloseEvent(LabAnalysisForm source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}
