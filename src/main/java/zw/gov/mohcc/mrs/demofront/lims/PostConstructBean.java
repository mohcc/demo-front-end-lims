package zw.gov.mohcc.mrs.demofront.lims;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zw.gov.mohcc.mrs.fhir.lims.CrFhirClientUtility;
import zw.gov.mohcc.mrs.fhir.lims.ShrFhirClientUtility;

@Component
public class PostConstructBean {

    @Value("${hie.shared-health-record.url}")
    private String shrUrl;
    @Value("${hie.client-registry.url}")
    private String crUrl;
    @Value("${hie.username}")
    private String username;
    @Value("${hie.password}")
    private String password;

    @PostConstruct
    public void init() {
        ShrFhirClientUtility.baseUrl = shrUrl;
        ShrFhirClientUtility.username = username;
        ShrFhirClientUtility.password = password;
        
        CrFhirClientUtility.baseUrl = crUrl;
        CrFhirClientUtility.username = username;
        CrFhirClientUtility.password = password;

    }

}
