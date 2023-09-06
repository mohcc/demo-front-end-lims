package zw.gov.mohcc.mrs.demofront.lims.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.WordUtils;
import org.hl7.fhir.r4.model.Task;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import zw.gov.mohcc.mrs.fhir.lims.OrderReceiveConfirmer;
import zw.gov.mohcc.mrs.fhir.lims.OrderRejecter;
import zw.gov.mohcc.mrs.fhir.lims.OrderResultIssuer;
import zw.gov.mohcc.mrs.fhir.lims.OrdersRetriever;
import zw.gov.mohcc.mrs.fhir.lims.entities.RejectionReason;
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

    @Async
    public CompletableFuture<Sample> confirmReceipt(Sample sample, LocalDateTime dateReceived, LocalDateTime cdateReceivedAtHub) {
        CompletableFuture<Sample> completableFuture = new CompletableFuture<>();
        String taskId = sample.getClientOrderNumber();
        OrderReceiveConfirmer.confirmTaskReceived(taskId);
        sample.setDateReceived(dateReceived);
        sample.setDateReceivedAtHub(cdateReceivedAtHub);
        sample.setStatus(WordUtils.capitalizeFully(Task.TaskStatus.RECEIVED.name()));
        completableFuture.complete(sample);
        return completableFuture;
    }

    @Async
    public CompletableFuture<Sample> rejectOrder(Sample sample, Collection<RejectionReason> rejectionReasons) {
        CompletableFuture<Sample> completableFuture = new CompletableFuture<>();
        String taskId = sample.getClientOrderNumber();
        OrderRejecter.rejectOrder(taskId, rejectionReasons);
        sample.setStatus(WordUtils.capitalizeFully(Task.TaskStatus.REJECTED.name()));
        sample.setRejectionReasons(new ArrayList<>(rejectionReasons));
        completableFuture.complete(sample);
        return completableFuture;
    }
    
    @Async
    public CompletableFuture<Sample> publish(Sample sample) {
        CompletableFuture<Sample> completableFuture = new CompletableFuture<>();
        OrderResultIssuer.publishResults(sample);
        sample.setStatus(WordUtils.capitalizeFully(Task.TaskStatus.COMPLETED.name()));
        completableFuture.complete(sample);
        return completableFuture;
    }

}
