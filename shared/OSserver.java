package shared;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

//Remote interface - this provides the description of all methods of a particular remote object.
//The client communicates with this remote interface.
public interface OSserver extends Remote {

    String userlogin(String username, String password) throws RemoteException;
    List<String[]> getProducts() throws RemoteException, FileNotFoundException;
    HashMap<String, HashMap> receiveProds() throws FileNotFoundException, RemoteException;
    String  confirmOrder(String date, HashMap<String, Integer> order) throws IOException;
    int predictAvailabilityForAnItem(LocalDateTime date, String text) throws RemoteException, FileNotFoundException;
    List<String[]>displayPrevOrders(String user) throws RemoteException, FileNotFoundException;
    void cancelOrder(String id) throws RemoteException, IOException;
    StringBuilder predictAvailabilityForAll() throws RemoteException, FileNotFoundException;
}
