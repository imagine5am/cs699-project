package pm9.trackingserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pm9.trackingserver.dao.OrganizationDao;
import pm9.trackingserver.entities.Organization;

import java.util.HashSet;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    OrganizationDao organizationDao;

    /**
     * Creates and returns org.springframework.security.core.userdetails.UserDetails for given username.
     * @param username Identifier for user which needs to be loaded.
     * @return org.springframework.security.core.userdetails.UserDetails for a given user.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Organization user = organizationDao.findByName(username);
        if (user == null) throw new UsernameNotFoundException(username);

        /*
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        new User(user.getName(), user.getPassword(), grantedAuthorities);
        */

        return new User(user.getName(), user.getPassword(), new HashSet<GrantedAuthority>());
    }
}
