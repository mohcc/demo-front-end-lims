package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabAnalysis;

public class LabAnalysisGrid extends VerticalLayout {

    private Grid<LabAnalysis> grid = new Grid<>(LabAnalysis.class);

    public LabAnalysisGrid() {
        add(new H3("Lab Analysis"));
        add(grid);
    }

    public void setItems(List<LabAnalysis> labAnalyses) {
        if (labAnalyses != null) {
            grid.setItems(labAnalyses);
        }
    }

}
