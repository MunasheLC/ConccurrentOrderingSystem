package client;

import shared.OSserver;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    private OSserver server;

    public void display_menu() {
        System.out.println("-----------");
        System.out.println("   Menu   ");
        System.out.println("-----------");
        System.out.println ( "1) Option 1\n2) Option 2\n" );
        System.out.print ( "Selection: " );
    }

    public void login(){
        System.out.println("-----------");
        System.out.println("   Login   ");
        System.out.println("-----------");

        Scanner in = new Scanner(System.in);
        // customer login
        System.out.println("Enter CustomerID: ");
        String username = in.nextLine();
        System.out.println("Enter Password: ");
        String password = in .nextLine();

        try {
            String serv_response = server.userlogin(username,password); //calling the remote method using the obtained object we got from registry (the server).
            System.out.println("> " + serv_response);
            System.out.println("\n");

            if (serv_response.equals("Login Successful")){ //if the customer logs in successfully, display the menu.

                display_menu();

                switch (in.nextInt()) {
                    case 1 -> System.out.println("You picked option 1");
                    case 2 -> System.out.println("You picked option 2");
                    default -> System.err.println("Unrecognized option");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }

    public void clientStart() throws RemoteException,NotBoundException{
        Registry reg = LocateRegistry.getRegistry("localhost", 1099); //getting the registry
        server = (OSserver)reg.lookup("serv"); //looking up the registry for the remote object
        login(); //present the customer with the login display
    }

}
