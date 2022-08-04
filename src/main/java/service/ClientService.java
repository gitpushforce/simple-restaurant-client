package service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class ClientService {

    private static final Integer GET = 0;
    private static final Integer PUT = 1;
    private static final Integer DELETE = 2;

    public void request (String target, Integer method, String threadName) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        if (Objects.equals(method, GET)) {
            request = HttpRequest.newBuilder().uri(URI.create(target))
                    .GET().build();
        } else if (Objects.equals(method, PUT)) {
            request = HttpRequest.newBuilder().uri(URI.create(target))
                    .PUT(HttpRequest.BodyPublishers.ofString("put")).build();
        } else if  (Objects.equals(method, DELETE)) {
            request = HttpRequest.newBuilder().uri(URI.create(target))
                    .DELETE().build();
        }

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(responseBody -> parse(responseBody, threadName))
                .join();
    }

    private static String parse(String responseBody, String threadName) {
        if (responseBody.contains("message")) {
            parseErrorMessage(responseBody, threadName);
            return null;
        }
        if (responseBody.startsWith("{\"count\""))
            parseQueryAll(responseBody, threadName);
        else if (responseBody.startsWith("{\"orderId\""))
            parseQueryItem(responseBody, threadName);
        else if (responseBody.contains("success"))
            parseAddDel(responseBody, threadName);
        return null;
    }

    private static void parseQueryAll (String responseBody, String threadName) {
        JSONObject obj = new JSONObject(responseBody);
        String res = "[" + threadName + "]\nShowing all items for a specified table number\n";
        if (Objects.equals(obj.get("count"), 0)) res += "No orders placed for this table\n";
        else res += "items count " + obj.get("count") + "\n";
        JSONArray orders = new JSONArray(obj.get("order").toString());
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            res += order + "\n";
        }
        System.out.println(res);
    }

    private static void parseQueryItem (String responseBody, String threadName) {
        JSONObject item = new JSONObject(responseBody);
        String res = "[" + threadName + "]\nshowing a specified item for a specified table number\n";
        if ("null".equals(item.get("orderId").toString())) res += "Data not found\n";
        else res += item + "\n";
        System.out.println(res);
    }

    private static void parseAddDel (String responseBody, String threadName) {
        JSONObject item = new JSONObject(responseBody);
        String res = "[" + threadName + "]\n";
        boolean success = item.getBoolean("success");
        if (success) {
            if (threadName.contains("/add"))
                res += "The order was placed correctly";
            else if (threadName.contains("/del"))
                res += "The order was removed correctly";
        } else {
            if (threadName.contains("/add"))
                res += "The order could not be placed";
            else if (threadName.contains("/del"))
                res += "The order could not be removed or already does not exist";
        }
        System.out.println(res);
    }

    private static void parseErrorMessage (String responseBody, String threadName) {
        JSONObject item = new JSONObject(responseBody);
        String res = "[" + threadName + "]\n";
        res += item.getString("message");
        System.out.println(res);
    }

    public void requestOrderOfTable (String target) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(target))
                .GET().build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(ClientService::parseRequestOrderOfTable)
                .join();
    }

    private static String parseRequestOrderOfTable(String responseBody) {
        if (responseBody.contains("message")) {
            System.out.println("No orders were placed for this table yet");
            return null;
        }
        JSONObject obj = new JSONObject(responseBody);
        String res = "[Order Id's in this table]\n";
        if (Objects.equals(obj.get("count"), 0)) {
            res += "No orders placed for this table\n";
            System.out.println(res);
            return null;
        }

        JSONArray orders = new JSONArray(obj.get("order").toString());
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            Integer orderId = order.getInt("orderId");
            if ((i + 1) % 4 == 0)
                res += orderId + "  \n";
            else
                res += orderId + "   ";
        }
        System.out.println(res);
        return null;
    }

    public void requestItemsAvailable (String target) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(target))
                .GET().build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(ClientService::parseItemsAvailableRes)
                .join();
    }

    private static String parseItemsAvailableRes(String responseBody) {
        if (responseBody.contains("message")) {
            System.out.println("No items available, verify DB");
            return null;
        }

        String res = "[Items available to order]\n";
        JSONArray items = new JSONArray(responseBody);
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String itemId = item.getString("itemId");
            if ((i + 1) % 4 == 0)
                res += itemId + "  \n";
            else
                res += itemId + "   ";
        }
        System.out.println(res);
        return null;
    }
}
