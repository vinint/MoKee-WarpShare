package org.mokee.warpshare.wifip2p;

import android.net.wifi.p2p.WifiP2pDevice;

import org.mokee.warpshare.base.Peer;

public class WifiP2pPeer extends Peer {
    public WifiP2pPeer(String deviceAddress, String name) {
        super(deviceAddress, name);
    }
}
