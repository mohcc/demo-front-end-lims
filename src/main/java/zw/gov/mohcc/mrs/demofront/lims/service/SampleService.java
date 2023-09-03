package zw.gov.mohcc.mrs.demofront.lims.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import zw.gov.mohcc.mrs.fhir.lims.OrdersRetriever;
import zw.gov.mohcc.mrs.fhir.lims.entities.Sample;
import zw.gov.mohcc.mrs.fhir.lims.translators.TaskSampleTranslator;

@Service
@RequiredArgsConstructor
public class SampleService {

    private final SampleRepository sampleRepository;

    @Scheduled(fixedDelay = 5000)
    public void fetchSamples() {
        System.out.println("Fetching samples from SHR");
        List<Task> fhirTasks = null;
        try {
            fhirTasks = OrdersRetriever.getAllTasks();
            System.out.println("Finished fetching samples from SHR");
        } catch (Exception ex) {
            System.out.println("Exception when fetching tasks from SHR:: " + ex);
        }

        if (fhirTasks != null) {
            for (Task task : fhirTasks) {
                String taskIdPart = task.getIdElement().getIdPart();
                try {
                    if (!sampleRepository.contains(task)) {
                        System.out.println("Converting a Task(" + taskIdPart + ") to a Sample");
                        Sample sample = TaskSampleTranslator.toSample(task);
                        sampleRepository.addSample(sample);
                        System.out.println("Finished converting a Task(" + taskIdPart + ") to a Sample");
                    } else {
                        System.out.println("Task(" + taskIdPart + ") already added. No need for conversion.");
                    }
                } catch (Exception ex) {
                    System.out.println("Error in converting a Task(" + taskIdPart + ") to a Sample:: " + ex);
                }
            }
        }

        System.out.println("Finished converting and fetching samples ");

    }

}
