package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.fhir.lims.entities.LimsPatient;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;

@Route(value="sample/:clientOrderNumber?/patient/view", layout = MainLayout.class)
public class PatientView extends VerticalLayout implements BeforeEnterObserver {

    private String clientOrderNumber;
    private Sample sample;
    private LimsPatient patient;
    private final SampleRepository sampleRepository;

    public PatientView(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    private void postInitComponents() {
        if (sample != null && patient!=null) {
            add(new H1(patient.getFirstname()+" "+patient.getSurname()));
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
