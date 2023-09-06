package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import zw.gov.mohcc.mrs.demofront.lims.dto.ConfirmReceiptDto;

public class ConfirmReceiptForm extends FormLayout {

    private ConfirmReceiptDto confirmReceiptDto = new ConfirmReceiptDto();

    private final DateTimePicker dateReceived = new DateTimePicker("Date Received");
    private final DateTimePicker dateReceivedAtHub = new DateTimePicker("Date Received At Hub");

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Close");

    private final ProgressBar progressBar = new ProgressBar();

    private final Binder<ConfirmReceiptDto> binder = new BeanValidationBinder<>(ConfirmReceiptDto.class);

    public ConfirmReceiptForm() {
        binder.bindInstanceFields(this);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        add(
                dateReceived,
                dateReceivedAtHub,
                progressBar,
                createButtonLayout());
    }

    public void setConfirmReceiptDto(ConfirmReceiptDto confirmReceiptDto) {
        this.binder.readBean(confirmReceiptDto);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> fireEvent(new ConfirmReceiptForm.CloseEvent(this)));

        return new HorizontalLayout(save, cancel);
    }
    
    private void validateAndSave() {
        try {
            binder.writeBean(confirmReceiptDto);

            progressBar.setVisible(true);
            save.setEnabled(false);

            fireEvent(new ConfirmReceiptForm.SaveEvent(this, confirmReceiptDto));
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

    // Events
    public static abstract class ConfirmReceiptFormEvent extends ComponentEvent<ConfirmReceiptForm> {

        private final ConfirmReceiptDto confirmReceiptDto;

        protected ConfirmReceiptFormEvent(ConfirmReceiptForm source, ConfirmReceiptDto confirmReceiptDto) {

            super(source, false);
            this.confirmReceiptDto = confirmReceiptDto;
        }

        public ConfirmReceiptDto getConfirmReceiptDto() {
            return confirmReceiptDto;
        }

    }

    public static class SaveEvent extends ConfirmReceiptFormEvent {

        public SaveEvent(ConfirmReceiptForm source, ConfirmReceiptDto confirmReceiptDto) {
            super(source, confirmReceiptDto);
        }
    }

    public static class CloseEvent extends ConfirmReceiptFormEvent {

        public CloseEvent(ConfirmReceiptForm source) {
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
