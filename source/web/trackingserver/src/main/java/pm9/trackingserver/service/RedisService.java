package pm9.trackingserver.service;

import com.google.maps.model.LatLng;
import pm9.trackingserver.entities.Device;
import pm9.trackingserver.entities.Organization;
import pm9.trackingserver.rest.response.DeviceLocationResponse;

import java.util.Map;

public interface RedisService {
    Map<String, DeviceLocationResponse> getAllDeviceLocations(String apiKey);

    void saveDeviceLocation(String apiKey, String deviceId, LatLng latLng);

    Organization getOrganizationByApiKey(String apiKey);

    void removeDevice(Device device);

    Organization updateOrganization(Organization organization);

    void insertOrganization(Organization organization);

    void insertDevice(Device device);

    void initCache();
}
