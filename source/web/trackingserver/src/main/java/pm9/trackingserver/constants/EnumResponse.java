package pm9.trackingserver.constants;

import pm9.trackingserver.rest.response.GenericResponse;

/**
 * Enum of common responses returned by the API.
 */
public enum EnumResponse {
    WORKING(100l, "Working"),
    SUCCESS(200l, "Success"),
    WRONG_API_KEY(500l, "Wrong API KEY"),
    DEVICE_REGISTER_FAILURE(510l, "Device not registered"),
    DEVICE_NOT_FOUND(520l, "Device not found"),
    DEVICE_UNREGISTER_FAILURE(530l, "Device not unregistered"),
    DEVICE_ALREADY_REGISTERED(540l, "Device already registered");

    /// Used to identify the response.
    private Long id;
    /// Contains details of the response.
    private String message;

    EnumResponse(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * This function returns a GenericResponse which contains the information of EnumResponse object.
     * @param enumResponse EnumResponse which needs to returned.
     * @return Packs EnumResponse as GenericResponse.
     */
    public static GenericResponse getGenericResponse(EnumResponse enumResponse){
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setStatus(enumResponse.getId());
        genericResponse.setMessage(enumResponse.getMessage());
        return genericResponse;
    }
}
