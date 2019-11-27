package pm9.trackingserver.dao.impl;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pm9.trackingserver.dao.BaseDao;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Implementation of BaseDao. It provides common methods to interact with the MySQL database.
 * There is currently no need for this. We are using CRUDRepository to perform database operations,
 * and not this class.
 */
@Primary
@Repository
public class BaseDaoImpl implements BaseDao {
    @Autowired
    EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    @Override
    public Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    @Transactional
    public Object save(Object entity) {
        Session session = getSession();
        Object updatedEntity = null;
        try {
            updatedEntity = session.merge(entity);
            session.flush();
        } catch (Exception exception) {
            logger.error("exception occurred in BaseDao save ",exception);
        }
        return updatedEntity;
    }

    @Override
    @Transactional
    public void saveOrUpdate(Object entity) {
        getSession().saveOrUpdate(entity);
    }

    @Override
    @Transactional
    public void delete(Object object){
        Session session = getSession();
        session.delete(object);
        //session.flush();
        //session.clear();
    }

    @Override
    @Transactional
    public <T> List<T> getAll(Class<T> class_){
        Session session = getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(class_);
        criteria.from(class_);
        List<T> list = session.createQuery(criteria).getResultList();
        return list;
    }

}
