package client;

import shared.OSserver;

import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Client {


    private OSserver server;
    boolean running;
//    HashMap<String, Integer> order;
    HashMap<String, HashMap> products;
    public Client() {

        this.running = true;
//        this.order = new HashMap<String, Integer>();
        this.products = new HashMap<>();



    }

    public void display_menu() {
        System.out.println("-----------");
        System.out.println("    Menu   ");
        System.out.println("-----------");
        System.out.println("1) Display Products \n2) Create Order\n3) Exit");
        System.out.print("Selection: ");
    }

    public void createOrder() {

        boolean ordering = true;
        HashMap<String, Integer> order = new HashMap<>();

        System.out.println("-----------");
        System.out.println("    Create Order   ");
        System.out.println("-----------");
        System.out.println("Select item and enter quantity");


        while (ordering) {

//                 Name -> Qty that info -> server -> products


            try {
                Scanner in = new Scanner(System.in);


                List<String[]> lines = server.getProducts(); //gets list of products & information from server

                int num = 1;
                for (String[] line : lines) {
                    String name = line[0];
                    int quantity = Integer.parseInt(line[1]);
                    int restockd = Integer.parseInt(line[2]);
                    int restockq = Integer.parseInt(line[3]);
                    System.out.println(num + ": " + name + ", Quantity: " + quantity + ", Restock Date: " + restockd + ", Restock Quantity: " + restockq );


                    num += 1;


                }

                System.out.println("\nEnter 'complete' to finish ordering " );
                System.out.print("Enter item name: \n");
                String item = in.nextLine();
                System.out.println("You entered: " + item);



                if (products.containsKey(item)) {


                    System.out.println("Enter item qty");
                    int qty = in.nextInt();

                    System.out.println("You want " + qty + " " + item + "'s");
                    order.put(item, qty);


                    System.out.println(order);




                }

                if(item.equals("complete")){
                    String orderReport;
                    System.out.println("confirming order: " + order);

                    try {
                        orderReport = server.confirmOrder(order);
                        System.out.println(orderReport);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    ordering = false;
                    display_menu();

                }

                //Confirming Order













            } catch (RemoteException | FileNotFoundException e) {

                e.printStackTrace();

            }
        }


    }

    public boolean login() {
        System.out.println("-----------");
        System.out.println("   Login   ");
        System.out.println("-----------");

        Scanner in = new Scanner(System.in);
        // customer login
        System.out.println("Enter CustomerID: ");
        String username = in.nextLine();
        System.out.println("Enter Password: ");
        String password = in.nextLine();


        try {

            String serv_response = server.userlogin(username, password); //calling the remote method using the obtained object we got from registry (the server).
            System.out.println("> " + serv_response);
            System.out.println("\n");

            if (serv_response.equals("Login Successful")) {

                display_menu();


            }


        } catch (RemoteException e) {

            e.printStackTrace();
        }

        return true;
    }


    public void exitClient() {

        this.running = false;

    }

    public void clientStart() throws RemoteException, NotBoundException, FileNotFoundException {

        Registry reg = LocateRegistry.getRegistry("localhost", 1099); //getting the registry
        server = (OSserver) reg.lookup("serv"); //looking up the registry for the remote object
        products = server.receiveProds();

        login();
        while (running) {





            try {

                Scanner in = new Scanner(System.in);

                switch (in.nextInt()) {
                    case 1 -> {
                        List<String[]> lines = server.getProducts(); //gets list of products & information from server

                        for (String[] line : lines) {
                            String name = line[0];
                            int quantity = Integer.parseInt(line[1]);
                            int restockd = Integer.parseInt(line[2]);
                            int restockq = Integer.parseInt(line[3]);
                            System.out.println("Name: " + name + ", Quantity: " + quantity + ", Restock Date: " + restockd + ", Restock Quantity: " + restockq);


                        }
                        display_menu();

                    }
                    case 2 -> createOrder();
                    case 3 -> exitClient();
                    default -> System.err.println("Unrecognized option");


                }

            } catch (Exception e) {

                System.out.println("There was an error");
                e.printStackTrace();
            }



    }


    }
}
