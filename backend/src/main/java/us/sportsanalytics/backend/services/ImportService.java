package us.sportsanalytics.backend.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord;
import us.sportsanalytics.backend.models.domain.csv.UploadSession;
import us.sportsanalytics.backend.models.domain.csv.UploadStatus;
import us.sportsanalytics.backend.models.domain.csv.UploadType;
import us.sportsanalytics.backend.models.dto.table.csv.CsvInitImportResponse;
import us.sportsanalytics.backend.models.dto.table.csv.CsvScanResponse;
import us.sportsanalytics.backend.repositories.csv.UploadSessionRepository;
import us.sportsanalytics.backend.security.workspace.WorkspaceContext;
import us.sportsanalytics.backend.services.persistence.csv.CsvIngestionService;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final S3StorageService storageService;
    private final CsvIngestionService csvIngestionService;
    private final UploadSessionRepository uploadSessionRepository;
    private final WorkspaceContext workspaceContext;
    private final ObjectMapper objectMapper;

    @Transactional
    public CsvInitImportResponse initiate(String fileName) {

        UUID uploadSessionId = UUID.randomUUID();
        UUID workspaceId = workspaceContext.getWorkspaceId();

        String derivedS3Key = String.format(
                "table/imports/%s/%s.csv",
                workspaceId,
                uploadSessionId);

        UploadSession session = UploadSession.builder()
                .id(uploadSessionId)
                .workspaceId(workspaceId)
                .s3Key(derivedS3Key)
                .originalFileName(fileName)
                .type(UploadType.CSV_TABLE)
                .status(UploadStatus.INITIATED)
                .createdAt(Instant.now())
                .build();

        uploadSessionRepository.save(session);

        String uploadURL = storageService.createPresignedPutUrl(
                derivedS3Key,
                Map.of("uploadSessionId", uploadSessionId.toString()));

        return new CsvInitImportResponse(uploadSessionId, uploadURL);
    }

    public CsvScanResponse scanFromUploadSession(UUID uploadSessionId, String tableNameOverride) throws IOException {

        UUID workspaceId = workspaceContext.getWorkspaceId();

        UploadSession session = uploadSessionRepository
                .findByIdAndWorkspaceId(uploadSessionId, workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Upload session not found"));

        updateStatus(uploadSessionId, UploadStatus.SCANNING);

        String tableName = (tableNameOverride != null && !tableNameOverride.isBlank())
                ? tableNameOverride
                : CsvIngestionService.stripExtension(session.getOriginalFileName());

        CsvScanResponse result;

        try (InputStream inputStream = storageService.getObjectStream(session.getS3Key())) {
            result = csvIngestionService.scanCsv(inputStream, tableName);
        } catch (Exception e) {
            updateStatus(uploadSessionId, UploadStatus.FAILED);
            throw e;
        }

        persistScanResult(uploadSessionId, result);

        return result;
    }

    public CsvScanResponse scanFromS3URL(URL getUrl, String tableName) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("GET");
        try (InputStream fileContentStream = connection.getInputStream()) {
            return csvIngestionService.scanCsv(fileContentStream, tableName);
        }
    }

    public CsvScanResponse scanFromS3(String keyName, String tableName) throws IOException {
        try (InputStream fileContentStream = storageService.getObjectStream(keyName)) {
            return csvIngestionService.scanCsv(fileContentStream, tableName);
        }
    }

    public CsvScanResponse scan(MultipartFile file, String tableNameOverride) throws IOException {

        String suggestedTableName = (tableNameOverride != null && !tableNameOverride.isBlank())
                ? tableNameOverride
                : CsvIngestionService.stripExtension(
                        Objects.requireNonNullElse(
                                file.getOriginalFilename(),
                                "uploaded_table"));

        try (InputStream fileContentStream = file.getInputStream()) {
            return csvIngestionService.scanCsv(fileContentStream, suggestedTableName);
        }
    }

    public void handleS3UploadEvent(S3EventNotification eventNotification)
            throws IOException, URISyntaxException {

        List<S3EventNotificationRecord> records = eventNotification.getRecords();

        S3EventNotificationRecord record = records.stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No S3 event notification record found"));

        String keyName = record.getS3().getObject().getKey();

        String[] keySplit = keyName.split("/");
        String fileIdentifier = keySplit[keySplit.length - 1];

        UUID uploadSessionId = UUID.fromString(
                fileIdentifier.substring(0, fileIdentifier.lastIndexOf(".csv")));

        updateStatus(uploadSessionId, UploadStatus.UPLOADED);
    }

    public CsvScanResponse getScanResult(UUID id) {

        UUID workspaceId = workspaceContext.getWorkspaceId();

        UploadSession session = uploadSessionRepository
                .findByIdAndWorkspaceId(id, workspaceId)
                .orElseThrow();

        if (session.getStatus() != UploadStatus.SCANNED) {
            throw new IllegalStateException("Scan not completed");
        }

        try {
            return objectMapper.readValue(
                    session.getScanResultJson(),
                    CsvScanResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize scan result", e);
        }
    }

    @Transactional
    protected void updateStatus(UUID uploadSessionId, UploadStatus status) {

        UploadSession session = uploadSessionRepository
                .findById(uploadSessionId)
                .orElseThrow(() -> new EntityNotFoundException("Upload Session not found"));

        session.setStatus(status);
    }

    @Transactional
    protected void persistScanResult(UUID uploadSessionId, CsvScanResponse result) {

        UploadSession session = uploadSessionRepository
                .findById(uploadSessionId)
                .orElseThrow(() -> new EntityNotFoundException("Upload Session not found"));

        try {
            session.setScanResultJson(objectMapper.writeValueAsString(result));
            session.setStatus(UploadStatus.SCANNED);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize scan result", e);
        }
    }
}