package client;

import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientStart {

    public static void main(String[] args) throws RemoteException, NotBoundException, FileNotFoundException {
        Client client = new Client();
        client.clientStart();

    }
}
