package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import java.util.Set;
import org.hl7.fhir.r4.model.Task;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleService;
import zw.gov.mohcc.mrs.fhir.lims.entities.RejectionReason;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;
import zw.gov.mohcc.mrs.fhir.lims.util.RejectionReasonRepository;

@Route("sample/:clientOrderNumber?/rejectForm")
public class SampleRejectionForm extends VerticalLayout implements BeforeEnterObserver {
    
    private String clientOrderNumber;
    private Sample sample;
    private final SampleRepository sampleRepository;
    private final SampleService sampleService;
    
    private final MultiSelectListBox<RejectionReason> listBox = new MultiSelectListBox<>();
    private final Button rejectionButton = new Button("Reject Sample");
    private final RouterLink viewSampleLink = new RouterLink();
    private final RouterLink allSamplesLink = new RouterLink("All Samples", SampleListView.class);
    private final ProgressBar progressBar = new ProgressBar();
    
    public SampleRejectionForm(SampleRepository sampleRepository, SampleService sampleService) {
        this.sampleRepository = sampleRepository;
        this.sampleService = sampleService;
        
        listBox.setItems(RejectionReasonRepository.getRejectionReasons());
        
        listBox.setItemLabelGenerator(RejectionReason::getName);
                
    }
    
    private void postInitComponents() {
        if (sample != null) {
            
            progressBar.setIndeterminate(true);
            progressBar.setVisible(false);
            
            rejectionButton.addClickListener(click -> {
                rejectOrder();
            });
            
            listBox.addSelectionListener((MultiSelectionEvent<MultiSelectListBox<RejectionReason>, RejectionReason> mse) -> {
                rejectionButton.setEnabled(!listBox.getSelectedItems().isEmpty() && sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
            });
            
            viewSampleLink.setText("Go to Sample");
            viewSampleLink.setRoute(SampleView.class, new RouteParameters("clientOrderNumber", clientOrderNumber));
            
            rejectionButton.setEnabled(!listBox.getSelectedItems().isEmpty() && sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
            listBox.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
            
            add(new H1("Sample:: " + sample.getClientOrderNumber()));
            add(new H2("Sample Rejection Form"));
            
            add(new Span("Rejection reasons"));
            add(listBox);
            add(progressBar);
            
            HorizontalLayout actionSection = new HorizontalLayout(rejectionButton, viewSampleLink, allSamplesLink);
            actionSection.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
            add(actionSection);
            
        } else {
            add(new H1("Sample not found"));
        }
        
    }
    
    private void rejectOrder() {
        progressBar.setVisible(true);
        Set<RejectionReason> rejectionReasons = listBox.getSelectedItems();
        rejectionButton.setEnabled(false);
        listBox.setEnabled(false);
        UI ui = UI.getCurrent();
        sampleService.rejectOrder(sample, rejectionReasons).thenAccept(result -> {
            ui.access(() -> {
                afterRejection();
            });
        }).handle((res, ex) -> {
            afterFailedRejection();
            return res;
        });
        
    }
    
    private void afterRejection() {
        showNotification("Sample rejected successfully.");
        rejectionButton.setEnabled(false);
        listBox.setEnabled(false);
        progressBar.setVisible(false);
        
    }
    
    private void afterFailedRejection() {
        rejectionButton.setEnabled(!listBox.getSelectedItems().isEmpty() && sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
        listBox.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
        progressBar.setVisible(false);
        Notification.show("Failed to confirm receipt of this sample");
    }
    
    private void showNotification(String message) {
        Notification notification = new Notification(message);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(3000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent bee) {
        clientOrderNumber = bee.getRouteParameters().get("clientOrderNumber").
                orElse(null);
        if (clientOrderNumber != null) {
            sample = sampleRepository.findByClientOrderNumber(clientOrderNumber).orElse(null);
        }
        
        postInitComponents();
        
    }
    
}
