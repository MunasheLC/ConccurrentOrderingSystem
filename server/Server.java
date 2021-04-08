package server;

import shared.OSserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server implements OSserver {
    private final HashMap users = new HashMap<String,String>();

    public Server() throws RemoteException{
        UnicastRemoteObject.exportObject(this,0); //exports the remote object
    }
    public String userlogin(String username, String password){
        addUsersToMap();
        return userAuthorization(username,password);
    }
    private String userAuthorization(String username , String password){ //checks if the username and password is correct and in the hashmap.
        String response = "";

        Set set = users.entrySet();
        Iterator iter = set.iterator();
        boolean inMap_check = false;

        while(iter.hasNext()){
            response = "";
            Map.Entry entry = (Map.Entry) iter.next();

            String user = entry.getKey().toString();
            String pass = entry.getValue().toString();

            if(username.equals(user)){
                inMap_check = true;
                if(password.equals(pass)){
                    response = "Login Successful";
                }else{
                    response = "Password Incorrect..";
                }
                break;
            }
        }
        if(!inMap_check){
            response = "User does not exist";
        }

        return response;
    }
    private void addUsersToMap(){ //places users into hashmap
        users.put("Admin", "admin");
        users.put("user1", "welcome123");
    }

}
