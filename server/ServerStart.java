package server;

import shared.OSserver;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerStart {

    //Here a remote object is created and a reference of that object is made available for the client ( The registry )
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        OSserver server = new Server();
        Registry reg = LocateRegistry.createRegistry(1099);
        reg.bind("serv", server); //bind name ( can be anything ) and the exported object - in this case it's the server.
        System.out.println("Server started ..");

    }

}
