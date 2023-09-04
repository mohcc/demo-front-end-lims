package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import zw.gov.mohcc.mrs.fhir.lims.entities.RejectionReason;

public class RejectionReasonGrid extends VerticalLayout {

    private final Grid<RejectionReason> grid = new Grid<>(RejectionReason.class);

    public RejectionReasonGrid() {
        add(new H3("Rejection reasons"));
        add(grid);
    }

    public void setItems(List<RejectionReason> rejectionReasons) {
        if (rejectionReasons != null) {
            grid.setItems(rejectionReasons);
        }
    }

}
