package com.curativecare;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


public class SendSMS {

    String sms = IPaddress.ip+"sendSMS.php";

    public boolean sendmsg() {


        RequestParams params = new RequestParams();
        params.put("msg", "Verification Pin : " + UserData.key);
        params.put("mobile", UserData.mobile);
        params.put("email",UserData.email);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(sms, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                IPaddress.result = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                IPaddress.result = false;
            }
        });
        return IPaddress.result;
    }
}
