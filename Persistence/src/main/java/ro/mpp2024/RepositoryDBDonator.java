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

public class RepositoryDBDonator implements IDonatorRepository{

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger(RepositoryDBDonator.class);

    public RepositoryDBDonator(Properties properties) {
        logger.info("Initializing RepositoryDBDonator with properties: {} ",properties);
        dbUtils=new JdbcUtils(properties);
    }

    @Override
    public Optional<Donator> findOne(Long aLong) throws SQLException {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement statement = connection.prepareStatement("select * from Donatori")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Long id = result.getLong("id");
                String nume = result.getString("nume");
                String prenume = result.getString("prenume");
                String adresa = result.getString("adresa");
                String cnp = result.getString("cnp");
                String nr_telefon = result.getString("nr_telefon");
                Donator donator = new Donator(nume, prenume, adresa, cnp, nr_telefon);
                donator.setId(id);
                return Optional.of(donator);
            }
        }
        catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Donator> findAll() throws SQLException {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Donator> donatori = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement("select * from Donatori")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Long id = result.getLong("id");
                String nume = result.getString("nume");
                String prenume = result.getString("prenume");
                String adresa = result.getString("adresa");
                String cnp = result.getString("cnp");
                String nr_telefon = result.getString("nr_telefon");
                Donator donator = new Donator(nume, prenume, cnp,adresa ,nr_telefon);
                donator.setId(id);
                donatori.add(donator);
            }
        }
        catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        return donatori;
    }

    @Override
    public Optional<Donator> save(Donator entity) throws SQLException {
        logger.traceEntry("saving donator {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into Donatori (nume, prenume, adresa, cnp, nr_telefon) values (?,?,?,?,?)")){
            preStmt.setString(1,entity.getNume());
            preStmt.setString(2,entity.getPrenume());
            preStmt.setString(3,entity.getAdresa());
            preStmt.setString(4,entity.getCnp());
            preStmt.setString(5,entity.getNr_telefon());
            int result = preStmt.executeUpdate();
            return result == 0 ? Optional.empty() : Optional.of(entity);
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Donator> delete(Long aLong) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<Donator> update(Donator entity) throws SQLException {
        logger.traceEntry("updating donator with id {} ",entity.getId());
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("update Donatori set nume=?, prenume=?, adresa=?, cnp=?, nr_telefon=? where id=?")){
            preStmt.setString(1,entity.getNume());
            preStmt.setString(2,entity.getPrenume());
            preStmt.setString(3,entity.getAdresa());
            preStmt.setString(4,entity.getCnp());
            preStmt.setString(5,entity.getNr_telefon());
            preStmt.setLong(6,entity.getId());
            int result=preStmt.executeUpdate();
            return result == 0 ? Optional.empty() : Optional.of(entity);
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return Optional.empty();
    }

    @Override
    public Donator findDonatorByCNP(String CNP) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement statement = connection.prepareStatement("select * from Donatori where cnp = ?")) {
            statement.setString(1, CNP);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Long id = result.getLong("id");
                String nume = result.getString("nume");
                String prenume = result.getString("prenume");
                String adresa = result.getString("adresa");
                String cnp = result.getString("cnp");
                String nr_telefon = result.getString("nr_telefon");
                Donator donator = new Donator(nume, prenume, adresa, cnp, nr_telefon);
                donator.setId(id);
                return donator;
            }
        }
        catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        return null;
    }
}
