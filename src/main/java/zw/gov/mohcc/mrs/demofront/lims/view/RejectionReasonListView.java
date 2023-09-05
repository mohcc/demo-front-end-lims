package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.fhir.lims.entities.RejectionReason;
import zw.gov.mohcc.mrs.fhir.lims.util.RejectionReasonRepository;

@PageTitle("Rejection reasons")
@Route(value="rejectionReasons", layout = MainLayout.class)
public class RejectionReasonListView extends VerticalLayout{
    
    private final Grid<RejectionReason> grid=new Grid<>(RejectionReason.class);
    
    public RejectionReasonListView(){
        add(new H1("Rejection reasons"));
        add(grid);
        grid.setItems(RejectionReasonRepository.getRejectionReasons());
    }
    
}
