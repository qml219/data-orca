package us.sportsanalytics.backend.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.stereotype.Component;

import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.services.sqs.model.Message;
import us.sportsanalytics.backend.services.ImportService;

@Component
@RequiredArgsConstructor
public class TableImportListener {

    private final ImportService importService;

    @SqsListener("table-import-queue")
    public void receiveMessage(Message message) {

        String sqsEventBody = message.body();

        S3EventNotification eventNotification = S3EventNotification.fromJson(sqsEventBody);
        try {
            importService.handleS3UploadEvent(eventNotification);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
