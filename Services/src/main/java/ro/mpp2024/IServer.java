package ro.mpp2024;

import java.sql.SQLException;

public interface IServer {


    public Voluntar login(String username, String password, IObserver client) throws MyAppException;

    public void logout(Voluntar user) throws MyAppException;

    public Iterable<Donator> getDonatori() throws MyAppException, SQLException;

    Iterable<CazCaritabil> getAllCazuriCaritabile() throws MyAppException, SQLException;

    Float getDonationSumforCazCaritabilbyName(String numeCaz) throws MyAppException;

    void addDonation(Donator donator, CazCaritabil cazCaritabil, Integer v) throws SQLException;

    void add_donator(Donator donator) throws SQLException;
}
