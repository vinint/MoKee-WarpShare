package org.mokee.warpshare.wifip2p;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class AndroidNsdController {
    private static final String TAG = "AndroidNsdController";
    private static final String SERVICE_TYPE = "_wifip2p._tcp.local.";
    private Context mContext;
    private NsdManager mNsdManager;
    private void init(Context context){
        mContext = context.getApplicationContext();
        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
    }

    private void registerNSDService() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("NsdChat_" + Build.MANUFACTURER);
        serviceInfo.setServiceType(SERVICE_TYPE);
        InetAddress wlanInetAddress = getInetAddress("wlan");
        InetAddress p2pInetAddress = getInetAddress("p2p");
        if (wlanInetAddress != null) {
            serviceInfo.setAttribute("WLAN_HOST", wlanInetAddress.getHostAddress());
        }
        if (p2pInetAddress != null) {
            serviceInfo.setAttribute("P2P_HOST", p2pInetAddress.getHostAddress());
            serviceInfo.setHost(p2pInetAddress);
        }
        serviceInfo.setHost(p2pInetAddress != null ? p2pInetAddress : wlanInetAddress);

        serviceInfo.setPort(8770);

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "NsdManager onRegistrationFailed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "NsdManager onUnregistrationFailed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "NsdManager onServiceRegistered");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "NsdManager onServiceUnregistered");
            }
        });
    }

    private void discoveryNSDService() {
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, new NsdManager.DiscoveryListener() {
            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
//                Log.d(TAG, "NsdManager.discoverServices.onServiceFound" + service.toString());
                mNsdManager.resolveService(service, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                        Log.d(TAG, "NsdManager.resolveService.onServiceResolved "+ serviceInfo.toString());
                    }
                });

            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            }
        });
    }


    private InetAddress getInetAddress(String networkInterfaceName) {
        InetAddress inetAddressResult = null;
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            List<NetworkInterface> networkInterfaceList = Collections.list(networkInterfaceEnumeration);
            for (NetworkInterface item : networkInterfaceList) {
                if (item.getName().contains(networkInterfaceName)) {
                    InetAddress inetAddress = getInetAddress(item);
                    if (inetAddress != null && !TextUtils.isEmpty(inetAddress.getHostAddress())) {
                        Log.d(TAG, "NetworkInterface name: " + item.getName());
                        Log.d(TAG, "NetworkInterface HostAddress: " + inetAddress.getHostAddress());
                        inetAddressResult = inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return inetAddressResult;
    }

    private InetAddress getInetAddress(NetworkInterface networkInterface) {
        InetAddress inetAddress = null;
        final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        Inet6Address address6 = null;
        Inet4Address address4 = null;
        while (addresses.hasMoreElements()) {
            final InetAddress addr = addresses.nextElement();
            if (address6 == null && addr instanceof Inet6Address) {
                try {
                    // Recreate a non-scoped address since we are going to advertise it out
                    address6 = (Inet6Address) Inet6Address.getByAddress(null, addr.getAddress());
                } catch (UnknownHostException ignored) {
                }
            } else if (address4 == null && addr instanceof Inet4Address) {
                address4 = (Inet4Address) addr;
            }
        }

        if (address4 != null) {
            inetAddress = address4;
        } else if (address6 != null) {
            inetAddress = address6;
        }
        return inetAddress;
    }
}
