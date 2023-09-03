package zw.gov.mohcc.mrs.demofront.lims.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Task;
import org.springframework.stereotype.Service;
import zw.gov.mohcc.mrs.fhir.lims.entities.LimsPatient;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;

@Service
public class SampleRepository {

    private final List<Sample> samples = new ArrayList<>();

    public void addSample(Sample sample) {
        if (!samples.contains(sample)) {
            samples.add(sample);
        }
    }

    public boolean contains(Task task) {
        String taskIdPart = task.getIdElement().getIdPart();
        return contains(taskIdPart);
    }

    public boolean contains(Sample sample) {
        return samples.contains(sample);
    }

    public boolean contains(String taskIdPart) {
        Sample sample = new Sample();
        sample.setClientOrderNumber(taskIdPart);
        return contains(sample);
    }

    public List<Sample> findByClientPatientId(String clientPatientId) {
        return samples.stream().filter(s -> s.getPatient().getClientPatientId().equals(clientPatientId))
                .collect(Collectors.toList());
    }

    public List<Sample> findByPatient(LimsPatient patient) {
        String clientPatientId = patient.getClientPatientId();
        return findByClientPatientId(clientPatientId);

    }

    public List<Sample> getSamples() {
        return samples;
    }
    
    public Optional<Sample> findByClientOrderNumber(String clientOrderNumber){
         return samples.stream().filter(s -> s.getClientOrderNumber().equals(clientOrderNumber))
                .findFirst();
    }

}
