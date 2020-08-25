package com.product.sampling.httpmoudle.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by gdy on 2018/10/25.
 *
 *
 * cf-ray: 4b62e1b20fe47898-LAX
 * content-encoding: gzip
 * content-type: application/json
 * date: Tue, 12 Mar 2019 04:04:01 GMT
 * expect-ct: max-age=604800, report-uri="https://report-uri.cloudflare.com/cdn-cgi/beacon/expect-ct"
 * instance: 3-_-2
 * server: cloudflare
 * set-cookie: __cfduid=d471fb7fefe4eb3ec7099261dc22221e51552363440;
 * expires=Wed, 11-Mar-20 04:04:00 GMT;
 * path=/;
 * domain=.btmxapp.com;
 * HttpOnly;
 * authtoken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6IndxYW5kcm9pZEBnbWFpbC5jb20iLCJlbWFpbE9yUGhvbmUiOiJ3cWFuZHJvaWRAZ21haWwuY29tIiwiaXNFbWFpbCI6dHJ1ZSwidXNlcklkIjoiQzNGUGFEZ0RMUVdZWVlrbWJkbXlKRlU5b25OdnNteGoiLCJhY2NvdW50SWQiOiJjc2hKZzl2RlE0MG5XZkVRVkhlZG1PeUhRMnJrekZjeSIsImlzRW1haWxWZXJpZmllZCI6dHJ1ZSwicGFzc3dvcmRTdHJlbmd0aCI6MSwidHdvRmFjdG9yUmVxdWlyZWQiOnRydWUsInR3b0ZhY3RvclZlcmlmaWVkIjp0cnVlLCJ1c2VybmFtZSI6IndxKioqb20iLCJkYXRhRmVlVGVybUFjY2VwdGVkIjp0cnVlLCJreWNMZXZlbCI6MCwiYWNjb3VudFR5cGUiOiJOVUxMX1RZUEUiLCJkYWlseVdpdGhkcmF3TGltaXRJbkJ0YyI6IjIuMCIsIm1heFdpdGhkcmF3YWxCdGNWYWx1ZSI6IjIuMCIsIm1ha2VyTWluaW5nT3B0aW9uIjoiTUlOSU5HIiwidGFrZXJNaW5pbmdPcHRpb24iOiJNSU5JTkciLCJ2aXAiOjAsImNhblRyYWRlIjpudWxsLCJjYW5XaXRoZHJhdyI6bnVsbCwiYWNjb3VudEdyb3VwIjo1LCJhdXRob3JpemF0aW9uIjoiMnpEdVlPU01jNWoyQmtBcFh0NU1CcElxcDllck50WXUiLCJtaW5pbmdPdXRwdXRPcHRpb24iOiJhdXRvbG9jayIsIm1hcmdpbkFjdElkIjoibWFycndnV1pqTEdKRHA4MUVDMzlKaFNPMFZTdzBuckUiLCJtYXJnaW5UZXJtc0FjY2VwdGVkIjp0cnVlLCJsb2dpblN1Y2Nlc3NmdWwiOnRydWUsIm1hcmdpblJpc2tUZXJtc0FjY2VwdGVkIjpmYWxzZX0.KGfnppmQqaehPDZCOHHx6DGASS7ofw3AKcgnLdV0E9M;
 * Expires=Tue, 12 Mar 2019 16:04:01 GMT;
 * Path=/;
 * Secure; HttpOnly
 * strict-transport-security: max-age=31536000; includeSubDomains
 * x-frame-operations: SAMEORIGIN
 *
 */

public class CookieInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        okhttp3.Response resp = chain.proceed(chain.request());
        List<String> cookies = resp.headers("Set-Cookie");
        String cookieStr = "";
        if (cookies != null && cookies.size() > 0) {
            for (String header : cookies ){
                if (header.contains("authtoken")) {
                    cookieStr += header;
                }
            }
        }



        if(!TextUtils.isEmpty(cookieStr)){
//            UserSingle._getInstance().saveUserCookieId(cookieStr);
        }
        return resp;

    }
}
