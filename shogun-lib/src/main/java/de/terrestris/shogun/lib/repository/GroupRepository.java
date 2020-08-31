package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository
    extends BaseCrudRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    Optional<Group> findByKeycloakId(String keycloakId);

}
