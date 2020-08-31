package de.terrestris.shogun.lib.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseFileRepository<T, ID> extends BaseCrudRepository<T, ID> {

    Optional<T> findByFileUuid(UUID uuid);

}
