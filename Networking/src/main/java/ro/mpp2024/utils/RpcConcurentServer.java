package ro.mpp2024.utils;


import ro.mpp2024.IServer;
import ro.mpp2024.rpcprotocol.ClientRpcReflectionWorker;

import java.net.Socket;

public class RpcConcurentServer extends  AbsConcurentServer{

    private IServer chatServer;
    public RpcConcurentServer(int port, IServer chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- RpcConcurrentServer");
    }


    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcReflectionWorker worker=new ClientRpcReflectionWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }
}
