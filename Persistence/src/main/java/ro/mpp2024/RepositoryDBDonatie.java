package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class RepositoryDBDonatie implements IDonatieRepository{
    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    private Connection connection;

    public RepositoryDBDonatie(Properties properties) {
        logger.info("Initializing RepositoryDBDonatie");
        dbUtils = new JdbcUtils(properties);
        connection = dbUtils.getConnection();
    }


    @Override
    public Optional<Donatie> findOne(Long aLong) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Iterable<Donatie> findAll() throws SQLException {
        logger.traceEntry();
        List<Donatie> donatii = new ArrayList<>();
            try (PreparedStatement statement = this.connection.prepareStatement("select * from Donatie")){
                 ResultSet result = statement.executeQuery();
                while (result.next()) {
                    Long id = result.getLong("id");
                    Long idDonator = result.getLong("id_donator");
                    Long idCazCaritabil = result.getLong("id_caz_caritabil");
                    Integer suma = result.getInt("suma");
                    Donator donator = findDonator(idDonator);
                    CazCaritabil cazCaritabil = findCazCaritabil(idCazCaritabil);
                    Donatie donatie = new Donatie(cazCaritabil, donator, suma);
                    donatie.setId(id);
                    donatii.add(donatie);
                }
            }
         catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
            return donatii;
        }

    @Override
    public Optional<Donatie> save(Donatie entity) throws SQLException {
        logger.traceEntry("saving donatie {}", entity);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("insert into Donatie (id_donator, id_caz_caritabil, suma) values (?, ?, ?)")) {
            statement.setLong(1, entity.getDonator().getId());
            statement.setLong(2, entity.getCazCaritabil().getId());
            statement.setInt(3, entity.getSuma());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.empty() : Optional.of(entity);

        }
        catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Donatie> delete(Long aLong) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<Donatie> update(Donatie entity) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Donator findDonator(Long id) {
        Donator donor = null;
            String sql = "select * from Donatori where id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                ResultSet result = preparedStatement.executeQuery();
                if (result.next()) {
                    String nume = result.getString("nume");
                    String prenume = result.getString("prenume");
                    String adresa = result.getString("adresa");
                    String cnp = result.getString("cnp");
                    String nr_telefon = result.getString("nr_telefon");
                    Donator donator = new Donator(nume, prenume, adresa, cnp, nr_telefon);
                    donator.setId(id);
                    donor = new Donator(nume, prenume, adresa, cnp, nr_telefon);
                    donor.setId(id);
                }
            }
         catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return donor;

    }

    @Override
    public CazCaritabil findCazCaritabil(Long id) {
        logger.traceEntry();
        CazCaritabil caz = null;
            String sql = "select * from CazCaritabil where id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                ResultSet result = preparedStatement.executeQuery();
                if (result.next()) {
                    Long cazId = result.getLong("id");
                    String nume = result.getString("nume_caz");
                    String asociatie = result.getString("asociatie");
                    caz = new CazCaritabil(nume, asociatie);
                    caz.setId(id);
                }
            }
         catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return caz;
    }

    @Override
    public Float getDonationSumforCazCaritabilbyName(String name) {
        logger.traceEntry();
        Float sum = 0f;
            String sql = "select sum(suma) from Donatie d join CazCaritabil c on d.id_caz_caritabil = c.id where c.nume_caz = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                ResultSet result = preparedStatement.executeQuery();
                if (result.next()) {
                    sum = result.getFloat(1);
                }
            }
         catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sum;
    }

}


