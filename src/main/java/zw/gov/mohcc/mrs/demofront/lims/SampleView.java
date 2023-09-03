package zw.gov.mohcc.mrs.demofront.lims;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.demofront.lims.service.SampleRepository;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;

@Route("sample/:clientOrderNumber?/view")
public class SampleView extends VerticalLayout implements BeforeEnterObserver {

    private String clientOrderNumber;
    private Sample sample;
    private final SampleRepository sampleRepository;

    public SampleView(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }
    
    private void initComponents(){
        if (sample != null) {
            add(new H1("Sample:: " + sample.getClientOrderNumber()));
        } else {
            add(new H1("Sample not found"));
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent bee) {
        clientOrderNumber = bee.getRouteParameters().get("clientOrderNumber").
                orElse(null);
        if (clientOrderNumber != null) {
            sample = sampleRepository.findByClientOrderNumber(clientOrderNumber).orElse(null);
        }
        
        initComponents();

    }

}
