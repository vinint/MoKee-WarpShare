package org.mokee.warpshare.wifip2p;

import android.content.Context;

import org.mokee.warpshare.base.DiscoverListener;
import org.mokee.warpshare.base.Discoverer;
import org.mokee.warpshare.base.Entity;
import org.mokee.warpshare.base.SendListener;
import org.mokee.warpshare.base.Sender;
import org.mokee.warpshare.base.SendingSession;

import java.util.List;

public class P2PwifiManager implements
        Discoverer,
        Sender<WifiP2pPeer> {
    Context mContext;
    AndroidWifiP2pController mWifiP2pController;

    @Override
    public void startDiscover(DiscoverListener discoverListener) {
        mWifiP2pController.registerP2PService();
        mWifiP2pController.discoveryP2PService(discoverListener);
    }

    @Override
    public void stopDiscover() {
        mWifiP2pController.unRegisterP2PService();
        mWifiP2pController.stopDiscoveryP2PService();
    }

    public void startDiscoverable(){

    }


    @Override
    public SendingSession send(WifiP2pPeer peer, List<Entity> entities, SendListener listener) {
        mWifiP2pController.connectWifiP2pDevice(peer.id,peer.name);
        return null;
    }

    public P2PwifiManager(Context context){
        mContext = context.getApplicationContext();
        mWifiP2pController = new AndroidWifiP2pController();
        mWifiP2pController.init(mContext);
    }
}
