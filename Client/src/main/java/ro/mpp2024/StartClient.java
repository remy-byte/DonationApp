package ro.mpp2024;



import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import ro.mpp2024.UI.LoginControllerClient;
import ro.mpp2024.UI.VoluntarController;
import ro.mpp2024.protobuffprotocol.ServerProtoBuffProxy;
import ro.mpp2024.rpcprotocol.ServerRpcProxy;

import java.io.IOException;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;
import java.util.Properties;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class StartClient extends Application {
    private Stage primaryStage;

    private static int defaultChatPort = 55556;
    private static String defaultServer = "localhost";


    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartClient.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("chat.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("chat.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IServer server = new ServerRpcProxy(serverIP, serverPort);
        //IServer server = new ServerProtoBuffProxy(serverIP, serverPort);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/hello-view.fxml"));
        Parent root = loader.load();
        LoginControllerClient controller = loader.getController();
        controller.setProps(server);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();

        FXMLLoader loader2 = new FXMLLoader();
        loader2.setLocation(getClass().getResource("/voluntar-view.fxml"));
        root = loader2.load();
        VoluntarController controller2 = loader2.getController();
        controller.setMainController(controller2);
        controller2.setProps(server);
        controller.setParent(root);


//        System.out.println("Hello!");
//
//
//        System.out.println("TEST Servicii rest");
//
//        System.out.println("get_all");
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/cazuri-caritabile"))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//
//        System.out.println("get_by_id");
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/cazuri-caritabile/1"))
//                .build();
//
//        response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//
//        System.out.println("add");
//
//        CazCaritabil cazCaritabil1 = new CazCaritabil("Salvam animalele", "ONU");
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String cazCaritabil1Json = objectMapper.writeValueAsString(cazCaritabil1);
//
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/cazuri-caritabile/"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(cazCaritabil1Json))
//                .build();
//
//        response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//
//        System.out.println("update");
//
//        CazCaritabil cazCaritabil2 = new CazCaritabil("Salvam animalele-v2", "ONU");
//        cazCaritabil2.setId(2L);
//
//        ObjectMapper objectMapper2 = new ObjectMapper();
//        String cazCaritabil2Json = objectMapper2.writeValueAsString(cazCaritabil2);
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/cazuri-caritabile/2"))
//                .header("Content-Type", "application/json")
//                .method("PUT", HttpRequest.BodyPublishers.ofString(cazCaritabil2Json))
//                .build();
//
//
//        response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//
//        System.out.println("delete");
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/cazuri-caritabile/2"))
//                .DELETE()
//                .build();
//
//        response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
    }
}
