package zw.gov.mohcc.mrs.demofront.lims;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zw.gov.mohcc.mrs.fhir.lims.FhirClientUtility;

@Component
public class PostConstructBean {

    @Value("${openhie.shared-health-record.url}")
    private String shrUrl;
    @Value("${openhie.shared-health-record.username}")
    private String username;
    @Value("${openhie.shared-health-record.password}")
    private String password;

    @PostConstruct
    public void init() {
        FhirClientUtility.baseUrl = shrUrl;
        FhirClientUtility.username = username;
        FhirClientUtility.password = password;

    }

}
