package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


public class RepositoryDBVoluntar implements IVoluntarRepository{

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();


    Connection connection;
    public RepositoryDBVoluntar(Properties properties) {
        logger.info("Initializing RepositoryDBVoluntar with properties: {} ",properties);
        dbUtils=new JdbcUtils(properties);
        connection = dbUtils.getConnection();
    }


    @Override
    public Optional<Voluntar> findOne(Long aLong) throws SQLException {
        logger.traceEntry();
        try (var statement = connection.prepareStatement("select * from Voluntari where id = ?")) {
            statement.setLong(1, aLong);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String nume = result.getString("nume");
                    String prenume = result.getString("prenume");
                    String username = result.getString("username");
                    String parola = result.getString("parola"); // parola = password
                    Voluntar voluntar = new Voluntar(nume, prenume, username, parola);
                    voluntar.setId(id);
                    return Optional.of(voluntar);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Voluntar> findAll() throws SQLException {
        logger.traceEntry();
        List<Voluntar> voluntari = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("select * from Voluntari");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Long id = result.getLong("id");
                String nume = result.getString("nume");
                String prenume = result.getString("prenume");
                String username = result.getString("username");
                String parola = result.getString("parola"); // parola = password
                Voluntar voluntar = new Voluntar(nume, prenume, username, parola);
                voluntar.setId(id);
                voluntari.add(voluntar);
            }
        }
        catch (SQLException e) {
            logger.error(e);
        }
        return voluntari;
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

    @Override
    public Voluntar findVoluntarByUsernameandPassword(String username, String password) {
        logger.traceEntry("finding user with username {}", username);
        try (var statement = connection.prepareStatement("select * from Voluntar where username = ? and parola = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String nume = result.getString("nume");
                    String prenume = result.getString("prenume");
                    String parola = result.getString("parola"); // parola = password
                    Voluntar voluntar = new Voluntar(nume, prenume, username, parola);
                    voluntar.setId(id);
                    return voluntar;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }
}
