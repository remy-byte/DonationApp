package ro.mpp2024;

import org.hibernate.Session;

import java.sql.SQLException;
import java.util.Optional;

public class RepositoryHibernateVoluntar implements IVoluntarRepository{
    @Override
    public Voluntar findVoluntarByUsernameandPassword(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Voluntar where username=:username and parola=:password", Voluntar.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResultOrNull();
        }
    }

    @Override
    public Optional<Voluntar> findOne(Long aLong) throws SQLException {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.createSelectionQuery("from Voluntar where id=:idM ", Voluntar.class)
                    .setParameter("idM", aLong)
                    .getSingleResultOrNull());
        }
    }

    @Override
    public Iterable<Voluntar> findAll() throws SQLException {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Voluntar", Voluntar.class)
                    .list();
        }
    }

    @Override
    public Optional<Voluntar> save(Voluntar entity) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<Voluntar> delete(Long aLong) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<Voluntar> update(Voluntar entity) throws SQLException {
        return Optional.empty();
    }
}
