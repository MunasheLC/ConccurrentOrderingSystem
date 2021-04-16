package client;

import shared.OSserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class Client {

    LocalDate date;
    String user;
    String item2;
    private OSserver server;
    boolean running;
    LocalDateTime ldt;
    //    HashMap<String, Integer> order;
    HashMap<String, HashMap> products;
    HashMap<String, HashMap> usersOrders;

    public Client() {

        this.running = true;
        this.products = new HashMap<>();
        this.usersOrders = new HashMap<>();
    }

    public void display_menu() {
        System.out.println("-----------");
        System.out.println("    Menu   ");
        System.out.println("-----------");
        System.out.println("1) Display Products \n2) Create Order\n3) Check item Availability\n4) Current Orders\n5) Remove Order\n6) Exit");
        System.out.print("Selection: ");
    }
    String dd;
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

                    item2 = item;
                    System.out.println("Enter item qty");
                    int qty = in.nextInt();

                    System.out.println("Enter date: (dd-mm-yyyy) ");
                    var d = in.next();
                    dd = d;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date2 = LocalDate.parse(d,formatter);

                    System.out.println("You want " + qty + " " + item + "'s" + " for the date " + date2);

                    LocalDateTime now = LocalDateTime.now();
                    if (date2.isBefore(ChronoLocalDate.from(now))){ //check if date is valid

                        System.out.println("date is not valid - " + d + " is before the current date");
                    }
//                    System.out.println(date2 + " " + now);
                    System.out.println("date is valid");
                    date = date2;

                    order.put(item, qty);
                    this.usersOrders.put(user,order);

                    ldt = LocalDateTime.of(date2, LocalTime.of(0,0));

                    System.out.println(this.usersOrders);

                }

                if(item.equals("complete")){
                    String orderReport;
                    System.out.println("confirming order: " + order);

                    try {
                        orderReport = server.confirmOrder(dd,order);
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
        user = username;
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
    public void removeOrder() throws IOException {
        Boolean removing = true;
        while(removing) {
            checkCurrentOrders();
            try {
                Scanner in = new Scanner(System.in);
                System.out.println("Enter Order Id: ");
                String id = in.next();
                server.cancelOrder(id);
                checkCurrentOrders();
                removing = false;
            }catch(RemoteException e){
                e.printStackTrace();
            }
        }
        display_menu();

    }

    public void displayPredict() throws FileNotFoundException, RemoteException {
        try {
            Scanner in = new Scanner(System.in);
            System.out.println("\nEnter 'complete' to finish ordering ");
            System.out.print("Enter item name: \n");
            String item = in.nextLine();
            System.out.println("You entered: " + item);

            if (products.containsKey(item)) {
                System.out.println("For what date (dd-mm-yyyy) : " );
                String dat = in.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date2 = LocalDate.parse(dat,formatter);

                LocalDateTime now = LocalDateTime.now();
                if (date2.isBefore(ChronoLocalDate.from(now))){ //check if date is valid

                    System.out.println("date is not valid - " + dat + " is before the current date");
                }
//                    System.out.println(date2 + " " + now);
                System.out.println("date is valid");
                ldt = LocalDateTime.of(date2, LocalTime.of(0,0));
                int result = server.displayPredictAvailability(ldt, item);
                System.out.println("Order prediction: " + item + " " + result);

            }
        }
        catch (RemoteException e) {

        e.printStackTrace();
        }
    }
    public void checkCurrentOrders() throws FileNotFoundException, RemoteException {
        List<String[]> results = server.displayPrevOrders(user);
        StringBuilder newString = new StringBuilder();
        for (String[] i : results){
            for (int j=0; j<i.length;j++) {
                newString.append(i[j] + " ");
            }
            newString.append("\n");
        }
        System.out.println(newString);
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
                    case 3 -> displayPredict();
                    case 4 -> checkCurrentOrders();
                    case 5 -> removeOrder();
                    case 6 -> exitClient();
                    default -> System.err.println("Unrecognized option");
                }

            } catch (Exception e) {

                System.out.println("There was an error");
                e.printStackTrace();
            }
        }
    }
}
