package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.fhir.lims.entities.AnalysisTemplate;
import zw.gov.mohcc.mrs.fhir.lims.util.AnalysisTemplateRepository;

@PageTitle("Analysis Templates")
@Route(value = "analysisTemplates", layout = MainLayout.class)
public class AnalysisTemplateListView extends VerticalLayout {

    private final Grid<AnalysisTemplate> grid = new Grid<>(AnalysisTemplate.class);

    public AnalysisTemplateListView() {
        add(new H1("Analysis Templates"));
        add(new H6("Also known as Sample Templates"));
        add(grid);
        grid.setItems(AnalysisTemplateRepository.getAnalysisTemplates());
        grid.setColumns("code",
                "title");
    }

}
