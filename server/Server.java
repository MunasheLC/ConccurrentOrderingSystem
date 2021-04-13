package server;

import shared.OSserver;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server implements OSserver {
    private final HashMap users = new HashMap<String,String>();

    HashMap<String, HashMap> products;


    //Always gets latest prod info
    public List<String[]> getProducts() throws FileNotFoundException { //get items from csv file in Product.java
        Products prod = new Products();
        List<String[]> items = prod.displayProducts(); //returns a list of products & their information
        System.out.println("displaying items");
        System.out.println(items);
        List<String> keys = new ArrayList<String>();
        keys.add("Quantity");
        keys.add("Restock Date");
        keys.add("Restock Quantity");


        for (String[] item : items){
            HashMap<String, Integer> values = new HashMap<String, Integer>();
            String name = item[0];

            int j = 0;
            for (int i=1 ; i < item.length; i++){



                int value = Integer.parseInt(item[i]);

                values.put(keys.get(j), value);
                j+=1;



                System.out.println("values: " + values);


            }


            products.put(name, values);


        }

        System.out.println(products);
        return items; //return these products to the client
    }

    public HashMap<String, HashMap> receiveProds() throws FileNotFoundException {
        getProducts() ;
        return products;
    }

    public Server() throws RemoteException{
        this.products = new HashMap<>();

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
                    System.out.println("User logged in");
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

    public String confirmOrder(HashMap<String, Integer> order){
        //Checks if items are in stock
        Set set = order.entrySet();
        Iterator iter = set.iterator();
        String response;
        response = "";

        while(iter.hasNext()){


            Map.Entry entry = (Map.Entry) iter.next();

            String item = entry.getKey().toString();
//            Integer qty = (Integer) entry.getValue();

            int currentQty = (int) products.get(item).get("Quantity");
            int orderQty = order.get(item);
            System.out.println(item + ":" + currentQty + "in stock");
            System.out.println("The order amount is: " + orderQty);

            if (orderQty > currentQty){
                response += item + " Out of stock, can't place order\n";


            }

            else {
                response += item + "It's on it's way boss\n";
            }





        }


        return response;
        //If they are, make changes to file


    }

}
