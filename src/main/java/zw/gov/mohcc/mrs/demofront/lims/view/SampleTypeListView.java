package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.fhir.lims.entities.SampleType;
import zw.gov.mohcc.mrs.fhir.lims.util.SampleTypeRepository;

@PageTitle("Sample Types")
@Route(value = "sampleTypes", layout = MainLayout.class)
public class SampleTypeListView extends VerticalLayout {

    private final Grid<SampleType> grid = new Grid<>(SampleType.class);

    public SampleTypeListView() {
        add(new H1("Sample Types"));
        add(grid);
        grid.setItems(SampleTypeRepository.getSampleTypes());
        grid.setColumns("code",
                "title");
    }

}
