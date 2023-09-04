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
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Task;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleService;
import zw.gov.mohcc.mrs.fhir.lims.entities.AnalysisService;
import zw.gov.mohcc.mrs.fhir.lims.entities.Instrument;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabAnalysis;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;
import zw.gov.mohcc.mrs.fhir.lims.entities.Method;
import zw.gov.mohcc.mrs.fhir.lims.entities.RejectionReason;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;
import zw.gov.mohcc.mrs.fhir.lims.util.AnalysisServiceRepository;
import zw.gov.mohcc.mrs.fhir.lims.util.InstrumentRepository;
import zw.gov.mohcc.mrs.fhir.lims.util.LabContactRepository;
import zw.gov.mohcc.mrs.fhir.lims.util.MethodRepository;
import zw.gov.mohcc.mrs.fhir.lims.util.RejectionReasonRepository;

@PageTitle("Sample")
@Route(value = "sample/:clientOrderNumber?/view", layout = MainLayout.class)
public class SampleView extends VerticalLayout implements BeforeEnterObserver {

    private String clientOrderNumber;
    private Sample sample;
    private final SampleRepository sampleRepository;
    private final SampleService sampleService;

    private final Button confirmReceiptBtn = new Button("Confirm Receipt");
    private final Button rejectBtn = new Button("Reject");
    private final Button resultBtn = new Button("Add Lab Analysis");
    private final ProgressBar progressBar = new ProgressBar();
    private final Span statusSpan = new Span();

    private final VerticalLayout sampleViewSection = new VerticalLayout();
    private RejectionForm rejectionForm;
    private LabAnalysisForm labAnalysisForm;
    private final VerticalLayout formSection = new VerticalLayout();
    private final LabAnalysisGrid labAnalysisGrid = new LabAnalysisGrid();
    private final RejectionReasonGrid rejectionReasonGrid=new RejectionReasonGrid();

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
        configureLabAnalyisForm();
        formSection.add(rejectionForm, labAnalysisForm);
        formSection.setWidth("30em");

    }

    private void configureSampleRejectionForm() {
        List<RejectionReason> rejectionReasons = RejectionReasonRepository.getRejectionReasons();
        rejectionForm = new RejectionForm(sample, rejectionReasons);
        rejectionForm.addSaveListener(this::rejectSample);
        rejectionForm.addCloseListener(e -> closeFormSection());
    }

    private void configureLabAnalyisForm() {
        List<AnalysisService> analysisServices = AnalysisServiceRepository.getAnalysisServices();
        List<LabContact> labContacts = LabContactRepository.getLabContacts();
        List<Instrument> instruments = InstrumentRepository.getInstruments();
        List<Method> methods = MethodRepository.getMethods();
        labAnalysisForm = new LabAnalysisForm(labContacts, instruments, analysisServices, methods);
        labAnalysisForm.addSaveListener(this::saveLabAnalysis);
        labAnalysisForm.addCloseListener(e -> closeFormSection());

    }

    private void closeFormSection() {
        this.formSection.setVisible(false);
    }

    private void saveLabAnalysis(LabAnalysisForm.SaveEvent event) {
        UI ui = UI.getCurrent();
        LabAnalysis labAnalysis = event.getLabAnalysis();
        if (sample.getLabAnalyses() == null) {
            sample.setLabAnalyses(new ArrayList<>());
        }

        sample.getLabAnalyses().add(labAnalysis);

        labAnalysisForm.success();

        labAnalysisGrid.setItems(sample.getLabAnalyses());

        formSection.setVisible(false);

    }

    private void rejectSample(RejectionForm.SaveEvent event) {
        UI ui = UI.getCurrent();
        sampleService.rejectOrder(event.getSample(), event.getRejectionReasons()).thenAccept(result -> {
            ui.access(() -> {
                showNotification("Sample rejected successfully.");
                statusSpan.setText(sample.getStatus());
                rejectionReasonGrid.setItems(sample.getRejectionReasons());
                toggleVisibility();
                rejectionForm.success();
                closeFormSection();
            });

        }).handle((res, ex) -> {
            ui.access(() -> {
                rejectionForm.error();
            });

            return res;
        });
    }

    private void configureSampleViewSection() {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        statusSpan.setText(sample.getStatus());

        formSection.setVisible(false);

        confirmReceiptBtn.addClickListener(click -> {
            confirmReceipt();
        });

        rejectBtn.addClickListener(click -> {
            formSection.setVisible(true);
            rejectionForm.setVisible(true);
            rejectionForm.clearSelectedItems();
            labAnalysisForm.setVisible(false);
        });

        resultBtn.addClickListener(click -> {
            formSection.setVisible(true);
            rejectionForm.setVisible(false);
            labAnalysisForm.setVisible(true);
            labAnalysisForm.setLabAnalysis(new LabAnalysis());
        });

        sampleViewSection.add(new H1("Sample:: " + sample.getClientOrderNumber()));
        sampleViewSection.add(statusSpan);
        sampleViewSection.add(progressBar);

        HorizontalLayout actionSection = new HorizontalLayout(
                confirmReceiptBtn, rejectBtn, resultBtn
        );
        actionSection.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        sampleViewSection.add(actionSection);

        labAnalysisGrid.setItems(sample.getLabAnalyses());
        
        rejectionReasonGrid.setItems(sample.getRejectionReasons());

        toggleVisibility();

        sampleViewSection.add(labAnalysisGrid);
        
        sampleViewSection.add(rejectionReasonGrid);
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

    private void toggleVisibility() {
        confirmReceiptBtn.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.REQUESTED.name()));
        rejectBtn.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
        resultBtn.setEnabled(sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name()));
        labAnalysisGrid.setVisible(sample.getLabAnalyses() != null && !sample.getLabAnalyses().isEmpty());
        rejectionReasonGrid.setVisible(sample.getRejectionReasons()!=null && !sample.getRejectionReasons().isEmpty());
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
