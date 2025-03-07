package ro.mpp2024;

import ro.mpp2024.server.ServerImpl;
import ro.mpp2024.utils.AbstractServer;
import ro.mpp2024.utils.RPCConcurentServerProto;
import ro.mpp2024.utils.RpcConcurentServer;
import ro.mpp2024.utils.ServerException;

import java.util.Properties;

public class StartRpcServer {

    public static int defaultPort = 55555;

    public static void main(String[] args) {

        Properties serverProps = new Properties();

        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (Exception e) {
            System.out.println("Cannot find server.properties " + e);
            return;
        }

        RepositoryDBDonatie donatieRepository = new RepositoryDBDonatie(serverProps);
        RepositoryDBDonator donatorRepository = new RepositoryDBDonator(serverProps);
        RepositoryDBVoluntar voluntarRepository = new RepositoryDBVoluntar(serverProps);
        RepositoryDBCazCaritabil cazCaritabilRepository = new RepositoryDBCazCaritabil(serverProps);
        RepositoryHibernateVoluntar repositoryHibernateVoluntar = new RepositoryHibernateVoluntar();
        IServer serverImpl = new ServerImpl(donatieRepository, donatorRepository, repositoryHibernateVoluntar, cazCaritabilRepository);

        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nfe) {
            System.err.println("Wrong  Port Number" + nfe);
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurentServer(serverPort, serverImpl);
        //AbstractServer server = new RPCConcurentServerProto(serverPort, serverImpl);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting server " + e);


        }


    }
}
