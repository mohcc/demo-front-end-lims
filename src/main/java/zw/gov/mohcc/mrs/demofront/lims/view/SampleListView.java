package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import java.util.List;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.fhir.lims.entities.Client;
import zw.gov.mohcc.mrs.fhir.lims.entities.LimsPatient;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;
import zw.gov.mohcc.mrs.fhir.lims.entities.SampleType;

@PageTitle("Samples")
@Route(value="", layout = MainLayout.class)
public class SampleListView extends VerticalLayout {

    private final SampleRepository sampleRepository;

    Grid<Sample> grid = new Grid<>(Sample.class);

    public SampleListView(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
        Button fetchButton = new Button("Reload Samples");
        fetchButton.addClickListener(click -> {
            updateList();
        });

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        add(new H1("Samples"));
        add(fetchButton);
        add(grid);

        updateList();

    }

    private void updateList() {
        List<Sample> samples = sampleRepository.getOrderedSamples();
        grid.setItems(samples);
    }

    private void configureGrid() {
        grid.addClassName("sample-grid");
        grid.setSizeFull();

        grid.setColumns(
                "clientSampleId",
                "dateSampled", "status");

        grid.addComponentColumn(sample -> {
            String clientOrderNumber = sample.getClientOrderNumber();
            LimsPatient patient = sample.getPatient();
            String patientName = patient == null ? "" : patient.getFirstname() + " " + patient.getSurname();
            return new RouterLink(patientName,
                    PatientView.class, new RouteParameters("clientOrderNumber", clientOrderNumber));
        }).setHeader("Patient");

        grid.addColumn(sample -> {
            Client client = sample.getClient();
            return client == null ? "" : client.getName();
        }).setHeader("Client");

        grid.addColumn(sample -> {
            SampleType sampleType = sample.getSampleType();
            return sampleType == null ? "" : sampleType.getTitle();
        }).setHeader("Sample Type");

        grid.addComponentColumn(sample -> {
            String clientOrderNumber = sample.getClientOrderNumber();
            return new RouterLink(clientOrderNumber,
                    SampleView.class, new RouteParameters("clientOrderNumber", clientOrderNumber));
        }).setHeader("Client Order Number");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

    }

}
