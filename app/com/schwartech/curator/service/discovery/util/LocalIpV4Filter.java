package com.schwartech.curator.service.discovery.util;

import org.apache.curator.x.discovery.LocalIpFilter;
import play.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * Created by jeff on 5/8/14.
 */
public class LocalIpV4Filter implements LocalIpFilter {
    @Override
    public boolean use(NetworkInterface nif, InetAddress adr) throws SocketException
    {
        if (adr == null) return false;
        if (adr.isLoopbackAddress()) return false;

        boolean isIpv4 = (adr instanceof Inet4Address);
//        Logger.info("isIpv4: " + isIpv4);

        return isIpv4 && (nif.isPointToPoint() || !adr.isLinkLocalAddress());
    }
}
