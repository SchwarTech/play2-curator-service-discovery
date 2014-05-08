package com.schwartech.curator.service.discovery.util;

import org.apache.curator.x.discovery.LocalIpFilter;
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
        return (adr != null) && !adr.isLoopbackAddress() && (adr instanceof Inet4Address) && (nif.isPointToPoint() || !adr.isLinkLocalAddress());
    }
}
