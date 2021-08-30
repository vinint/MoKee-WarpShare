package org.mokee.warpshare.certificate;

import android.content.Context;

import org.mokee.warpshare.WarpShareApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okio.Buffer;

public class CertificateManager {
    private String KEYSTORE_PWD = "android";

    private SSLContext mServerSSlContext;

    public CertificateManager(Context context) {
        initServerSSLContext2(context);
    }

    //客户端信任所有证书
    public SSLSocketFactory createClientSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()},new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    public SSLContext getServerSSLContext() {
        return mServerSSlContext;
    }

    private void initServerSSLContext2(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("airdrop.bks");
            // 选择keystore的储存类型，andorid只支持BKS
            KeyStore keyStore = KeyStore. getInstance("BKS");
            keyStore.load(inputStream, KEYSTORE_PWD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, KEYSTORE_PWD.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            //选择安全协议的版本
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(),new TrustManager[]{new TrustAllCerts()}, null);
            mServerSSlContext = sslContext;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class TrustAllCerts implements X509TrustManager{

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
