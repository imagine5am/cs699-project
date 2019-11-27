package pm9.trackingserver.dao.impl;

import com.google.maps.model.LatLng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pm9.trackingserver.dao.OrganizationDao;
import pm9.trackingserver.dao.RedisRepository;
import pm9.trackingserver.entities.Device;
import pm9.trackingserver.entities.Organization;
import pm9.trackingserver.rest.response.DeviceLocationResponse;
import pm9.trackingserver.service.impl.MainControllerServiceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of RedisRepository. This class has all the methods
 * to interact with Redis repository.
 */
@Repository
public class RedisRepositoryImpl implements RedisRepository {

    @Value("${redis.expiry.seconds}")
    private int expiry_seconds;
    @Value("${redis.expire}")
    private boolean expire;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrganizationDao organizationDao;

    private static final Logger logger = LoggerFactory.getLogger(MainControllerServiceImpl.class);

    /**
     * Saves device location
     * @param apiKey
     * @param deviceId
     * @param latLng
     */
    @SuppressWarnings("unchecked")
    public void saveDeviceLocation(String apiKey, String deviceId, LatLng latLng) {
        DeviceLocationResponse deviceLocationResponse = new DeviceLocationResponse();
        deviceLocationResponse.setLatLng(latLng);
        deviceLocationResponse.setTimestamp(new Timestamp(System.currentTimeMillis()));
        getOpsForHash().put(apiKey, deviceId, deviceLocationResponse);
    }

    /**
     * Returns device locations of a particular organization.
     * @param apiKey String based on which organization is identified.
     * @return Map<String, DeviceLocationResponse> where key is the device name.
     */
    @SuppressWarnings("unchecked")
    public Map<String, DeviceLocationResponse> getAllDeviceLocations(String apiKey) {
        return (Map<String, DeviceLocationResponse>) getOpsForHash().entries(apiKey);
    }

    /**
     * Load 'organization' cache when the server is started. All Organization
     * instances are loaded in the cache.
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public void loadOrganizationCache() {
        for (Organization organization : organizationDao.findAll()) {
            getOpsForHash().put("organization", organization.getApiKey(), organization);
        }
        logger.info("Organization Cache Loaded");
    }

    /**
     * Gets organization instance from 'organization' cache.
     * @param apiKey Used to find Organization instance.
     * @return Organization instance.
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public Organization getOrganizationByApiKey(String apiKey) {
        return (Organization) getOpsForHash().get("organization", apiKey);
    }

    /**
     * Removes the device instance from 'organization' cache.
     * @param device Device instance which needs to be deleted.
     */
    @SuppressWarnings("unchecked")
    public void removeDevice(Device device) {
        Organization organization = updateOrganization(device.getOrganization());
        getOpsForHash().delete(organization.getApiKey(), device.getDeviceId());
    }

    /**
     * Updates the organization instance in 'organization' cache.
     * @param organization Organization instance which needs to be updated.
     * @return Required Organization instance.
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public Organization updateOrganization(Organization organization) {
        organization = organizationDao.findByApiKey(organization.getApiKey());
        insertOrganization(organization);
        return organization;
    }

    /**
     * Inserts the organization instance in 'organization' cache.
     * @param organization Organization instance which needs to be inserted.
     */
    public void insertOrganization(Organization organization) {
        getOpsForHash().put("organization", organization.getApiKey(), organization);
    }

    /**
     * Inserts device into the 'organization' cache using organization's API as key.
     * @param device The Device object which needs to be cached.
     */
    public void insertDevice(Device device) {
        Organization organization = device.getOrganization();
        organization = (Organization)getOpsForHash().get("organization", organization.getApiKey());
        List<Device> deviceList = organization.getDeviceList();
        if(deviceList == null) {
            deviceList = new ArrayList<>();
        }
        deviceList.add(device);
        organization.setDeviceList(deviceList);
        getOpsForHash().put("organization", organization.getApiKey(), organization);
    }

    /**
     * Returns HashOperations object which allows us to put and get objects from different caches.
     * @return HashOperations from the redisTemplate.
     */
    private HashOperations getOpsForHash() {
        return redisTemplate.opsForHash();
    }

}
