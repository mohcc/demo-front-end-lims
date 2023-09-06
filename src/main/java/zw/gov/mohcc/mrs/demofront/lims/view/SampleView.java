package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Task;
import org.springframework.beans.BeanUtils;
import zw.gov.mohcc.mrs.demofront.lims.dto.ConfirmReceiptDto;
import zw.gov.mohcc.mrs.demofront.lims.dto.PublishDto;
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
    private final Button publishBtn = new Button("Publish");

    private final Span statusSpan = new Span();

    private final VerticalLayout sampleViewSection = new VerticalLayout();

    private RejectionForm rejectionForm;
    private LabAnalysisForm labAnalysisForm;
    private PublishForm publishForm;
    private ConfirmReceiptForm confirmReceiptForm;

    private final VerticalLayout formSection = new VerticalLayout();
    private final LabAnalysisGrid labAnalysisGrid = new LabAnalysisGrid();
    private final RejectionReasonGrid rejectionReasonGrid = new RejectionReasonGrid();

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
        configurePublishForm();
        configureConfirmReceiptForm();
        formSection.add(rejectionForm, labAnalysisForm, publishForm, confirmReceiptForm);
        formSection.setWidth("30em");

    }

    private void configureSampleRejectionForm() {
        List<RejectionReason> rejectionReasons = RejectionReasonRepository.getRejectionReasons();
        rejectionForm = new RejectionForm(rejectionReasons);
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

    private void configurePublishForm() {
        List<LabContact> labContacts = LabContactRepository.getLabContacts();
        publishForm = new PublishForm(labContacts);
        publishForm.addSaveListener(this::savePublish);
        publishForm.addCloseListener(e -> closeFormSection());

    }

    private void configureConfirmReceiptForm() {
        confirmReceiptForm = new ConfirmReceiptForm();
        confirmReceiptForm.addSaveListener(this::confirmReceipt);
        confirmReceiptForm.addCloseListener(e -> closeFormSection());
    }

    private void closeFormSection() {
        this.formSection.setVisible(false);
    }

    private void saveLabAnalysis(LabAnalysisForm.SaveEvent event) {
        LabAnalysis labAnalysis = event.getLabAnalysis();
        if (sample.getLabAnalyses() == null) {
            sample.setLabAnalyses(new ArrayList<>());
        }

        sample.getLabAnalyses().add(labAnalysis);

        labAnalysisForm.success();

        updateStateComponents();

        closeFormSection();

    }

    private void savePublish(PublishForm.SaveEvent event) {
        UI ui = UI.getCurrent();
        BeanUtils.copyProperties(event.getPublishDto(), sample);
        sampleService.publish(sample).thenAccept(result -> {
            ui.access(() -> {
                showNotification("Sample result published successfully.");
                updateStateComponents();
                publishForm.success();
                closeFormSection();
            });

        }).handle((res, ex) -> {
            ui.access(() -> {
                Notification.show("Failed");
                updateStateComponents();
                publishForm.error();
            });

            return res;
        });

    }

    private void rejectSample(RejectionForm.SaveEvent event) {
        UI ui = UI.getCurrent();
        sampleService.rejectOrder(sample, event.getRejectionReasons()).thenAccept(result -> {
            ui.access(() -> {
                showNotification("Sample rejected successfully.");
                updateStateComponents();
                rejectionForm.success();
                closeFormSection();
            });

        }).handle((res, ex) -> {
            ui.access(() -> {
                Notification.show("Failed");
                updateStateComponents();
                rejectionForm.error();
            });

            return res;
        });
    }

    private void confirmReceipt(ConfirmReceiptForm.SaveEvent event) {
        UI ui = UI.getCurrent();
        
        LocalDateTime dateReceived = event.getConfirmReceiptDto().getDateReceived();
        LocalDateTime dateReceivedAtHub = event.getConfirmReceiptDto().getDateReceivedAtHub();
        
        sampleService.confirmReceipt(sample, dateReceived, dateReceivedAtHub).thenAccept(result -> {
            ui.access(() -> {
                showNotification("Receipt of this sample done successfully.");
                updateStateComponents();
                confirmReceiptForm.success();
                closeFormSection();
            });
        }).handle((res, ex) -> {
            ui.access(() -> {
                Notification.show("Failed to confirm receipt of this sample");
                updateStateComponents();
                confirmReceiptForm.error();
            });

            return res;
        });

    }

    private void configureSampleViewSection() {
        statusSpan.setText(sample.getStatus());

        formSection.setVisible(false);

        confirmReceiptBtn.addClickListener(click -> {
            formSection.setVisible(true);
            confirmReceiptForm.setVisible(true);
            rejectionForm.setVisible(false);
            labAnalysisForm.setVisible(false);
            publishForm.setVisible(false);
            confirmReceiptForm.setConfirmReceiptDto(new ConfirmReceiptDto());
        });

        rejectBtn.addClickListener(click -> {
            formSection.setVisible(true);
            rejectionForm.setVisible(true);
            labAnalysisForm.setVisible(false);
            publishForm.setVisible(false);
            confirmReceiptForm.setVisible(false);
            rejectionForm.clearSelectedItems();
        });

        resultBtn.addClickListener(click -> {
            formSection.setVisible(true);
            labAnalysisForm.setVisible(true);
            publishForm.setVisible(false);
            rejectionForm.setVisible(false);
            confirmReceiptForm.setVisible(false);
            labAnalysisForm.setLabAnalysis(new LabAnalysis());
        });

        publishBtn.addClickListener(click -> {
            formSection.setVisible(true);
            publishForm.setVisible(true);
            rejectionForm.setVisible(false);
            labAnalysisForm.setVisible(false);
            confirmReceiptForm.setVisible(false);
            publishForm.setPublishDto(new PublishDto());
        });

        sampleViewSection.add(new H4("Client Order #:: " + sample.getClientOrderNumber()));
        sampleViewSection.add(new H5("Client Sample Id:: " + sample.getClientSampleId()));
        sampleViewSection.add(new H5("Sample template:: " + (sample.getSampleTemplate() != null ? sample.getSampleTemplate().getTitle() : "")));
        sampleViewSection.add(new H5("Sample type:: " + (sample.getSampleType() != null ? sample.getSampleType().getTitle() : "")));

        sampleViewSection.add(statusSpan);

        HorizontalLayout actionSection = new HorizontalLayout(
                confirmReceiptBtn, rejectBtn, resultBtn, publishBtn
        );
        actionSection.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        sampleViewSection.add(actionSection);

        updateStateComponents();

        sampleViewSection.add(labAnalysisGrid);

        sampleViewSection.add(rejectionReasonGrid);
    }

    private void showNotification(String message) {
        Notification notification = new Notification(message);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(3000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private void updateStateComponents() {
        boolean hasRejectionReasons = sample.getRejectionReasons() != null && !sample.getRejectionReasons().isEmpty();
        boolean hasLabAnalyses = sample.getLabAnalyses() != null && !sample.getLabAnalyses().isEmpty();
        boolean isReceivedStatus = sample.getStatus().equalsIgnoreCase(Task.TaskStatus.RECEIVED.name());
        boolean isRequestedStatus = sample.getStatus().equalsIgnoreCase(Task.TaskStatus.REQUESTED.name());

        statusSpan.setText(sample.getStatus());
        labAnalysisGrid.setItems(sample.getLabAnalyses());
        rejectionReasonGrid.setItems(sample.getRejectionReasons());

        confirmReceiptBtn.setEnabled(isRequestedStatus);
        publishBtn.setEnabled(isReceivedStatus && hasLabAnalyses);
        rejectBtn.setEnabled(isReceivedStatus && !hasLabAnalyses);
        resultBtn.setEnabled(isReceivedStatus);

        labAnalysisGrid.setVisible(hasLabAnalyses);
        rejectionReasonGrid.setVisible(hasRejectionReasons);
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
