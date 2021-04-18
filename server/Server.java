package server;

import shared.OSserver;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements OSserver {
    private final HashMap users = new HashMap<String, String>();

    HashMap<String, HashMap> products;
    String user;

    public void writeToOrdersCSV(StringBuilder text, String user) throws IOException {
//        System.out.println(text);
        Lock lock = new ReentrantLock(true);
        System.out.println(user + " has the lock");
        lock.lock();
        FileWriter writer = new FileWriter("server/Orders.csv", true);
        writer.append(text);
        writer.append("\n");
        writer.close();
        lock.unlock();
    }

    //Always gets latest prod info
    public List<String[]> getProducts() throws FileNotFoundException { //get items from csv file in Product.java
        Products prod = new Products();
        List<String[]> items = prod.displayProducts(); //returns a list of products & their information
//        System.out.println("displaying items");
//        System.out.println("in items shshsh " + items);
        List<String> keys = new ArrayList<String>();
        keys.add("Quantity");
        keys.add("Restock Date");
        keys.add("Restock Quantity");

        for (String[] item : items) {
            HashMap<String, Integer> values = new HashMap<String, Integer>();
            String name = item[0];

            int j = 0;
            for (int i = 1; i < item.length; i++) {
                int value = Integer.parseInt(item[i]);
                values.put(keys.get(j), value);
                j += 1;
//                System.out.println("values: " + values);
            }
            products.put(name, values);
        }
        System.out.println(products);
        return items; //return these products to the client
    }

    public HashMap<String, HashMap> receiveProds() throws FileNotFoundException {
        getProducts();
        return products;
    }

    public Server() throws RemoteException {
        this.products = new HashMap<>();

        UnicastRemoteObject.exportObject(this, 0); //exports the remote object
    }

    public String userlogin(String username, String password) {
        addUsersToMap();
        return userAuthorization(username, password);
    }

    private String userAuthorization(String username, String password) { //checks if the username and password is correct and in the hashmap.
        String response = "";
        user = username;
        Set set = users.entrySet();
        Iterator iter = set.iterator();
        boolean inMap_check = false;

        while (iter.hasNext()) {
            response = "";
            Map.Entry entry = (Map.Entry) iter.next();

            String user = entry.getKey().toString();
            String pass = entry.getValue().toString();

            if (username.equals(user)) {
                inMap_check = true;
                if (password.equals(pass)) {
                    System.out.println("User logged in");
                    response = "Login Successful";
                } else {
                    response = "Password Incorrect..";
                }
                break;
            }
        }
        if (!inMap_check) {
            response = "User does not exist";
        }

        return response;
    }

    private void addUsersToMap() { //places users into hashmap
        users.put("Admin", "admin");
        users.put("user1", "welcome123");
    }

    public void convertHashMap() throws IOException {
        Products prod = new Products();
        StringBuilder convertMap = new StringBuilder();
        for (String key : products.keySet()) {
            Map innerMap = products.get(key);
            convertMap.append(key + " ");
            for (Object k : innerMap.keySet()) {
                convertMap.append(innerMap.get(k) + " ");
            }
            convertMap.append("\n");
        }
        convertMap.delete(convertMap.length() - 2, convertMap.length());
        prod.overWrite(convertMap);
    }

    public void cancelOrder(String id) throws IOException {
        Lock lock = new ReentrantLock(true);
        lock.lock();
        Products prod = new Products();
        prod.removeOrders(id);
        lock.unlock();
    }

    public String confirmOrder(String date, HashMap<String, Integer> order, String user) throws IOException {
        //Checks if items are in stock
        Set set = order.entrySet();
        Iterator iter = set.iterator();
        String response;
        response = "";
        String id = UUID.randomUUID().toString();
        Lock lock = new ReentrantLock(true);
        lock.lock(); //Lock while trying to make changes to the products
        while (iter.hasNext()) {



            Map.Entry entry = (Map.Entry) iter.next();

            String item = entry.getKey().toString();
//            Integer qty = (Integer) entry.getValue();

            int currentQty = (int) products.get(item).get("Quantity");
            int orderQty = order.get(item);
            StringBuilder newString = new StringBuilder();
            newString.append(id + " ");
            newString.append(user + " ");
            newString.append(item + " ");
            newString.append(date + " ");
            newString.append(orderQty + " ");
            newString.delete(newString.length() - 1, newString.length());
            writeToOrdersCSV(newString, user);
            System.out.println(item + ":" + currentQty + "in stock");
            System.out.println("The order amount is: " + orderQty);

            if (orderQty > currentQty) {
                response += item + " None available at this date, can't place order\n";

            } else {

                int newQty = currentQty - orderQty;
                products.get(item).replace("Quantity", newQty);
                convertHashMap();
                response += item + "It's on it's way boss\n";
            }
        }
        lock.unlock();
        return response;
        //If they are, make changes to file


    }

    public List displayPrevOrders(String user) throws FileNotFoundException {
        Products prod = new Products();
        List<String[]> orders = prod.displayOrders(user);
        return orders;

    }

    public int predictAvailabilityForAnItem(LocalDateTime date, String item) throws RemoteException, FileNotFoundException {
        Products prod = new Products();
        List<String[]> orders = prod.displayOrders(item); //displays orders from the Order.csv file that contains item for instance "Apple"
        //get restock date for the item
        int restockdate = (int) products.get(item).get("Restock Date");
        int restockq = (int) products.get(item).get("Restock Quantity");
        int predictQ = (int) products.get(item).get("Quantity");
        int orderqty = 0;

        LocalDate restock = LocalDate.now().withDayOfMonth(restockdate).plusMonths(1);
        while (restock.isBefore(ChronoLocalDate.from(date))) {
            restock = restock.plusMonths(1);
            predictQ += restockq;
        }
        for (String[] items : orders) { //for each line that appeared in orders that contains the item
            String orderD = items[3]; //get the date
            orderqty = Integer.parseInt(items[4]); //get the qty
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate orderDate = LocalDate.parse(orderD, formatter);
            if (orderDate.isBefore(ChronoLocalDate.from(date))) { //if the date is before the current order date
                predictQ -= orderqty;
            }
        }
        return predictQ;
    }

    public StringBuilder predictAvailabilityForAll() throws FileNotFoundException {
        Products prod = new Products();
        StringBuilder string = new StringBuilder();
        //get restock date for the item
        //iterate through hashmap
        LocalDate date = LocalDate.now().plusMonths(1);
        LocalDate SixMonths = LocalDate.now().plusMonths(7);
        int d = 1;

        while(date.isBefore(SixMonths)) {
            string.append("| Month:" + d + " |" + " Item : Qty  "+  "\n");
            for (String item : products.keySet()) {
                int restockdate = (int) products.get(item).get("Restock Date");
                int restockq = (int) products.get(item).get("Restock Quantity");
                int predictQ = (int) products.get(item).get("Quantity");
                List<String[]> orders = prod.displayOrders(item); //displays orders from the Order.csv file that contains item for instance "Apple"
                int orderqty = 0;

                LocalDate restock = LocalDate.now().withDayOfMonth(restockdate).plusMonths(1);
                while (restock.isBefore(date)) {
                    restock = restock.plusMonths(1);
                    predictQ += restockq;
                }
                for (String[] items : orders) { //for each line that appeared in orders that contains the item
                    String orderD = items[3]; //get the date
                    orderqty = Integer.parseInt(items[4]); //get the qty
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate orderDate = LocalDate.parse(orderD, formatter);
                    if (orderDate.isBefore(ChronoLocalDate.from(date))) { //if the date is before the current order date
                        predictQ -= orderqty;
                    }
                }
                //month item qty
                string.append("         " + " | " + item + ": "  + predictQ + "\n");
            }
            date = date.plusMonths(1);
            string.append("\n");
            d +=1;
        }
        return string;
    }
}
