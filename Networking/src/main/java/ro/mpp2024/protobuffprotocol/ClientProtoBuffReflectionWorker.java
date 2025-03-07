package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ClientProtoBuffReflectionWorker implements  Runnable, IObserver {

    private IServer server;
    private Socket connection;

    private InputStream input;
    private OutputStream output;

    private volatile  boolean connected;

    public ClientProtoBuffReflectionWorker(IServer server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = connection.getOutputStream();
            input = connection.getInputStream();
            connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        while (connected){
            try {
                var request = DonatieProto.Request.parseDelimitedFrom(input);
                var response = handleRequest(request);
                if (response != null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        try{
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    private void sendResponse(Object response) {
        System.out.println("Sending response " + response);
        try {
            ((DonatieProto.Response) response).writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object handleRequest(DonatieProto.Request request) {
        DonatieProto.Response response = null;
        if (request.getType() == DonatieProto.Request.RequestType.LOGIN) {
            System.out.println("Login request ...");
            Voluntar user = ProtoUtils.getVoluntar(request);
            try {
                var client = server.login(user.getUsername(), user.getParola(), this);
                if (client != null) {
                    return ProtoUtils.createOkResponse(client);
                } else {
                    return ProtoUtils.createErrorResponse("Authentication failed");
                }
            } catch (MyAppException e) {
                response = ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if (request.getType() == DonatieProto.Request.RequestType.GET_ALL_CAZURI_CARITABILE) {
            System.out.println("Get all cazuri caritabile request ...");
            try {
                var cazuri = server.getAllCazuriCaritabile();
                return ProtoUtils.createGetAllCazuriCaritabileResponse(cazuri);
            } catch (MyAppException e) {
                response = ProtoUtils.createErrorResponse(e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (request.getType() == DonatieProto.Request.RequestType.GET_DONATION_SUM)
        {
            System.out.println("Get donation sum for caz request ...");
            String name = request.getNumecaz();
            try {
                var suma = server.getDonationSumforCazCaritabilbyName(name);
                return ProtoUtils.createGetDonationSumResponse(suma);
            } catch (MyAppException e) {
                response = ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        return response;
    }

    @Override
    public void update() throws MyAppException, SQLException {

        System.out.println("Update called ...");
        DonatieProto.Response response = ProtoUtils.createOkResponse();

        sendResponse(response);

    }
}
