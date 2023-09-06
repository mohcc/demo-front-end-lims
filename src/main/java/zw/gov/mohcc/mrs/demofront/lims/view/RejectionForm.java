package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.shared.Registration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import zw.gov.mohcc.mrs.fhir.lims.entities.RejectionReason;

public class RejectionForm extends VerticalLayout {
    
    private final MultiSelectListBox<RejectionReason> listBox = new MultiSelectListBox<>();
    private final Button rejectionButton = new Button("Reject Sample");
    private final Button cancelButton = new Button("Cancel");
    private final ProgressBar progressBar = new ProgressBar();

    public RejectionForm(List<RejectionReason> rejectionReasons) {

        listBox.setItems(rejectionReasons);

        listBox.setItemLabelGenerator(RejectionReason::getName);

        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        listBox.addSelectionListener((MultiSelectionEvent<MultiSelectListBox<RejectionReason>, RejectionReason> mse) -> {
            rejectionButton.setEnabled(!listBox.getSelectedItems().isEmpty());
        });

        rejectionButton.setEnabled(!listBox.getSelectedItems().isEmpty());
        
        add(new H2("Sample Rejection"));

        add(new Span("Rejection reasons"));
        add(listBox);
        add(progressBar);

        add(createButtonLayout());

    }

    private Component createButtonLayout() {
        rejectionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        rejectionButton.addClickListener(click -> {
            progressBar.setVisible(true);
            rejectionButton.setEnabled(false);
            listBox.setEnabled(false);
            Set<RejectionReason> rejectionReasons = listBox.getSelectedItems();
            fireEvent(new SaveEvent(this, rejectionReasons));
        });

        cancelButton.addClickListener(click -> {
            fireEvent(new CloseEvent(this));
        });

        HorizontalLayout actionSection = new HorizontalLayout(rejectionButton, cancelButton);
        actionSection.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        return actionSection;
    }

    public void success() {
        rejectionButton.setEnabled(false);
        listBox.setEnabled(false);
        progressBar.setVisible(false);
    }

    public void error() {
        rejectionButton.setEnabled(!listBox.getSelectedItems().isEmpty());
        progressBar.setVisible(false);
    }
    
    public void clearSelectedItems(){
        listBox.setValue(new HashSet<>());
    }

    // Events
    public static abstract class RejectionFormEvent extends ComponentEvent<RejectionForm> {

        private final Collection<RejectionReason> rejectionReasons;

        protected RejectionFormEvent(RejectionForm source, Collection<RejectionReason> rejectionReasons) {

            super(source, false);
            this.rejectionReasons = rejectionReasons;
        }

        public Collection<RejectionReason> getRejectionReasons() {
            return rejectionReasons;
        }

        

    }

    public static class SaveEvent extends RejectionFormEvent {

        public SaveEvent(RejectionForm source, Collection<RejectionReason> rejectionReasons) {
            super(source, rejectionReasons);
        }


    }

    public static class CloseEvent extends RejectionFormEvent {

        public CloseEvent(RejectionForm source) {
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
