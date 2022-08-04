package client;

import multithreading.ClientRunnable;
import service.ClientService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientApp {

    private static HttpURLConnection con;

    //final static String domain = "http://localhost:8080";
    final static String domain = "http://153.121.71.32:8081";

    public static void main(String[] args) throws IOException {
        Map<String, String> requests = new HashMap<>();

        System.out.println("Write the number of Threads (up to 10)");
        Scanner in = new Scanner(System.in);
        int threads = in.nextInt();

        for (int i = 1; i <= threads; i++) {
            System.out.println("Select an action for thread " + i);
            System.out.println("1. Show all items in a table");
            System.out.println("2. Show specified order");
            System.out.println("3. create order");
            System.out.println("4. delete order");

            in = new Scanner(System.in);
            int action = in.nextInt();

            int tableNum;
            String orderId, itemId;
            String reqTail = "";
            ClientService service = new ClientService();
            switch (action) {
                case 1:
                    System.out.println("Select a table number");
                    in = new Scanner(System.in);
                    tableNum = in.nextInt();
                    reqTail = "queryall?table=" + tableNum;
                    break;
                case 2:
                    // Show Specified item of a specified table
                    System.out.println("Select a table number");
                    in = new Scanner(System.in);
                    tableNum = in.nextInt();
                    // shows available orderId's in the selected table
                    service.requestOrderOfTable(domain + "/v1/queryall?table=" + tableNum);
                    System.out.println("Select an order id in the list above");
                    in = new Scanner(System.in);
                    orderId = in.nextLine();
                    reqTail = "queryitem?orderId=" + orderId;
                    break;
                case 3:
                    // Create order
                    System.out.println("Select a table number");
                    in = new Scanner(System.in);
                    tableNum = in.nextInt();
                    // TODO Show the items ids availables
                    service.requestItemsAvailable(domain + "/v1/items");
                    System.out.println("Select an item id (e.g. 0003)");
                    in = new Scanner(System.in);
                    itemId = in.nextLine();
                    reqTail = "add?table=" + tableNum + "&item=" + itemId;
                    break;
                case 4:
                    //  remove a specified item for a specified table number
                    System.out.println("Select a table number");
                    in = new Scanner(System.in);
                    tableNum = in.nextInt();
                    // shows available orderId's in the selected table
                    service.requestOrderOfTable(domain + "/v1/queryall?table=" + tableNum);
                    System.out.println("Select an order id (e.g. 5)");
                    in = new Scanner(System.in);
                    orderId = in.nextLine();
                    reqTail = "del?orderId=" + orderId;
                    break;

            }
        requests.put("thread-" + i, domain + "/v1/" + reqTail);
        }

        // creating threads
        for (int i = 1; i <= threads; i++) {
            ClientRunnable tmpRunnable = new ClientRunnable(requests.get("thread-" + i));
            Thread tmpThread = new Thread(tmpRunnable);
            tmpThread.setName("thread-" + i + ": " + requests.get("thread-" + i));
            tmpThread.start();
        }
    }
}
