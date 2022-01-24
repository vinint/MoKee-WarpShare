package org.mokee.warpshare.wifip2p;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.mokee.warpshare.base.DiscoverListener;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidWifiP2pController {
    private final String TAG = "AndroidWifiP2pController";
    private static final String SERVICE_TYPE = "_wifip2p._tcp";
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mWifiP2pChannel;

    public void init(Context context) {
        mWifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiP2pChannel = mWifiP2pManager.initialize(context, Looper.myLooper(), null);
    }

    @SuppressLint("MissingPermission")
    public void registerP2PService() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("buddyname", Build.MANUFACTURER + (int) (Math.random() * 1000));
        record.put("available", "visible");

        String instanceName = Build.MANUFACTURER + (int) (Math.random() * 1000);

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance(instanceName, SERVICE_TYPE, record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mWifiP2pManager.addLocalService(mWifiP2pChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                Log.d(TAG, "WifiP2pManager.addLocalService.onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d(TAG, "WifiP2pManager.addLocalService.onFailure "+ failureReason(reason));
            }
        });
    }

    public void unRegisterP2PService(){
        mWifiP2pManager.clearLocalServices(mWifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"WifiP2pManager.clearLocalServices.onSuccess ");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"WifiP2pManager.clearLocalServices.onFailure "+ failureReason(reason));
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void discoveryP2PService(DiscoverListener discoverListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mWifiP2pManager.requestConnectionInfo(mWifiP2pChannel,(WifiP2pInfo info)->{
                if (info!=null&&
                        info.groupFormed&&
                        !info.isGroupOwner){
                    //info.groupOwnerAddress;
                    Log.d(TAG,"被动获取Group p2p ip 地址："+info.groupOwnerAddress.getHostAddress());
                }
            });
            mWifiP2pManager.requestNetworkInfo(mWifiP2pChannel,(NetworkInfo networkInfo)->{
                if (networkInfo!=null&&networkInfo.isConnected()){
                    Log.d(TAG, "WifiP2pManager.requestNetworkInfo Wifi P2p已连接");
                    InetAddress wlanInetAddress = getInetAddress("wlan");
                    InetAddress p2pInetAddress = getInetAddress("p2p");
                    if (wlanInetAddress!=null){
                        Log.d(TAG,"被动获取当前wlan ip 地址："+wlanInetAddress.getHostAddress());
                    }
                    if (p2pInetAddress!=null){
                        Log.d(TAG,"被动获取当前p2p ip 地址："+p2pInetAddress.getHostAddress());
                    }
                }
            });
        }

        final HashMap<String, String> buddies = new HashMap<String, String>();
        mWifiP2pManager.setDnsSdResponseListeners(mWifiP2pChannel,
                (String instanceName, String registrationType, WifiP2pDevice resourceType) -> {
                    //初步返回
                    // Add to the custom adapter defined specifically for showing
                    Log.d(TAG, "mWifiP2pManager.setDnsSdResponseListeners onDnsSdServiceAvailable " + resourceType.toString());
                },
                (String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) -> {
                    //详细返回
                    /* Callback includes:
                     * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
                     * record: TXT record dta as a map of key/value pairs.
                     * device: The device running the advertised service.
                     */
                    discoverListener.onPeerFound(new WifiP2pPeer(srcDevice.deviceAddress,srcDevice.deviceName+"-vin"));
                    Log.d(TAG, "WifiP2pManager.setDnsSdResponseListeners onDnsSdTxtRecordAvailable " + srcDevice.toString());
                    Log.d(TAG, "WifiP2pManager.setDnsSdResponseListeners onDnsSdTxtRecordAvailable fullDomain = " + fullDomainName);
                    Log.d(TAG, "WifiP2pManager.setDnsSdResponseListeners onDnsSdTxtRecordAvailable DnsSdTxtRecord available -" + txtRecordMap.toString());
                });

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mWifiP2pManager.addServiceRequest(mWifiP2pChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        // Success!
                        Log.d(TAG, "WifiP2pManager.addServiceRequest.onSuccess");
                    }

                    @Override
                    public void onFailure(int reason) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                        Log.d(TAG, "WifiP2pManager.addServiceRequest.onFailure " + failureReason(reason));
                    }
                });

        mWifiP2pManager.discoverServices(mWifiP2pChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Success!
                Log.d(TAG, "WifiP2pManager.discoverServices.onSuccess");

            }

            @Override
            public void onFailure(int reason) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d(TAG, "WifiP2pManager.discoverServices.onFailure " + failureReason(reason));
            }
        });

    }

    public void stopDiscoveryP2PService(){
        mWifiP2pManager.stopPeerDiscovery(mWifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "WifiP2pManager.stopPeerDiscovery.onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "WifiP2pManager.stopPeerDiscovery.onFailure " + failureReason(reason));
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void connectWifiP2pDevice(String deviceAddress, final String name) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        /*mac地址*/
        config.wps.setup = WpsInfo.PBC;
        //设置为组群拥有者
        config.groupOwnerIntent = 0;

        mWifiP2pManager.connect(mWifiP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "WifiP2pManager.connect.onSuccess");
                //1.获取当前wlan ip ,p2p ip
                InetAddress wlanInetAddress = getInetAddress("wlan");
                InetAddress p2pInetAddress = getInetAddress("p2p");
                if (wlanInetAddress!=null){
                    Log.d(TAG,"主动获取当前wlan ip 地址："+wlanInetAddress.getHostAddress());
                }
                if (p2pInetAddress!=null){
                    Log.d(TAG,"主动获取当前p2p ip 地址："+p2pInetAddress.getHostAddress());
                }

            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "WifiP2pManager.connect.onFailure " + failureReason(reason));
            }
        });
    }

    private String failureReason(int reason){
        String failureReason = "";
        if (WifiP2pManager.P2P_UNSUPPORTED == reason){
            failureReason = "运行该应用的设备不支持 WLAN 点对点";
        }else if (WifiP2pManager.BUSY == reason){
            failureReason = "系统太忙，无法处理请求";
        }else if (WifiP2pManager.ERROR == reason){
            failureReason = "由于出现内部错误，操作失败";
        }
        return failureReason;
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
