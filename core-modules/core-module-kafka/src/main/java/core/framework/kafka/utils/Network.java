package core.framework.kafka.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ebin
 */
public final class Network {    // in cloud env, the application vm or container stick with same host ip / name during entire lifecycle
    public static final String LOCAL_HOST_ADDRESS;
    public static final String LOCAL_HOST_NAME;

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            LOCAL_HOST_ADDRESS = address.getHostAddress();
            LOCAL_HOST_NAME = address.getHostName();
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    private Network() {
    }
}
