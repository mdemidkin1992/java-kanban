import managers.servers.KVServer;

import java.io.IOException;

/**
 * @author Maxim Demidkin
 * @Date 01.03.2023
 */

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer(8078).start();
    }
}
