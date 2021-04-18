package io.daiwei.rpc.util;

import io.daiwei.rpc.exception.DaiweiRpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.net.SocketServer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class NetUtil {

    private NetUtil() {}

    public static String[] getHostAndPort(String addr) {
        String[] res = new String[2];
        if (addr.contains(":")) {
            String[] split = addr.split(":");
            return new String[]{split[0].trim(), split[1].trim()};
        } else {
            throw new DaiweiRpcException("addr[ "+ addr +" ] is illegal！");
        }
    }

    public static int findAvailablePort(int defaultPort) {
        int port = defaultPort;
        while (port < 65535) {
            if (isAvailablePort(port)) {
                return port;
            } else {
                port++;
            }
        }
        port -= defaultPort;
        while (port > 0) {
            if (isAvailablePort(port)) {
                return port;
            } else {
                port--;
            }
        }
        throw new DaiweiRpcException("not find available port");
    }

    public static boolean isAvailablePort(int port) {
        boolean available = false;
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            available = true;
            server.close();
        } catch (IOException e) {
            available = false;
            log.debug("port[{}] is in use", port);
        }
        return available;
    }

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (!netInterface.isLoopback() && !netInterface.isVirtual() && netInterface.isUp()) {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("IP地址获取失败", e);
        }
        return "";
    }

}
