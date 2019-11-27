package pm9.trackingserver.dao;

import org.springframework.data.repository.CrudRepository;
import pm9.trackingserver.entities.Device;

public interface DeviceDao extends CrudRepository<Device, Long> {
    Device findByDeviceId(String deviceId);


}
