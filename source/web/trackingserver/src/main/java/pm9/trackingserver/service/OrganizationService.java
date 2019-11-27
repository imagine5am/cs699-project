package pm9.trackingserver.service;

import pm9.trackingserver.entities.Organization;

public interface OrganizationService {
    Organization findByName(String name);

    void createOrganization(Organization organization);

    public Organization findByApiKey(String apiKey);
}
