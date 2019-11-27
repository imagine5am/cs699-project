package pm9.trackingserver.service.impl;

import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pm9.trackingserver.dao.RedisRepository;
import pm9.trackingserver.entities.Device;
import pm9.trackingserver.entities.Organization;
import pm9.trackingserver.rest.response.DeviceLocationResponse;
import pm9.trackingserver.service.RedisService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of RedisService. Provides services related to Redis repository.
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisRepository redisRepository;

    @Value("${redis.expiry.seconds}")
    private int keyExpirySeconds;

    @Value("${redis.expire}")
    private boolean expireFlag;

    /**
     * Returns device locations of a particular organization.
     * @param apiKey String based on which organization is identified.
     * @return Map<String, DeviceLocationResponse> where key is the device name.
     */
    public Map<String, DeviceLocationResponse> getAllDeviceLocations(String apiKey) {
        Map<String, DeviceLocationResponse> deviceLocations = redisRepository.getAllDeviceLocations(apiKey);

        if(!expireFlag) return deviceLocations;

        Map<String, DeviceLocationResponse> map = new HashMap<>();
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, DeviceLocationResponse> entry : deviceLocations.entrySet()){
            long timestamp_value = entry.getValue().getTimestamp().getTime();
            if ((currentTime - timestamp_value) / 1000 < keyExpirySeconds) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
        /*
        return redisRepository.getAllDeviceLocations(apiKey).entrySet()
                .parallelStream()
                .filter(entry -> currentTime - entry.getValue().getValue().getTime() / 1000 >= keyExpirySeconds)
                .collect(Collectors.toMap(entry -> entry, entry -> entry.getValue().getKey(),(e1, e2) -> e2.get,LinkedHashMap::new));

         */
    }

    /**
     * Used to save a device location when it is submitted.
     * @param apiKey Based on this Organization is identified.
     * @param deviceId Based on this Device is identified.
     * @param latLng Location of the device.
     */
    public void saveDeviceLocation(String apiKey, String deviceId, LatLng latLng) {
        redisRepository.saveDeviceLocation(apiKey, deviceId, latLng);
    }

    /**
     * Finds organization by API key.
     * @param apiKey Based on this Organization is identified.
     * @return Required Organization instance.
     */
    public Organization getOrganizationByApiKey(String apiKey) {
        return redisRepository.getOrganizationByApiKey(apiKey);
    }

    /**
     * Removes device from the Redis repository.
     * @param device Instance which needs to be removed.
     */
    public void removeDevice(Device device) {
        redisRepository.removeDevice(device);
    }

    /**
     * Updates the organization instance in 'organization' cache.
     * @param organization Organization instance which needs to be updated.
     * @return Required Organization instance.
     */
    public Organization updateOrganization(Organization organization) {
        return redisRepository.updateOrganization(organization);
    }

    /**
     * Inserts the organization instance in 'organization' cache.
     * @param organization Organization instance which needs to be inserted.
     */
    public void insertOrganization(Organization organization) {
        redisRepository.insertOrganization(organization);
    }

    /**
     * Inserts device into the 'organization' cache using organization's API as key.
     * @param device The Device object which needs to be cached.
     */
    public void insertDevice(Device device) {
        redisRepository.insertDevice(device);
    }

    /**
     * Service method to load 'organization' cache when the server is started.
     * All Organization instances are loaded in the cache.
     */
    @PostConstruct
    public void initCache(){
        redisRepository.loadOrganizationCache();
    }
}
