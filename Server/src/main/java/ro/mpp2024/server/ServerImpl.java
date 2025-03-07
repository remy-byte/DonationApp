package ro.mpp2024.server;


import ro.mpp2024.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements IServer {

    IDonatieRepository donatieRepository;
    IDonatorRepository donatorRepository;
    IVoluntarRepository voluntarRepository;
    ICazCaritabilRepository cazCaritabilRepository;

    private Map<String, IObserver> loggedClients;
    private final int defaultThreadsNo=5;

    public ServerImpl(RepositoryDBDonatie donatieRepository, RepositoryDBDonator donatorRepository, RepositoryHibernateVoluntar voluntarRepository, RepositoryDBCazCaritabil cazCaritabilRepository) {
        this.donatieRepository = donatieRepository;
        this.donatorRepository = donatorRepository;
        this.voluntarRepository = voluntarRepository;
        this.cazCaritabilRepository = cazCaritabilRepository;
        loggedClients= new ConcurrentHashMap<>();
    }
    public ServerImpl(RepositoryDBDonatie donatieRepository, RepositoryDBDonator donatorRepository, RepositoryDBVoluntar voluntarRepository, RepositoryDBCazCaritabil cazCaritabilRepository) {
        this.donatieRepository = donatieRepository;
        this.donatorRepository = donatorRepository;
        this.voluntarRepository = voluntarRepository;
        this.cazCaritabilRepository = cazCaritabilRepository;
        loggedClients= new ConcurrentHashMap<>();
    }
    @Override
    public synchronized Voluntar login(String username, String password, IObserver client) throws MyAppException {

        try {
            Voluntar voluntar = voluntarRepository.findVoluntarByUsernameandPassword(username, password);
            if (voluntar != null) {
                loggedClients.put(voluntar.getUsername(), client);
                return voluntar;
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return null;
    }
    @Override
    public synchronized void logout(Voluntar user) throws MyAppException {
        if (loggedClients.get(user.getUsername()) == null)
            throw new MyAppException("User not logged in");
        loggedClients.remove(user.getUsername());


    }

    @Override
    public Iterable<Donator> getDonatori() throws MyAppException, SQLException {
        return donatorRepository.findAll();
    }

    @Override
    public Iterable<CazCaritabil> getAllCazuriCaritabile() throws MyAppException, SQLException {
        return cazCaritabilRepository.findAll();
    }

    @Override
    public Float getDonationSumforCazCaritabilbyName(String numeCaz) throws MyAppException {

        return donatieRepository.getDonationSumforCazCaritabilbyName(numeCaz);
    }

    @Override
    public synchronized void addDonation(Donator donator, CazCaritabil cazCaritabil, Integer v) throws SQLException {
        Donator donator1 = donatorRepository.findDonatorByCNP(donator.getCnp());
        Donatie donatie = new Donatie(cazCaritabil, donator1, v);
        donatieRepository.save(donatie);
        notifyClients();


    }

    @Override
    public void add_donator(Donator donator) throws SQLException {
        donatorRepository.save(donator);
    }

    private void notifyClients(){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for(var client : loggedClients.values()){
            if(client == null)
                continue;
            executor.execute(() -> {
                try {
                    client.update();
                } catch (MyAppException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
