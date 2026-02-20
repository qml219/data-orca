package us.sportsanalytics.backend.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.TableDefinition;
import us.sportsanalytics.backend.models.dto.table.CreateTableRequest;
import us.sportsanalytics.backend.security.CustomUserDetails;
import us.sportsanalytics.backend.security.workspace.WorkspaceContext;
import us.sportsanalytics.backend.services.TableService;
import us.sportsanalytics.backend.services.WorkspaceService;
import us.sportsanalytics.backend.services.persistence.csv.CsvIngestionService;

@RestController
@RequestMapping("api/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;
    private final CsvIngestionService csvIngestionService;

    @PostMapping("/create")
    public TableDefinition createTable(@RequestBody CreateTableRequest request) {
        return tableService.saveTable(request);
    }

    @GetMapping("")
    public List<TableDefinition> getAllTables() {
        return tableService.getAllTablesMetadatas();
    }

}
