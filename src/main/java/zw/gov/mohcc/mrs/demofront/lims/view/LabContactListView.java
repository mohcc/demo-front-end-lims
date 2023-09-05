package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import zw.gov.mohcc.mrs.fhir.lims.entities.LabContact;
import zw.gov.mohcc.mrs.fhir.lims.util.LabContactRepository;

@PageTitle("Lab Contacts")
@Route(value = "labContacts", layout = MainLayout.class)
public class LabContactListView extends VerticalLayout {

    private final Grid<LabContact> grid = new Grid<>(LabContact.class);

    public LabContactListView() {
        add(new H1("Lab Contacts"));
        add(grid);
        grid.setItems(LabContactRepository.getLabContacts());
        grid.setColumns("labContactId",
                "firstname",
                "middlename",
                "surname",
                "fullname");
    }

}
