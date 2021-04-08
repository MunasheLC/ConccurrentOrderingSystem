package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Products {

    public List<String[]> displayProducts() throws java.io.FileNotFoundException {
        Scanner input = new Scanner(new File("C:/Users/fonat/IdeaProjects/OrderingSystem/src/server/Products.csv"));
        input.useDelimiter(",|\n");
        String[] line = new String[0];
        List<String[]> line_list = new ArrayList<>();
        while (input.hasNextLine())  //returns a boolean value
        {
            String lines = input.nextLine(); //find and returns the next complete token from this scanner
            line = lines.split(" ");
            line_list.add(line);
        }
        return line_list; //sends the list of lines to the server
    }
}