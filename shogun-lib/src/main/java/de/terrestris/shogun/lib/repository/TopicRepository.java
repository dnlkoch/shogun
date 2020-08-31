package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Topic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository
    extends BaseCrudRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    Optional<Topic> findByTitle(String title);

}
