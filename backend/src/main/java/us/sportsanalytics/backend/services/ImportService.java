package us.sportsanalytics.backend.services;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import us.sportsanalytics.backend.models.domain.csv.UploadSession;
import us.sportsanalytics.backend.models.domain.csv.UploadStatus;
import us.sportsanalytics.backend.models.dto.table.csv.CsvInitImportResponse;
import us.sportsanalytics.backend.models.dto.table.csv.CsvScanResponse;
import us.sportsanalytics.backend.repositories.csv.UploadSessionRepository;
import us.sportsanalytics.backend.security.workspace.WorkspaceContext;
import us.sportsanalytics.backend.services.persistence.csv.CsvIngestionService;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord;

@Service
@RequiredArgsConstructor
@Transactional
public class ImportService {

    private final S3StorageService storageService;
    private final CsvIngestionService csvIngestionService;
    private final UploadSessionRepository uploadSessionRepository;
    private final WorkspaceContext workspaceContext;

    public CsvInitImportResponse initiate(String fileName) {

        UUID uploadSessionId = UUID.randomUUID();
        UUID workspaceId = workspaceContext.getWorkspaceId();

        String derivedS3Key = String.format("table/imports/%s/%s.csv", workspaceId, uploadSessionId);

        UploadSession session = UploadSession.builder().id(uploadSessionId).workspaceId(workspaceId).s3Key(derivedS3Key)
                .originalFileName(fileName)
                .status(UploadStatus.INITIATED)
                .createdAt(Instant.now()).build();

        uploadSessionRepository.save(session);

        String uploadURL = storageService.createPresignedPutUrl(derivedS3Key,
                Map.of("uploadSessionId", uploadSessionId.toString()));

        return new CsvInitImportResponse(uploadSessionId, uploadURL);
    }

    public CsvScanResponse scan(MultipartFile file, String tableNameOverride) throws IOException {
        return csvIngestionService.scanCsv(file, tableNameOverride);
    }

    @Transactional
    public void handleS3UploadEvent(S3EventNotification eventNotification) {
        List<S3EventNotificationRecord> records = eventNotification.getRecords();

        S3EventNotificationRecord record = records.stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No S3 event notification record found"));

        String[] keySplit = record.getS3().getObject().getKey().split("/");
        String fileIdentifier = keySplit[keySplit.length - 1];
        UUID uploadSessionId = UUID.fromString(fileIdentifier.substring(0, fileIdentifier.lastIndexOf(".csv")));

        updateUploadSessionStatus(uploadSessionId, UploadStatus.UPLOADED);

    }

    @Transactional
    public void updateUploadSessionStatus(UUID uploadSessionId, UploadStatus status) {
        UploadSession session = uploadSessionRepository.findById(uploadSessionId)
                .orElseThrow(() -> new EntityNotFoundException("Upload Session not found"));
        if (session.getStatus() != status) {
            session.setStatus(status);
        }
    }

}
