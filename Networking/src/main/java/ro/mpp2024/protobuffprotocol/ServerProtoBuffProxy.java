package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerProtoBuffProxy implements IServer {
    private String host;
    private int port;

    private IObserver client;

    private InputStream input;
    private OutputStream output;
    private Socket connection;

    private volatile boolean finished;

    private BlockingQueue<DonatieProto.Response> qresponses;


    public ServerProtoBuffProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<>();
    }

    @Override
    public Voluntar login(String username, String password, IObserver client) throws MyAppException {
        initializeConnection();
        Voluntar user = new Voluntar();
        user.setUsername(username);
        user.setParola(password);
        sendRequest(ProtoUtils.createLoginRequest(username, password));
        DonatieProto.Response response = readResponse();
        if (response.getType() == DonatieProto.Response.ResponseType.OK) {
            this.client = client;
            return ProtoUtils.getVoluntar(response);
        }
        if (response.getType() == DonatieProto.Response.ResponseType.ERROR) {
            String errorText = response.getMessage();
            closeConnection();
            throw new MyAppException(errorText);
        }
        return null;
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DonatieProto.Response readResponse() {
        DonatieProto.Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void sendRequest(DonatieProto.Request request) {
        try {
            request.writeDelimitedTo(output);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            output.flush();
            input = connection.getInputStream();
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public void logout(Voluntar user) throws MyAppException {

    }

    @Override
    public Iterable<Donator> getDonatori() throws MyAppException, SQLException {
        sendRequest(ProtoUtils.createGetDonatoriRequest());
        var response = readResponse();
        if (response.getType() == DonatieProto.Response.ResponseType.ERROR) {
            String errorText = response.getMessage();
            throw new MyAppException(errorText);
        }
        return ProtoUtils.getDonatoriFromResponse(response);
    }

    @Override
    public Iterable<CazCaritabil> getAllCazuriCaritabile() throws MyAppException, SQLException {
        sendRequest(ProtoUtils.createGetAllCazuriCaritabileRequest());
        var response = readResponse();
        if (response.getType() == DonatieProto.Response.ResponseType.ERROR) {
            String errorText = response.getMessage();
            throw new MyAppException(errorText);
        }
        return ProtoUtils.getCazuriCaritabileFromResponse(response);
    }

    @Override
    public Float getDonationSumforCazCaritabilbyName(String numeCaz) throws MyAppException {
        sendRequest(ProtoUtils.createGetDonationSumRequest(numeCaz));
        var response = readResponse();
        if (response.getType() == DonatieProto.Response.ResponseType.ERROR) {
            String errorText = response.getMessage();
            throw new MyAppException(errorText);
        }
        return ProtoUtils.getDonationSumFromResponse(response);
    }

    @Override
    public void addDonation(Donator donator, CazCaritabil cazCaritabil, Integer v) throws SQLException {
        Donatie donatie = new Donatie(cazCaritabil, donator, v);
       sendRequest(ProtoUtils.createAddDonationRequest(donatie));
       var response = readResponse();
         if (response.getType() == DonatieProto.Response.ResponseType.ERROR) {
              String errorText = response.getMessage();
             try {
                 throw new MyAppException(errorText);
             } catch (MyAppException e) {
                 throw new RuntimeException(e);
             }
         }

    }

    @Override
    public void add_donator(Donator donator) throws SQLException {
        sendRequest(ProtoUtils.createAddDonatorRequest(donator));
        var response = readResponse();
        if (response.getType() == DonatieProto.Response.ResponseType.ERROR) {
            String errorText = response.getMessage();
            try {
                throw new MyAppException(errorText);
            } catch (MyAppException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
//                    Object response = input.readObject();
                    DonatieProto.Response response = DonatieProto.Response.parseDelimitedFrom(input);
                    System.out.println("Response received " + response);
                    if(isUpdateResponse(response)){
                        handleUpdate(response);
                    } else {
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }

    private void handleUpdate(DonatieProto.Response response) {
        if (response.getType() == DonatieProto.Response.ResponseType.UPDATE) {
            try {
                client.update();
            } catch (MyAppException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isUpdateResponse(DonatieProto.Response response) {
        return response.getType() == DonatieProto.Response.ResponseType.UPDATE;
    }

}