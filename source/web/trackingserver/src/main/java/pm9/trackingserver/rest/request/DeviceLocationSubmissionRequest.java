package pm9.trackingserver.rest.request;

import com.google.maps.model.LatLng;

public class DeviceLocationSubmissionRequest {
    private String apiKey;
    private String deviceId;
    private LatLng latLng;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
