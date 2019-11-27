package pm9.trackingserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pm9.trackingserver.dao.OrganizationDao;
import pm9.trackingserver.entities.Organization;
import pm9.trackingserver.service.OrganizationService;
import pm9.trackingserver.service.RedisService;

import java.util.UUID;

/**
 * Implementation of OrganizationService. Provides services related to Organization.
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Organization findByName(String name) {
        Organization organization = null;
        try {
            organization = organizationDao.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return organization;
    }

    @Override
    public void createOrganization(Organization organization) {
        organization.setPassword(bCryptPasswordEncoder.encode(organization.getPassword()));
        String apiKey = UUID.randomUUID().toString();
        // Generates UUID if UUID is already any API key.
        while(findByApiKey(apiKey) != null) apiKey = UUID.randomUUID().toString();
        organization.setApiKey(apiKey);
        organization = organizationDao.save(organization);
        redisService.insertOrganization(organization);
    }

    @Override
    public Organization findByApiKey(String apiKey) {
        Organization organization = null;
        try {
            organization = organizationDao.findByApiKey(apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return organization;
    }
}
