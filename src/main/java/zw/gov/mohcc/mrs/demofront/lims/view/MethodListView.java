package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.fhir.lims.entities.Method;
import zw.gov.mohcc.mrs.fhir.lims.util.MethodRepository;

@PageTitle("Method")
@Route(value="methods", layout = MainLayout.class)
public class MethodListView extends VerticalLayout{
    
    private final Grid<Method> grid=new Grid<>(Method.class);
    
    public MethodListView(){
        add(new H1("Methods"));
        add(grid);
        grid.setItems(MethodRepository.getMethods());
    }
    
}
