package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import zw.gov.mohcc.mrs.fhir.lims.entities.AnalysisService;
import zw.gov.mohcc.mrs.fhir.lims.entities.Instrument;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabAnalysis;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;
import zw.gov.mohcc.mrs.fhir.lims.entities.Method;

public class LabAnalysisGrid extends VerticalLayout {

    private final Grid<LabAnalysis> grid = new Grid<>(LabAnalysis.class);

    public LabAnalysisGrid() {
        configureGrid();
        add(new H3("Lab Analysis"));
        add(grid);
    }

    public void setItems(List<LabAnalysis> labAnalyses) {
        if (labAnalyses != null) {
            grid.setItems(labAnalyses);
        }
    }

    private void configureGrid() {
        grid.setColumns(
                "dueDate",
                "resultValue");
        
        grid.addColumn(labAnalysis -> {
            LabContact analyst = labAnalysis.getAnalyst();
            return analyst == null ? "" : analyst.getFullname();
        }).setHeader("Analyst");
        
        grid.addColumn(labAnalysis -> {
            Instrument instrument = labAnalysis.getInstrument();
            return instrument == null ? "" : instrument.getTitle();
        }).setHeader("Instrument");
        
        grid.addColumn(labAnalysis -> {
            Method method = labAnalysis.getMethod();
            return method == null ? "" : method.getTitle();
        }).setHeader("Method");
        
        grid.addColumn(labAnalysis -> {
            AnalysisService analysis = labAnalysis.getAnalysis();
            return analysis == null ? "" : analysis.getTitle();
        }).setHeader("Analysis");
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        

    }

}
