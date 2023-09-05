package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.fhir.lims.entities.AnalysisService;
import zw.gov.mohcc.mrs.fhir.lims.util.AnalysisServiceRepository;

@PageTitle("Analysis Services")
@Route(value = "analysisServices", layout = MainLayout.class)
public class AnalysisServiceListView extends VerticalLayout {

    private final Grid<AnalysisService> grid = new Grid<>(AnalysisService.class);

    public AnalysisServiceListView() {
        add(new H1("Analysis Services"));
        add(grid);
        grid.setItems(AnalysisServiceRepository.getAnalysisServices());
        grid.setColumns("analysisKeyword",
                "title",
                "unit",
                "code");
    }

}
