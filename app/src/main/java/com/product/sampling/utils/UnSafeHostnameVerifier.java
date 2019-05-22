package com.product.sampling.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by gongdongyang on 2018/9/25.
 */

public class UnSafeHostnameVerifier implements HostnameVerifier {
    private String host;

    public UnSafeHostnameVerifier(String host) {
        this.host = host;
//        HttpLog.i("###############　UnSafeHostnameVerifier " + host);
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
//        HttpLog.i("############### verify " + hostname + " " + this.host);
//        if("btmxapp.com".equals(hostname)){
//            return true;
//        }else{
//            HostnameVerifier hv= HttpsURLConnection.getDefaultHostnameVerifier();
//            return hv.verify(hostname,session);
//        }
        return true;
    }

}
