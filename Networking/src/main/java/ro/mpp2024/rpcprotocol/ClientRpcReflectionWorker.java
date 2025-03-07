package ro.mpp2024.rpcprotocol;


import ro.mpp2024.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.sql.SQLException;

public class ClientRpcReflectionWorker implements  Runnable, IObserver {

    private IServer server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private static Response errorResponse = new Response.Builder().type(ResponseType.ERROR).build();

    public ClientRpcReflectionWorker(IServer chatServer, Socket client) {
        this.server = chatServer;
        this.connection = client;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() throws MyAppException {
        System.out.println("Update called ...");
        Response response = new Response.Builder().type(ResponseType.UPDATE).data(null).build();
        sendResponse(response);



    }

    @Override
    public void run() {
        while (connected) {
            try {
                    Object request = input.readObject();
                    Response response = handleRequest((Request) request);
                    if (response != null) {
                        sendResponse(response);
                    }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response handleLOGIN(Request request) {
        System.out.println("Login request ..." + request.type());
        Voluntar user = (Voluntar) request.data();
        try {
            var optional = server.login(user.getUsername(), user.getParola(), this);
            if (optional != null) {
                return new Response.Builder().type(ResponseType.OK).data(optional).build();
            }
            else {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data("Authentication failed").build();

            }
        } catch (MyAppException e) {
            e.printStackTrace();
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }



    }

    public Response handleLOGOUT(Request request) {
        System.out.println("Logout request ..." + request.type());
        Voluntar user = (Voluntar) request.data();
        try {
            server.logout(user);
            connected = false;
            return okResponse;
        } catch (MyAppException e) {
            e.printStackTrace();
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    public Response handleGET_ALL_CAZURI_CARITABILE(Request request) {
        System.out.println("Get all cazuri caritabile request ..." + request.type());
        try {
            return new Response.Builder().type(ResponseType.OK).data(server.getAllCazuriCaritabile()).build();
        } catch (MyAppException | SQLException e) {
            e.printStackTrace();
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    public Response handleGET_DONATION_SUM(Request request) {
        System.out.println("Get donation sum for caz request ..." + request.type());
        try {
            return new Response.Builder().type(ResponseType.OK).data(server.getDonationSumforCazCaritabilbyName((String) request.data())).build();
        } catch (MyAppException e) {
            e.printStackTrace();
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    public Response handleGET_DONATORI(Request request) {
        System.out.println("Get donatori request ..." + request.type());
        try {
            return new Response.Builder().type(ResponseType.OK).data(server.getDonatori()).build();
        } catch (MyAppException | SQLException e) {
            e.printStackTrace();
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

public Response handleADD_DONATION(Request request) throws SQLException {
    System.out.println("Add donation request ..." + request.type());
    if (request.data() == null) {
        return new Response.Builder().type(ResponseType.ERROR).data("Invalid request data").build();
    }
    Donatie donatie = (Donatie) request.data();
    server.addDonation(donatie.getDonator(), donatie.getCazCaritabil(), donatie.getSuma());
    return new Response.Builder().type(ResponseType.ADD_DONATION).build();
}

public Response handleADD_DONATOR(Request request) throws SQLException {
    System.out.println("Add donator request ..." + request.type());
    if (request.data() == null) {
        return new Response.Builder().type(ResponseType.ERROR).data("Invalid request data").build();
    }
    Donator donator = (Donator) request.data();
    server.add_donator(donator);
    return new Response.Builder().type(ResponseType.ADD_DONATOR).build();

}
    private synchronized void sendResponse(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
