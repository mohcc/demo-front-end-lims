package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
import org.hl7.fhir.r4.model.Task;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleService;
import zw.gov.mohcc.mrs.fhir.lims.entities.RejectionReason;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;
import zw.gov.mohcc.mrs.fhir.lims.util.RejectionReasonRepository;

@PageTitle("Sample")
@Route(value = "sample/:clientOrderNumber?/view", layout = MainLayout.class)
public class SampleView extends VerticalLayout implements BeforeEnterObserver {

    private String clientOrderNumber;
    private Sample sample;
    private final SampleRepository sampleRepository;
    private final SampleService sampleService;

    private final Button confirmReceiptBtn = new Button("Confirm Receipt");
    private final ProgressBar progressBar = new ProgressBar();
    private final Span statusSpan = new Span();

    private final VerticalLayout sampleViewSection = new VerticalLayout();
    private RejectionForm rejectionForm;
    private final VerticalLayout formSection = new VerticalLayout();

    public SampleView(SampleRepository sampleRepository, SampleService sampleService) {
        this.sampleRepository = sampleRepository;
        this.sampleService = sampleService;

    }

    private void postInitComponents() {
        if (sample != null) {
            setSizeFull();
            configureSampleViewSection();
            configureFormSection();
            add(getToolBar(), getContent());

        } else {
            add(new H1("Sample not found"));
        }

    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(sampleViewSection, formSection);
        content.setFlexGrow(2, sampleViewSection);
        content.setFlexGrow(1, formSection);
        content.addClassName("content");
        content.setSizeFull();
        return content;

    }

    private Component getToolBar() {
        VerticalLayout toolBar = new VerticalLayout();
        toolBar.add(new H1("Sample"));
        return toolBar;
    }

    private void configureFormSection() {
        configureSampleRejectionForm();
        formSection.add(rejectionForm);
        formSection.setWidth("30em");

    }

    private void configureSampleRejectionForm() {
        List<RejectionReason> rejectionReasons = RejectionReasonRepository.getRejectionReasons();
        rejectionForm = new RejectionForm(sample, rejectionReasons);
        rejectionForm.addSaveListener(this::rejectSample);
        rejectionForm.addCloseListener(e -> closeFormSection());
    }

    private void closeFormSection() {
        this.formSection.setVisible(false);
    }

    private void rejectSample(RejectionForm.SaveEvent event) {
        UI ui = UI.getCurrent();
        sampleService.rejectOrder(event.getSample(), event.getRejectionReasons()).thenAccept(result -> {
            ui.access(() -> {
                showNotification("Sample rejected successfully.");
                statusSpan.setText(sample.getStatus());
                rejectionForm.postSuccess();
            });

        }).handle((res, ex) -> {
            ui.access(() -> {
                rejectionForm.postFailure();
            });

            return res;
        });
    }

    private void configureSampleViewSection() {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        statusSpan.setText(sample.getStatus());
        confirmReceiptBtn.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.REQUESTED.name()));

        confirmReceiptBtn.addClickListener(click -> {
            confirmReceipt();
        });

        sampleViewSection.add(new H1("Sample:: " + sample.getClientOrderNumber()));
        sampleViewSection.add(statusSpan);
        sampleViewSection.add(progressBar);

        HorizontalLayout actionSection = new HorizontalLayout(
                confirmReceiptBtn
        );
        actionSection.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        sampleViewSection.add(actionSection);
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
    }

    private void afterFailedReceipt() {
        confirmReceiptBtn.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.REQUESTED.name()));
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
