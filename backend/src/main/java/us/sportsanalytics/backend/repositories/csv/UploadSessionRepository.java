package us.sportsanalytics.backend.repositories.csv;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import us.sportsanalytics.backend.models.domain.csv.UploadSession;

public interface UploadSessionRepository extends JpaRepository<UploadSession, UUID> {
    List<UploadSession> findByWorkspaceId(UUID workspaceId);

    Optional<UploadSession> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
