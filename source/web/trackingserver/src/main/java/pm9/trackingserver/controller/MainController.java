package pm9.trackingserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pm9.trackingserver.constants.EnumResponse;
import pm9.trackingserver.rest.request.DeviceLocationSubmissionRequest;
import pm9.trackingserver.rest.request.DeviceRegisterRequest;
import pm9.trackingserver.rest.request.DeviceUnregisterRequest;
import pm9.trackingserver.rest.request.GetDeviceLocationsRequest;
import pm9.trackingserver.rest.response.DeviceLocationResponse;
import pm9.trackingserver.rest.response.GenericResponse;
import pm9.trackingserver.service.MainControllerService;
import pm9.trackingserver.service.RedisService;

import java.util.Map;

/**
 * Contains all methods to process requests at /api/*.
 * It is a rest controller. It get JSON requests and returns JSON responses.
 */
@RestController
@RequestMapping(path = "/api")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private MainControllerService mainControllerService;

    /**
     * Processes POST requests at /submitLocation
     * @param deviceLocationSubmissionRequest Request packet to submit device location.
     * @return
     */
    @RequestMapping(value = "/submitLocation", method = RequestMethod.POST)
    public GenericResponse submitLocation(@RequestBody DeviceLocationSubmissionRequest deviceLocationSubmissionRequest) {
        return mainControllerService.submitLocation(deviceLocationSubmissionRequest);
    }

    /**
     * Processes POST requests at /getDeviceLocations
     * @param getDeviceLocationsRequest Request packet to get device locations.
     * @return map of devices and their locations along with their timestamps.
     */
    @RequestMapping(value = "/getDeviceLocations", method = RequestMethod.POST)
    public Map<String, DeviceLocationResponse> getDeviceLocations(@RequestBody GetDeviceLocationsRequest getDeviceLocationsRequest) {
        logger.debug("Requesting Device Location for API key: " + getDeviceLocationsRequest.getApiKey());
        return redisService.getAllDeviceLocations(getDeviceLocationsRequest.getApiKey());
    }

    /**
     * Processes POST requests at /registerDevice
     * @param deviceRegisterRequest Request packet to register device.
     * @return 'Success' if the device was unregistered successfully else return the error message.
     */
    @RequestMapping(value = "/registerDevice", method = RequestMethod.POST)
    public GenericResponse registerDevice(@RequestBody DeviceRegisterRequest deviceRegisterRequest) {
        return mainControllerService.registerDevice(deviceRegisterRequest);
    }

    /**
     * Processes POST requests at /unregisterDevice
     * @param deviceUnregisterRequest Request packet to unregister device.
     * @return 'Success' if the device was unregistered successfully else return the error message.
     */
    @RequestMapping(value = "/unregisterDevice", method = RequestMethod.POST)
    public GenericResponse unregisterDevice(@RequestBody DeviceUnregisterRequest deviceUnregisterRequest) {
        return mainControllerService.unregisterDevice(deviceUnregisterRequest);
    }

    /**
     * Returns status of the API, whether it is working or not.
     * @return 'Working' if the API is online.
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public GenericResponse status() {
        return EnumResponse.getGenericResponse(EnumResponse.WORKING);
    }
}
