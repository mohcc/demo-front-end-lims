package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import zw.gov.mohcc.mrs.demofront.lims.dto.PublishDto;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;

public class PublishForm extends FormLayout {

    private final ComboBox<LabContact> submitter = new ComboBox<>("Submitter");
    private final ComboBox<LabContact> verifier = new ComboBox<>("Verifier");
    private final DatePicker dateSubmitted = new DatePicker("Date Submitted");
    private final DatePicker dateVerified = new DatePicker("Date Verified");
    private final TextField reviewState = new TextField("Review State");

    private PublishDto publishDto = new PublishDto();

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Close");

    private final ProgressBar progressBar = new ProgressBar();

    private final Binder<PublishDto> binder = new BeanValidationBinder<>(PublishDto.class);

    public PublishForm(List<LabContact> labContacts) {

        binder.bindInstanceFields(this);

        submitter.setItems(labContacts);
        verifier.setItems(labContacts);

        submitter.setItemLabelGenerator(LabContact::getFullname);
        verifier.setItemLabelGenerator(LabContact::getFullname);

        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        add(
                submitter,
                verifier,
                dateSubmitted,
                dateVerified,
                reviewState,
                progressBar,
                createButtonLayout());

    }

    public void setSubmissionDto(PublishDto publishDto) {
        this.binder.readBean(publishDto);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> fireEvent(new PublishForm.CloseEvent(this)));

        return new HorizontalLayout(save, cancel);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(publishDto);

            progressBar.setVisible(true);
            save.setEnabled(false);

            fireEvent(new PublishForm.SaveEvent(this, publishDto));
        } catch (ValidationException ex) {
            ex.printStackTrace();
        }
    }

    public void success() {
        progressBar.setVisible(false);
        save.setEnabled(true);
    }

    public void error() {
        progressBar.setVisible(false);
        save.setEnabled(true);
    }

    public void setPublishDto(PublishDto publishDto) {
        this.publishDto = publishDto;
    }

    // Events
    public static abstract class PublishFormEvent extends ComponentEvent<PublishForm> {

        private final PublishDto publishDto;

        protected PublishFormEvent(PublishForm source, PublishDto publishDto) {

            super(source, false);
            this.publishDto = publishDto;
        }

        public PublishDto getPublishDto() {
            return publishDto;
        }

    }

    public static class SaveEvent extends PublishFormEvent {

        public SaveEvent(PublishForm source, PublishDto publishDto) {
            super(source, publishDto);
        }
    }

    public static class CloseEvent extends PublishFormEvent {

        public CloseEvent(PublishForm source) {
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
