package cz.vity.freerapid.sandbox;

/**
 * @author Ladislav Vitasek
 */

import static java.lang.System.out;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

public class ListNets {
    public static void main(String args[]) throws SocketException, UnknownHostException {
        //169.254.190.128
        final byte[] ip = {(byte) 169, (byte) 254, (byte) 190, (byte) 128};
        final InetAddress byName = InetAddress.getByAddress(ip);
        final NetworkInterface networkInterface = NetworkInterface.getByInetAddress(byName);
        System.out.println("networkInterface = " + networkInterface.getName());

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());

        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
    }
}