package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        return line_list; //sends the list of lines to the server
    }

    public void overWrite(StringBuilder map) throws IOException {
        PrintWriter writer = new PrintWriter("server/Products.csv");
        writer.println(map);
        writer.close();
    }
}