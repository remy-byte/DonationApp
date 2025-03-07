package ro.mpp2024.rpcprotocol;


import ro.mpp2024.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerRpcProxy implements IServer {
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public ServerRpcProxy(String serverIP, int serverPort) {

        this.host = serverIP;
        this.port = serverPort;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    private void closeConnection() {
        System.out.println("Inchidem conexiunea");
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) throws MyAppException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new MyAppException("Error sending object " + e);
        }

    }

    private Response readResponse() throws MyAppException {
        Response response = null;
        try {
            response = qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() throws MyAppException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public Voluntar login(String username, String password, IObserver client) throws MyAppException {
        initializeConnection();
        Voluntar user = new Voluntar();
        user.setUsername(username);
        user.setParola(password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return (Voluntar) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            closeConnection();
            throw   new MyAppException("Authentication failed");
        }

        return null;

    }

    @Override
    public void logout(Voluntar user) throws MyAppException {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            throw new MyAppException("Logout error");
        }
    }

    @Override
    public Iterable<Donator> getDonatori() throws MyAppException {
        Request request = new Request.Builder().type(RequestType.GET_DONATORI).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new MyAppException("Error getting donators");
        }
        return (Iterable<Donator>) response.data();
    }

    @Override
    public Iterable<CazCaritabil> getAllCazuriCaritabile() throws MyAppException {
        Request request = new Request.Builder().type(RequestType.GET_ALL_CAZURI_CARITABILE).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new MyAppException("Error getting charity cases");
        }
        return (Iterable<CazCaritabil>) response.data();
    }

    @Override
    public Float getDonationSumforCazCaritabilbyName(String numeCaz) throws MyAppException {

        Request request = new Request.Builder().type(RequestType.GET_DONATION_SUM).data(numeCaz).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new MyAppException("Error getting donation sum");
        }
        return (Float) response.data();
    }

    @Override
    public void addDonation(Donator donator, CazCaritabil cazCaritabil, Integer v) {
        Request request = new Request.Builder().type(RequestType.ADD_DONATION).data(new Donatie(cazCaritabil,donator, v)).build();
        try {
            sendRequest(request);
            Response response = readResponse();
            if (response.type() == ResponseType.ERROR) {
                throw new MyAppException("Error adding donation");
            }
        } catch (MyAppException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add_donator(Donator donator) throws SQLException {
        Request request = new Request.Builder().type(RequestType.ADD_DONATOR).data(donator).build();
        try {
            sendRequest(request);
            Response response = readResponse();
            if (response.type() == ResponseType.ERROR) {
                throw new MyAppException("Error adding donor");
            }
        } catch (MyAppException e) {
            e.printStackTrace();
        }
    }


    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.UPDATE;
    }


    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate2((Response) response);
                    }
                     else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (MyAppException e) {
                    throw new RuntimeException(e);
                }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
        private void handleUpdate2(Response response) throws SQLException, MyAppException {
            if (response.type() == ResponseType.UPDATE) {
                System.out.println("Inscriere noua. Se notifica toti clientii.");


                client.update();
                System.out.println("Am intrat in handleUpdate din Proxi");
                System.out.println("Am iesit in handleUpdate din Proxi");

            }
        }

        private void handleUpdate(Response response1) {
            try {
                client.update();
            } catch (MyAppException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean isUpdateResponse(Response response1) {
            return response1.type() == ResponseType.UPDATE;
        }
    }
}
