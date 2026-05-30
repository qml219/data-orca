package us.sportsanalytics.backend.controllers;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.TableDefinition;
import us.sportsanalytics.backend.models.dto.table.CreateTableRequest;
import us.sportsanalytics.backend.models.dto.table.ScanRequest;
import us.sportsanalytics.backend.models.dto.table.csv.CsvConfirmCreateTableRequest;
import us.sportsanalytics.backend.models.dto.table.csv.CsvInitImportResponse;
import us.sportsanalytics.backend.models.dto.table.csv.CsvScanResponse;
import us.sportsanalytics.backend.services.ImportService;
import us.sportsanalytics.backend.services.TableService;
import us.sportsanalytics.backend.services.persistence.csv.CsvIngestionService;

@RestController
@RequestMapping("/api/tables/import")
@RequiredArgsConstructor
public class ImportTableController {

    private final CsvIngestionService csvIngestionService;
    private final TableService tableService;
    private final ImportService importService;

    @PostMapping(value = "/init", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CsvInitImportResponse initiateImport(
            @RequestBody @Valid CsvInitImportRequest request) {
        return importService.initiate(request.getOriginalFileName());
    }

    @PostMapping(value = "/scan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CsvScanResponse scan(@RequestBody ScanRequest request) throws IOException {
        return importService.scanFromUploadSession(request.uploadSessionId(), request.tableName());
    }

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TableDefinition confirmAndCreate(@RequestBody @Valid ConfirmPayload payload) {
        CsvScanResponse scan = payload.getScan();
        CsvConfirmCreateTableRequest confirm = payload.getConfirm();

        CreateTableRequest createReq = csvIngestionService.toCreateTableRequest(scan, confirm);

        return tableService.saveTable(createReq);
    }

    @Data
    public static class CsvInitImportRequest {
        private String originalFileName;
    }

    @Data
    public static class ConfirmPayload {
        private CsvScanResponse scan;
        private CsvConfirmCreateTableRequest confirm;
    }

}
