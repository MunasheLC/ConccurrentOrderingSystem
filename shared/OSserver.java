package shared;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

//Remote interface - this provides the description of all methods of a particular remote object.
//The client communicates with this remote interface.
public interface OSserver extends Remote {
    String userlogin(String username, String password) throws RemoteException;
    List<String[]> getProducts() throws RemoteException, FileNotFoundException;
}
