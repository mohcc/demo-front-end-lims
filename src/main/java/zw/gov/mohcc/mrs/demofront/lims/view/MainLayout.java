package zw.gov.mohcc.mrs.demofront.lims.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        //https://martinfowler.com/articles/demo-front-end.html
        H1 logo = new H1("NMRL LIMS Demo Front-End");
        logo.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);

    }

    private void createDrawer() {

        RouterLink sampleListView = new RouterLink("Samples", SampleListView.class);
        RouterLink labContactListView = new RouterLink("Lab Contacts", LabContactListView.class);
        RouterLink analysisServiceListView = new RouterLink("Analysis Services", AnalysisServiceListView.class);
        RouterLink methodListView = new RouterLink("Methods", MethodListView.class);
        RouterLink instrumentListView = new RouterLink("Instruments", InstrumentListView.class);
        RouterLink rejectionReasonListView = new RouterLink("Rejection reasons", RejectionReasonListView.class);
        RouterLink sampleTypeListView = new RouterLink("Sample Types", SampleTypeListView.class);
        RouterLink analysisTemplateListView = new RouterLink("Analysis Templates", AnalysisTemplateListView.class);

        sampleListView.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(sampleListView,
                labContactListView,
                analysisServiceListView,
                methodListView,
                instrumentListView,
                rejectionReasonListView,
                sampleTypeListView,
                analysisTemplateListView
        ));

    }

}
