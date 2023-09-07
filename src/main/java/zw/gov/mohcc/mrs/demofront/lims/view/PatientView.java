package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.text.WordUtils;
import org.vaadin.stefan.table.Table;
import org.vaadin.stefan.table.TableRow;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.fhir.lims.entities.LimsPatient;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;

@PageTitle("Patient")
@Route(value = "sample/:clientOrderNumber?/patient/view", layout = MainLayout.class)
public class PatientView extends VerticalLayout implements BeforeEnterObserver {

    private String clientOrderNumber;
    private Sample sample;
    private LimsPatient patient;
    private final SampleRepository sampleRepository;

    public PatientView(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    private void postInitComponents() {
        if (sample != null && patient != null) {
            TableRow detailsRow;
            add(new H1(patient.getFirstname() + " " + patient.getSurname()));

            Table table = new Table();

            table.setSizeFull();

            detailsRow = table.addRow();
            detailsRow.addHeaderCell().setText("Firstname");
            detailsRow.addDataCell().setText(patient.getFirstname());
            detailsRow.addHeaderCell().setText("Surname");
            detailsRow.addDataCell().setText(patient.getSurname());
                                  
            detailsRow = table.addRow();
            detailsRow.addHeaderCell().setText("Patient");
            detailsRow.addDataCell().setText(patient.getFirstname() + " " + patient.getSurname());
            detailsRow.addHeaderCell().setText("Client Patient ID");
            detailsRow.addDataCell().setText(patient.getClientPatientId());

            detailsRow = table.addRow();
            detailsRow.addHeaderCell().setText("Gender");
            detailsRow.addDataCell().setText(patient.getGender() != null ? WordUtils.capitalizeFully(patient.getGender().name()) : "");
            detailsRow.addHeaderCell().setText("Date of Birth");
            detailsRow.addDataCell().setText(patient.getBirthDate() != null ? patient.getBirthDate().toString() : "");

            detailsRow = table.addRow();
            detailsRow.addHeaderCell().setText("Cohort");
            detailsRow.addDataCell().setText("");
            detailsRow.addHeaderCell().setText("Phone (mobile)");
            detailsRow.addDataCell().setText(patient.getPhoneMobile() != null ? patient.getPhoneMobile() : "");

            add(table);

        } else {
            add(new H1("Patient not found"));

        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent bee) {
        clientOrderNumber = bee.getRouteParameters().get("clientOrderNumber").
                orElse(null);
        if (clientOrderNumber != null) {
            sample = sampleRepository.findByClientOrderNumber(clientOrderNumber).orElse(null);
            patient = sample != null ? sample.getPatient() : patient;
        }

        postInitComponents();

    }

}
