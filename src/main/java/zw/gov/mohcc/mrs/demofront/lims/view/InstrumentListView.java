package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.fhir.lims.entities.Instrument;
import zw.gov.mohcc.mrs.fhir.lims.util.InstrumentRepository;

@PageTitle("Instruments")
@Route(value="instruments", layout = MainLayout.class)
public class InstrumentListView extends VerticalLayout{
    
    private final Grid<Instrument> grid=new Grid<>(Instrument.class);
    
    public InstrumentListView(){
        add(new H1("Instruments"));
        add(grid);
        grid.setItems(InstrumentRepository.getInstruments());
        grid.setColumns("code",
                "title");
    }
    
}
