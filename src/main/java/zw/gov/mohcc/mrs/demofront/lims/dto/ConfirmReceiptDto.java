package zw.gov.mohcc.mrs.demofront.lims.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmReceiptDto {
    
    @NotNull
    private LocalDateTime dateReceived;
    @NotNull
    private LocalDateTime dateReceivedAtHub;
    
}
