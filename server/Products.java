package server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Products {

    HashMap<String, String> products;
    List<String[]> prodList;

    public Products(){



    }

    public List<String[]> displayProducts() throws java.io.FileNotFoundException {
        Scanner input = new Scanner(new File("server/Products.csv"));
        input.useDelimiter(",|\n");
        String[] line = new String[0];
        List<String[]> line_list = new ArrayList<>();
        while (input.hasNextLine())  //returns a boolean value
        {
            String lines = input.nextLine(); //find and returns the next complete token from this scanner
            line = lines.split(" ");
            line_list.add(line);
        }
        input.close();
        prodList = line_list;
//        System.out.println("in line listjsshs + " + line_list);
        return line_list; //sends the list of lines to the server
    }
    public List<String[]> displayOrders(String text) throws java.io.FileNotFoundException {
        Scanner input = new Scanner(new File("server/Orders.csv"));
        input.useDelimiter(",|\n");
        String[] line = new String[0];
        List<String[]> user_Order_list = new ArrayList<>();
        while (input.hasNextLine())  //returns a boolean value
        {
            String lines = input.nextLine(); //find and returns the next complete token from this scanner
            if (lines.contains(text)){
                System.out.println("lines that contain item from Order.csv : " + text + " " + lines);
                line = lines.split(" ");
                user_Order_list.add(line);
        }
        }
        input.close();
        return user_Order_list; //sends the list of lines to the server
    }

    public void removeOrders(String id) throws IOException {
        StringBuilder s = new StringBuilder();
        Scanner input = new Scanner(new File("server/Orders.csv"));

        while (input.hasNextLine())  //returns a boolean value
        {
            String lines = input.nextLine(); //find and returns the next complete token from this scanner
            if (!lines.contains(id)){
                s.append(lines);
                s.append("\n");
            }
        }
        input.close();

        PrintWriter writer = new PrintWriter("server/Orders.csv");
        writer.println(s);
        writer.close();
    }


    public void overWrite(StringBuilder map) throws IOException {
        PrintWriter writer = new PrintWriter("server/Products.csv");
        writer.println(map);
        writer.close();
    }
}