package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
@Component
public class RepositoryDBCazCaritabil implements ICazCaritabilRepository {

    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public RepositoryDBCazCaritabil(Properties props) {
        logger.info("Initializing RepositoryDBCazCaritabil");
        dbUtils = new JdbcUtils(props);
    }


    @Override
    public Optional<CazCaritabil> findOne(Long aLong) throws SQLException {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("select * from CazCaritabil where id = ?")) {
            statement.setLong(1, aLong);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    String nume = result.getString("nume_caz");
                    String asociatie = result.getString("asociatie");
                    CazCaritabil caz = new CazCaritabil(nume, asociatie);
                    caz.setId(id);
                    return Optional.of(caz);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<CazCaritabil> findAll() throws SQLException {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<CazCaritabil> cazuri = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from CazCaritabil");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Long id = result.getLong("id");
                String nume = result.getString("nume_caz");
                String asociatie = result.getString("asociatie");
                CazCaritabil caz = new CazCaritabil(nume, asociatie);
                caz.setId(id);
                cazuri.add(caz);
            }
        }
        return cazuri;
    }

    @Override
    public Optional<CazCaritabil> save(CazCaritabil entity) throws SQLException {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("insert into CazCaritabil (nume_caz, asociatie) values (?, ?)")) {
            statement.setString(1, entity.getNume_caz());
            statement.setString(2, entity.getAsociatie());
            int result = statement.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));
                    }
                }
            }
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<CazCaritabil> delete(Long aLong) throws SQLException {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("delete from CazCaritabil where id = ?")) {
            statement.setLong(1, aLong);
            int result = statement.executeUpdate();
            if (result > 0) {
                return Optional.of(new CazCaritabil());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<CazCaritabil> update(CazCaritabil entity) throws SQLException {
            logger.traceEntry();
            Connection connection = dbUtils.getConnection();
            try (PreparedStatement statement = connection.prepareStatement("update CazCaritabil set nume_caz = ?, asociatie = ? where id = ?")) {
                statement.setString(1, entity.getNume_caz());
                statement.setString(2, entity.getAsociatie());
                statement.setLong(3, entity.getId());
                int result = statement.executeUpdate();
                if (result > 0) {
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
    }
}
