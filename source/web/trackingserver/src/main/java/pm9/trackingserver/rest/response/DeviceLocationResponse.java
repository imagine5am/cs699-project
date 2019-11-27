package pm9.trackingserver.rest.response;

import com.google.maps.model.LatLng;

import java.io.Serializable;
import java.sql.Timestamp;

public class DeviceLocationResponse implements Serializable {
    private LatLng latLng;
    private Timestamp timestamp;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
