package pm9.trackingserver.service;

public interface SecurityService {
    void autoLogin(String username, String password);

    String findLoggedInUsername();

}
