package zw.gov.mohcc.mrs.demofront.lims;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import org.hl7.fhir.r4.model.Task;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleService;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;

@Route("sample/:clientOrderNumber?/view")
public class SampleView extends VerticalLayout implements BeforeEnterObserver {

    private String clientOrderNumber;
    private Sample sample;
    private final SampleRepository sampleRepository;
    private final SampleService sampleService;

    private final Button confirmReceiptBtn = new Button("Confirm Receipt");
    private final ProgressBar progressBar = new ProgressBar();
    private final Span statusSpan = new Span();

    private final RouterLink rejectSampleLink = new RouterLink();
    private final RouterLink allSamplesLink = new RouterLink("All Samples", SampleListView.class);

    public SampleView(SampleRepository sampleRepository, SampleService sampleService) {
        this.sampleRepository = sampleRepository;
        this.sampleService = sampleService;

    }

    private void postInitComponents() {
        if (sample != null) {
            rejectSampleLink.setText("Reject Sample");
            rejectSampleLink.setRoute(SampleRejectionForm.class, new RouteParameters("clientOrderNumber", clientOrderNumber));
            progressBar.setIndeterminate(true);
            progressBar.setVisible(false);
            statusSpan.setText(sample.getStatus());
            confirmReceiptBtn.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.REQUESTED.name()));
            rejectSampleLink.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
            confirmReceiptBtn.addClickListener(click -> {
                confirmReceipt();
            });

            add(new H1("Sample:: " + sample.getClientOrderNumber()));
            add(statusSpan);
            add(progressBar);
            add();
            
            HorizontalLayout actionSection=new HorizontalLayout(
                    confirmReceiptBtn,
                    rejectSampleLink,
                    allSamplesLink
            );
            actionSection.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
            add(actionSection);

        } else {
            add(new H1("Sample not found"));
        }

    }

    private void confirmReceipt() {
        progressBar.setVisible(true);
        confirmReceiptBtn.setEnabled(false);
        UI ui = UI.getCurrent();
        sampleService.confirmReceipt(sample).thenAccept(result -> {
            ui.access(() -> {
                afterSuccessfulReceipt();
            });
        }).handle((res, ex) -> {
            afterFailedReceipt();
            return res;
        });

    }

    private void afterSuccessfulReceipt() {
        showNotification("Receipt of this sample done successfully.");
        statusSpan.setText(sample.getStatus());
        progressBar.setVisible(false);
        rejectSampleLink.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
    }

    private void afterFailedReceipt() {
        confirmReceiptBtn.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.REQUESTED.name()));
        rejectSampleLink.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
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
