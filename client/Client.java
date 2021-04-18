package client;
import server.Products;
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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Client  {
    LocalDate date;
    String user;
    String temp_item;
    private OSserver server;
    boolean running;
    LocalDateTime ldt;
    HashMap<String, HashMap> products;
    HashMap<String, HashMap> usersOrders;
    String temp_date;



    public Client() {

        this.running = true;
        this.products = new HashMap<>();
        this.usersOrders = new HashMap<>();
    }

    public void display_menu() {
        System.out.println("-----------");
        System.out.println("    Menu   ");
        System.out.println("-----------");
        System.out.println("1) Display Products \n2) Create Order\n3) List of projected availability\n4) Check item Availability\n5) Current Orders\n6) Remove Order\n7) TestMode\n8) Exit ");
        System.out.print("Selection: ");
    }



    public void testMode() throws IOException, InterruptedException {
        //Set the name of thread maybe

//        Timer timer = new Timer(); //For client execution times

        Thread.sleep(ThreadLocalRandom.current().nextInt(10000));

        List<String[]> lines = server.getProducts(); //gets list of products & information from server
        for (String[] line : lines) {
            String name = line[0];
            int quantity = Integer.parseInt(line[1]);
            int restockd = Integer.parseInt(line[2]);
            int restockq = Integer.parseInt(line[3]);
            System.out.println("Name: " + name + ", Quantity: " + quantity + ", Restock Date: " + restockd + ", Restock Quantity: " + restockq);
        }

        HashMap<String, Integer> order = new HashMap<>();
        List<String> myList = Arrays.asList("Apples", "Oranges", "GameBoy", "JoltCola"); //get random item
        Random r = new Random();
        int randomitem = r.nextInt(myList.size());
        String item = myList.get(randomitem);
        int qty = r.nextInt((600 - 200) + 1) + 200; //pick a random qty

        List<String> dates = Arrays.asList("20-06-2021", "16-07-2021", "05-08-2021", "22-09-2021"); //pick a date
        int randomdate = r.nextInt(dates.size());
        String date_item = dates.get(randomdate);

        order.put(item, qty);

        //create Order
        System.out.println("--------------------");
        System.out.println("    Create Order    ");
        System.out.println("--------------------");
        System.out.println("You want to order: " + item + " for the date: " + date_item);
        System.out.println("Confirming order ....");

        String orderReport = server.confirmOrder(date_item,order, user);
        System.out.println(orderReport);
        products = server.receiveProds();

        Thread.sleep(ThreadLocalRandom.current().nextInt(10000)); //Wait random amount of time before next execution

        //get available Predict of an item
        System.out.println("---------------------------------------");
        System.out.println("    Predict Availability of An Item    ");
        System.out.println("---------------------------------------");
        getDate(item,qty,date_item);
        int result = server.predictAvailabilityForAnItem(ldt, item);
        System.out.println("Order prediction for item: " + item + " = " + result);
        Thread.sleep(ThreadLocalRandom.current().nextInt(10000)); //Wait random amount of time before next execution

        // Predict availability for all items
        System.out.println("---------------------------------------");
        System.out.println("    Predict Availability All Items     ");
        System.out.println("---------------------------------------");
        StringBuilder PredictResult = server.predictAvailabilityForAll();
        System.out.println(PredictResult);
        Thread.sleep(ThreadLocalRandom.current().nextInt(10000)); //Wait random amount of time before next execution

        //check current orders
        checkCurrentOrders();

        Thread.sleep(ThreadLocalRandom.current().nextInt(10000)); //Wait random amount of time before next execution

        //remove an order
        System.out.println("--------------------");
        System.out.println("    Remove An Order ");
        System.out.println("--------------------");
        Products prod = new Products();
        List<String> IDList = new ArrayList();
        System.out.println("The user is: " + user);
        List<String []> results = prod.displayOrders(user);
        for (String [] items : results){
            IDList.add(items[0]);
        }
        int randomID = r.nextInt(IDList.size());
        String ranID = IDList.get(randomID);
        System.out.println("Removing item with ID: " + ranID);
        server.cancelOrder(ranID);
        checkCurrentOrders();

        System.out.println("Terminating Client..");
        exitClient();
    }
    public void getDate(String item, int qty, String inputted_date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate StringToDate = LocalDate.parse(inputted_date,formatter);

        System.out.println("You want " + qty + " " + item + "'s" + " for the date " + StringToDate);

        LocalDateTime now = LocalDateTime.now();
        if (StringToDate.isBefore(ChronoLocalDate.from(now))){ //check if date is valid

            System.out.println("date is not valid - " + inputted_date + " is before the current date");
        }
        ldt = LocalDateTime.of(StringToDate, LocalTime.of(0,0));
    }
    public void createOrder() {

        boolean ordering = true;
        HashMap<String, Integer> order = new HashMap<>();

        System.out.println("--------------------");
        System.out.println("    Create Order    ");
        System.out.println("--------------------");
        System.out.println("Select item and enter quantity");

        while (ordering) {
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
                    temp_item = item;
                    System.out.println("Enter item qty");
                    int qty = in.nextInt();

                    System.out.println("Enter date: (dd-mm-yyyy) ");
                    var inputted_date = in.next();
                    temp_date = inputted_date;
                    getDate(item, qty, inputted_date);

                    order.put(item, qty);
                    this.usersOrders.put(user,order);

                }

                if(item.equals("complete")){
                    String orderReport;
                    System.out.println("confirming order: " + order + " ....");

                    try {
                        orderReport = server.confirmOrder(temp_date,order, user);
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
        System.out.println("\n");
        System.out.println("----------- Remove An Order -----------");
        Boolean removing = true;
        var result = checkCurrentOrders();
        if (result) {
            while (removing) {
                try {
                    Scanner in = new Scanner(System.in);
                    System.out.println("Enter Order ID in which you want to remove ( Enter CANCEL to cancel this operation ) : ");
                    String id = in.next();
                    if (!id.equals("CANCEL")) {
                        server.cancelOrder(id);
                        checkCurrentOrders();
                        removing = false;
                    }
                    else{
                        removing = false;
                        System.out.println("> CANCEL");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        display_menu();

    }
    public void displayPredictForAll() throws FileNotFoundException, RemoteException {
        System.out.println("-- Item Availability Prediction over the next 6 months --");
        StringBuilder result = server.predictAvailabilityForAll();
        System.out.println(result);
        display_menu();

    }
    public void displayPredictForAnItem() throws FileNotFoundException, RemoteException {
        try {
            Scanner in = new Scanner(System.in);
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
                int result = server.predictAvailabilityForAnItem(ldt, item);
                System.out.println("Order prediction: " + item + " " + result + "\n");
                display_menu();

            }
        }
        catch (RemoteException e) {

        e.printStackTrace();
        }
    }
    public boolean checkCurrentOrders() throws FileNotFoundException, RemoteException {
        System.out.println("---------------------");
        System.out.println("    Current Orders   ");
        System.out.println("---------------------");
        List<String[]> results = server.displayPrevOrders(user);
        StringBuilder newString = new StringBuilder();
        for (String[] i : results){
            for (int j=0; j<i.length;j++) {
                newString.append(i[j] + " ");
            }
            newString.append("\n");
        }
        if(newString.length() == 0){
            System.out.println("There are no current orders..");
            return false;

        }
        System.out.println("ID                                   USER    ITEM    DATE     QTY");
        System.out.println(newString);
        return true;
    }



    public void clientStart() throws RemoteException, NotBoundException, FileNotFoundException {

        Registry reg = LocateRegistry.getRegistry("localhost", 1099); //getting the registry
        server = (OSserver) reg.lookup("serv"); //looking up the registry for the remote object
        products = server.receiveProds(); // Takes products from server

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
                    case 3 -> displayPredictForAll();
                    case 4 -> displayPredictForAnItem();
                    case 5 -> {
                        checkCurrentOrders();
                        display_menu();
                    }
                    case 6 -> removeOrder();
                    case 7 -> testMode();
                    case 8 -> exitClient();
                    default -> System.err.println("Unrecognized option");
                }

            } catch (Exception e) {

                System.out.println("There was an error");
                e.printStackTrace();
            }
        }
    }

}
