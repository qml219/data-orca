package us.sportsanalytics.backend.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.csv.UploadStatus;
import us.sportsanalytics.backend.models.dto.table.csv.CsvScanResponse;
import us.sportsanalytics.backend.repositories.csv.UploadSessionRepository;
import us.sportsanalytics.backend.security.workspace.WorkspaceContext;
import us.sportsanalytics.backend.services.ImportService;

@RestController
@RequestMapping("api/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final WorkspaceContext workspaceContext;
    private final UploadSessionRepository uploadSessionRepository;
    private final ImportService importService;

    // Later create an UploadService for generic upload details polling instead of
    // making direct calls to uploadSessionRepository
    @GetMapping("/{id}")
    public UploadStatus getStatus(@PathVariable UUID id) {
        UUID workspaceId = workspaceContext.getWorkspaceId();

        return uploadSessionRepository.findByIdAndWorkspaceId(id, workspaceId).orElseThrow().getStatus();
    }

    @GetMapping("/{id}/scanresult")
    public CsvScanResponse getScanResult(@PathVariable UUID id) {
        return importService.getScanResult(id);
    }
}
