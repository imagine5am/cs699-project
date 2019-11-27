package pm9.trackingserver.dao;

import org.springframework.data.repository.CrudRepository;
import pm9.trackingserver.entities.Organization;

public interface OrganizationDao extends CrudRepository<Organization, Long> {
    Organization findByApiKey(String apiKey);

    Organization findByName(String name);
}
