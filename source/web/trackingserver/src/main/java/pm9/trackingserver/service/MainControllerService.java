package pm9.trackingserver.service;

import pm9.trackingserver.rest.request.DeviceLocationSubmissionRequest;
import pm9.trackingserver.rest.request.DeviceRegisterRequest;
import pm9.trackingserver.rest.request.DeviceUnregisterRequest;
import pm9.trackingserver.rest.response.GenericResponse;

public interface MainControllerService {
    GenericResponse registerDevice(DeviceRegisterRequest deviceRegisterRequest);

    GenericResponse unregisterDevice(DeviceUnregisterRequest deviceUnregisterRequest);

    GenericResponse submitLocation(DeviceLocationSubmissionRequest deviceLocationSubmissionRequest);
}
