package org.mokee.warpshare.certificate;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okio.Buffer;

public class CertificateManager {
    private SSLContext mSSlContext;
    private KeyManager[] keyManagers;
    private TrustManager[] trustManagers;

    public CertificateManager(Context context) {
        trustManagers = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        try {
            mSSlContext = SSLContext.getInstance("SSL");
            mSSlContext.init(null, trustManagers, new SecureRandom());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SSLContext getSSLContext() {
        return mSSlContext;
    }

    public TrustManager[] getTrustManagers() {
        return trustManagers;
    }


//    private void init(Context context) {
//        String appleRootCertificateAuthority = "-----BEGIN CERTIFICATE-----\n" +
//                "MIIEuzCCA6OgAwIBAgIBAjANBgkqhkiG9w0BAQUFADBiMQswCQYDVQQGEwJVUzET\n" +
//                "MBEGA1UEChMKQXBwbGUgSW5jLjEmMCQGA1UECxMdQXBwbGUgQ2VydGlmaWNhdGlv\n" +
//                "biBBdXRob3JpdHkxFjAUBgNVBAMTDUFwcGxlIFJvb3QgQ0EwHhcNMDYwNDI1MjE0\n" +
//                "MDM2WhcNMzUwMjA5MjE0MDM2WjBiMQswCQYDVQQGEwJVUzETMBEGA1UEChMKQXBw\n" +
//                "bGUgSW5jLjEmMCQGA1UECxMdQXBwbGUgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkx\n" +
//                "FjAUBgNVBAMTDUFwcGxlIFJvb3QgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw\n" +
//                "ggEKAoIBAQDkkakJH5HbHkdQ6wXtXnmELes2oldMVeyLGYne+Uts9QerIjAC6Bg+\n" +
//                "+FAJ039BqJj50cpmnCRrEdCju+QbKsMflZ56DKRHi1vUFjczy8QPTc4UadHJGXL1\n" +
//                "XQ7Vf1+b8iUDulWPTV0N8WQ1IxVLFVkds5T39pyez1C6wVhQZ48ItCD3y6wsIG9w\n" +
//                "tj8BMIy3Q88PnT3zK0koGsj+zrW5DtleHNbLPbU6rfQPDgCSC7EhFi501TwN22IW\n" +
//                "q6NxkkdTVcGvL0Gz+PvjcM3mo0xFfh9Ma1CWQYnEdGILEINBhzOKgbEwWOxaBDKM\n" +
//                "aLOPHd5lc/9nXmW8Sdh2nzMUZaF3lMktAgMBAAGjggF6MIIBdjAOBgNVHQ8BAf8E\n" +
//                "BAMCAQYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUK9BpR5R2Cf70a40uQKb3\n" +
//                "R01/CF4wHwYDVR0jBBgwFoAUK9BpR5R2Cf70a40uQKb3R01/CF4wggERBgNVHSAE\n" +
//                "ggEIMIIBBDCCAQAGCSqGSIb3Y2QFATCB8jAqBggrBgEFBQcCARYeaHR0cHM6Ly93\n" +
//                "d3cuYXBwbGUuY29tL2FwcGxlY2EvMIHDBggrBgEFBQcCAjCBthqBs1JlbGlhbmNl\n" +
//                "IG9uIHRoaXMgY2VydGlmaWNhdGUgYnkgYW55IHBhcnR5IGFzc3VtZXMgYWNjZXB0\n" +
//                "YW5jZSBvZiB0aGUgdGhlbiBhcHBsaWNhYmxlIHN0YW5kYXJkIHRlcm1zIGFuZCBj\n" +
//                "b25kaXRpb25zIG9mIHVzZSwgY2VydGlmaWNhdGUgcG9saWN5IGFuZCBjZXJ0aWZp\n" +
//                "Y2F0aW9uIHByYWN0aWNlIHN0YXRlbWVudHMuMA0GCSqGSIb3DQEBBQUAA4IBAQBc\n" +
//                "NplMLXi37Yyb3PN3m/J20ncwT8EfhYOFG5k9RzfyqZtAjizUsZAS2L70c5vu0mQP\n" +
//                "y3lPNNiiPvl4/2vIB+x9OYOLUyDTOMSxv5pPCmv/K/xZpwUJfBdAVhEedNO3iyM7\n" +
//                "R6PVbyTi69G3cN8PReEnyvFteO3ntRcXqNx+IjXKJdXZD9Zr1KIkIxH3oayPc4Fg\n" +
//                "xhtbCS+SsvhESPBgOJ4V9T0mZyCKM2r3DYLP3uujL/lTaltkwGMzd/c6ByxW69oP\n" +
//                "IQ7aunMZT7XZNn/Bh1XZp5m5MkL72NVxnn6hUrcbvZNCJBIqxw8dtk2cXmPIS4AX\n" +
//                "UKqK1drk/NAJBzewdXUh\n" +
//                "-----END CERTIFICATE-----\n";
//
//        String mokeeCertificateAuthority ="-----BEGIN CERTIFICATE-----\n" +
//                "MIID7zCCAtegAwIBAgIJAJFQfdLg9JlbMA0GCSqGSIb3DQEBCwUAMIGMMQswCQYD\n" +
//                "VQQGEwJDTjERMA8GA1UECAwIU2hhbmdoYWkxIjAgBgNVBAoMGU1vS2VlIE9wZW4g\n" +
//                "U291cmNlIFByb2plY3QxKjAoBgNVBAsMIVdhcnBTaGFyZSBDZXJ0aWZpY2F0aW9u\n" +
//                "IEF1dGhvcml0eTEaMBgGA1UEAwwRV2FycFNoYXJlIFJvb3QgQ0EwIBcNMTkwOTI2\n" +
//                "MDgxODA4WhgPMjExOTA5MDIwODE4MDhaMIGMMQswCQYDVQQGEwJDTjERMA8GA1UE\n" +
//                "CAwIU2hhbmdoYWkxIjAgBgNVBAoMGU1vS2VlIE9wZW4gU291cmNlIFByb2plY3Qx\n" +
//                "KjAoBgNVBAsMIVdhcnBTaGFyZSBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEaMBgG\n" +
//                "A1UEAwwRV2FycFNoYXJlIFJvb3QgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw\n" +
//                "ggEKAoIBAQDQRwgbGMUengpaBwezp7H3P5XC7tlFaCcm0mn4+6Pa9uMQW/L3ANmX\n" +
//                "B4Hchj7JsXskq0LwlFftbfjUuCgEsIQgFWUmCQi5sHKpaNcI/Pq/MTamamKzPnYO\n" +
//                "HaKnlnXLGjzK+aa8uy/Z1o0iTnCqBFM0Wv5ps3rtapVi3kXg1RRPUnTB62Y8jVQ3\n" +
//                "CHLiM4y4KBMPmaFxTf+FU0wxNnk5BoOq9ocfM+gnscc0qRDBvqcsks8CXwY7VsSp\n" +
//                "nfQ0oIdP76St0S93J7hf2WO7xmqOjotROFhMJkwrBZLo60AXnPs/iPz2bU+GQPGp\n" +
//                "Cn1c9JTQuwWoMxyNV1gi4SZyYIb9Hk2DAgMBAAGjUDBOMB0GA1UdDgQWBBTxb9SE\n" +
//                "ik/1Q/nuiKtdOXxtN933OjAfBgNVHSMEGDAWgBTxb9SEik/1Q/nuiKtdOXxtN933\n" +
//                "OjAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQAZFdJc0Yu7NE6IOxi+\n" +
//                "GUiwynWmO40JfCX7EvpsbiN1qQG3lec7kxE1ordLwld4NaEmPnoJgfEGRIX77hKr\n" +
//                "kTHQWUq1mf7W3+D+rTeg/30eyfHXecsDJeK6drDgP6+C5p7cUHsj9hQUP/HthKBA\n" +
//                "Wr7MEQZOnsG0Ib1l2S0j/bBepiK5bF32YSCb5OmFVbCyVXMoe5y5MMBAfj/4a6w0\n" +
//                "cXQZv88TpnO7awt29tUqc+wkr/nhFreyrX5kMAYtPTkRl1QQGjBdqD+U+X/Thdz7\n" +
//                "w5n19lAa2AIdb5d+riQQ14fTQ9kBX2BHJvhPE8sNvh7dM5V6BtkKy4dwWA2Ken8/\n" +
//                "uHHH\n" +
//                "-----END CERTIFICATE-----\n";
//
//        InputStream inputStreamCert = new Buffer()
//                .writeUtf8(appleRootCertificateAuthority)
//                .writeUtf8(mokeeCertificateAuthority)
//                .inputStream();
//
//        trustManagerForCertificates(inputStreamCert);
//    }
//
//    private void trustManagerForCertificates(InputStream in) {
//        try {
//
//
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
//            if (certificates.isEmpty()) {
//                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
//            }
//
//            // Put the certificates a key store.
//            char[] password = "password".toCharArray(); // Any password will work.
//            KeyStore keyStore = newEmptyKeyStore(password);
//            int index = 0;
//            for (Certificate certificate : certificates) {
//                String certificateAlias = Integer.toString(index++);
//                keyStore.setCertificateEntry(certificateAlias, certificate);
//            }
//
//            // Use it to build an X509 trust manager.
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
//                    KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, password);
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
//                    TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//
//            keyManagers = keyManagerFactory.getKeyManagers();
//            trustManagers = trustManagerFactory.getTrustManagers();
//        } catch (GeneralSecurityException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
//        try {
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            InputStream in = null; // By convention, 'null' creates an empty key store.
//            keyStore.load(in, password);
//            return keyStore;
//        } catch (IOException e) {
//            throw new AssertionError(e);
//        }
//    }
}
