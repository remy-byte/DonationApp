package ro.mpp2024.utils;

import ro.mpp2024.IServer;
import ro.mpp2024.protobuffprotocol.ClientProtoBuffReflectionWorker;

import java.net.Socket;

public class RPCConcurentServerProto extends AbsConcurentServer{
    private IServer server;
    public RPCConcurentServerProto(int port, IServer server) {
        super(port);
        this.server = server;
        System.out.println("RPCConcurentServerProto");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientProtoBuffReflectionWorker worker = new ClientProtoBuffReflectionWorker(server, client);
        Thread tw = new Thread(worker);
        return tw;
    }
}
