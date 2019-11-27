package pm9.trackingserver.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pm9.trackingserver.constants.EnumResponse;
import pm9.trackingserver.dao.DeviceDao;
import pm9.trackingserver.dao.OrganizationDao;
import pm9.trackingserver.entities.Device;
import pm9.trackingserver.entities.Organization;
import pm9.trackingserver.rest.request.DeviceLocationSubmissionRequest;
import pm9.trackingserver.rest.request.DeviceRegisterRequest;
import pm9.trackingserver.rest.request.DeviceUnregisterRequest;
import pm9.trackingserver.rest.response.GenericResponse;
import pm9.trackingserver.service.MainControllerService;
import pm9.trackingserver.service.RedisService;

/**
 * Implementation of MainControllerService. Provides service methods for MainController.
 */
@Service
public class MainControllerServiceImpl implements MainControllerService {
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private OrganizationDao organizationDao;
    @Autowired
    private RedisService redisService;

    private static final Logger logger = LoggerFactory.getLogger(MainControllerServiceImpl.class);

    /**
     * Used to register a new device on the tracking server.
     * @param deviceRegisterRequest Request packet to register device.
     * @return EnumResponse as GenericResponse.
     */
    @Override
    public GenericResponse registerDevice(DeviceRegisterRequest deviceRegisterRequest) {
        try {
            String apiKey = deviceRegisterRequest.getApiKey();
            Organization organization = redisService.getOrganizationByApiKey(apiKey);
            String deviceId = deviceRegisterRequest.getDeviceId();

            if(organization == null) {
                return EnumResponse.getGenericResponse(EnumResponse.WRONG_API_KEY);
            }

            Boolean devicePresent = false;
            for(Device device: deviceDao.findAll()) {
                if(device.getDeviceId().equals(deviceId)) {
                    devicePresent = true;
                }
            }

            if(devicePresent) {
                return EnumResponse.getGenericResponse(EnumResponse.DEVICE_ALREADY_REGISTERED);
            } else {
                Device device = new Device();
                device.setDeviceId(deviceId);
                device.setOrganization(organization);
                device = deviceDao.save(device);
                redisService.insertDevice(device);
                return EnumResponse.getGenericResponse(EnumResponse.SUCCESS);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return EnumResponse.getGenericResponse(EnumResponse.DEVICE_REGISTER_FAILURE);
    }

    /**
     * Used to unregister a device from the tracking server.
     * @param deviceUnregisterRequest Request packet to unregister device.
     * @return EnumResponse as GenericResponse.
     */
    @Override
    public GenericResponse unregisterDevice(DeviceUnregisterRequest deviceUnregisterRequest) {
        try {
            String deviceId = deviceUnregisterRequest.getDeviceId();
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device != null) {
                deviceDao.delete(device);
                redisService.removeDevice(device);
                return EnumResponse.getGenericResponse(EnumResponse.SUCCESS);
            } else {
                return EnumResponse.getGenericResponse(EnumResponse.DEVICE_NOT_FOUND);
            }
        } catch (Exception e){
            logger.error(e.toString());
        }
        return EnumResponse.getGenericResponse(EnumResponse.DEVICE_UNREGISTER_FAILURE);
    }

    /**
     * Used to by a device to submit its location.
     * @param deviceLocationSubmissionRequest Request packet to submit device location.
     * @return EnumResponse as GenericResponse.
     */
    @Override
    public GenericResponse submitLocation(DeviceLocationSubmissionRequest deviceLocationSubmissionRequest) {
        logger.debug(deviceLocationSubmissionRequest.getApiKey());
        logger.debug(deviceLocationSubmissionRequest.getDeviceId());
        logger.debug("" + deviceLocationSubmissionRequest.getLatLng().lat);
        logger.debug("" + deviceLocationSubmissionRequest.getLatLng().lng);
        String apiKey = deviceLocationSubmissionRequest.getApiKey();
        String deviceId = deviceLocationSubmissionRequest.getDeviceId();
        Organization organization = redisService.getOrganizationByApiKey(apiKey);
        if(organization == null) {
            return EnumResponse.getGenericResponse(EnumResponse.WRONG_API_KEY);
        }
        for(Device device: organization.getDeviceList()) {
            if(device.getDeviceId().equals(deviceId)) {
                redisService.saveDeviceLocation(apiKey, deviceId, deviceLocationSubmissionRequest.getLatLng());
                return EnumResponse.getGenericResponse(EnumResponse.SUCCESS);
            }
        }

        return EnumResponse.getGenericResponse(EnumResponse.DEVICE_NOT_FOUND);
    }
}
