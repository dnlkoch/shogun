package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionCollectionRepository
    extends BaseCrudRepository<PermissionCollection, Long>,
    JpaSpecificationExecutor<PermissionCollection> {

    Optional<PermissionCollection> findByName(PermissionCollectionType name);

}
