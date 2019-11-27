package pm9.trackingserver.dao;

import org.hibernate.Session;

import java.util.List;

public interface BaseDao {
    Session getSession();

    Object save(Object entity);

    void saveOrUpdate(Object entity);

    void delete(Object object);

    <T> List<T> getAll(Class<T> class_);
}
