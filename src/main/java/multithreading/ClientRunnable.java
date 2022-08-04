package multithreading;

import service.ClientService;

public class ClientRunnable implements Runnable {

    private String uri;
    private static final Integer GET = 0;
    private static final Integer PUT = 1;
    private static final Integer DELETE = 2;

    public ClientRunnable(String uri) {
        this.uri = uri;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        ClientService service = new ClientService();
        if (uri.contains("query"))
            service.request(uri, GET, threadName);
        else if (uri.contains("add"))
            service.request(uri, PUT, threadName);
        else if (uri.contains("del"))
            service.request(uri, DELETE, threadName);
    }
}
