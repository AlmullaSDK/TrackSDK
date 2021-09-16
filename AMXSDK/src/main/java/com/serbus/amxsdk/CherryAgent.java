package com.serbus.amxsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CherryAgent {

    static private  String apiKey;
    static private String deviceId;
    static private final String s_platform = "ANDROID";
    static private String s_version = "";
    static private String s_osVersion;
    static private SharedPreferences mPrefs;
    static private SharedPreferences.Editor editor;
    static private String s_identity;
    static private String s_consumerKey;
    static private String s_origin;
    static private TopicListener topicListener;
    private Context context;

     public CherryAgent initSDK(Context context, String consumerKey,TopicListener topicListener){
        CherryAgent.topicListener = topicListener;
        mPrefs = context.getSharedPreferences("SDKPrefs", Context.MODE_PRIVATE);
        PackageInfo pInfo = null;
        this.context = context;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            s_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        s_osVersion = Build.VERSION.RELEASE;
        s_identity = mPrefs.getString("identity", "");
        s_origin = context.getPackageName();
        s_consumerKey = consumerKey;

        setStringSharePrefs(context,"version",s_version);
        setStringSharePrefs(context,"osVersion",s_osVersion);
        setStringSharePrefs(context,"identity",s_identity);
        setStringSharePrefs(context,"origin",s_origin);
        setStringSharePrefs(context,"consumerKey",s_consumerKey);


        makeHeartbeatCall(context);
        return this;
    }

    public void setDomain(String domain){
        setStringSharePrefs(context,"domain",domain);
    }

    static public void setIdentity(Context context,String identity){
        mPrefs = context.getSharedPreferences("SDKPrefs", Context.MODE_PRIVATE);
        editor = mPrefs.edit();
        editor.putString("identity", identity);
        editor.apply();
    }

    static public void handleNotification(Map<String,String> data){

    }

    static public void handleEvent(Context context,Event event){
        setEventCall(context,event);
    }


    static private void makeHeartbeatCall(final Context context){
        HashMap<String, Object> req_data = new HashMap<String, Object>();;
//        req_data.put("appId",getStringSharePrefs(context,"appId"));
//        req_data.put("appKey",getStringSharePrefs(context,"appKey"));
//        req_data.put("appVersion",getStringSharePrefs(context,"version"));


        HashMap<String , Object> uuidMap = new HashMap<>();
        uuidMap.put("version", getIntSharePrefs(context, "uuid_version"));
        uuidMap.put("value", getStringSharePrefs(context, "uuid_value"));
        uuidMap.put("updatedStamp", getIntSharePrefs(context, "uuid_updatedStamp"));



        HashMap<String , Object> clientProperties = new HashMap<>();
        clientProperties.put("appVersion", getStringSharePrefs(context,"version"));
        clientProperties.put("osVersion", getStringSharePrefs(context,"osVersion"));
        clientProperties.put("appType", getStringSharePrefs(context,"ANDROID"));
        clientProperties.put("devicePlatform", getStringSharePrefs(context,"ANDROID"));
        clientProperties.put("channel", getStringSharePrefs(context,"ANDROID"));
        clientProperties.put("deviceType", getStringSharePrefs(context,"MOBILE"));

        req_data.put("clientFp", Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        if(!getStringSharePrefs(context,"identity").equals("")){
            req_data.put("identity",getStringSharePrefs(context,"identity"));
        }
        if(!getStringSharePrefs(context,"clientId").equals("")){
            req_data.put("clientId",getStringSharePrefs(context,"clientId"));
        }
        if(!getStringSharePrefs(context,"signature").equals("")){
            req_data.put("signature",getStringSharePrefs(context,"signature"));
        }
        req_data.put("uuId",uuidMap);
        req_data.put("clientProperties",clientProperties);
        String domain = "https://apib-kwt.almullaexchange.com/xms";
        if(!getStringSharePrefs(context,"domain").equals("")){
            domain = getStringSharePrefs(context,"domain");
        }

        Log.e("REQUEST", new JSONObject(req_data).toString());



        Map<String,String> headerMap = new HashMap<String,String>();
        headerMap.put("consumerKey",getStringSharePrefs(context,"consumerKey"));

        new HTTPRequest().makeCall(context,domain+"/api/v1/client/heartbeat", new JSONObject(req_data),headerMap, new HTTPCallback() {
            @Override
            public void processFinish(String response) {
                Log.e("Response",response);

                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray resultArray = responseObject.getJSONArray("results");
                    JSONObject resultObject = resultArray.getJSONObject(0);
                    String clientId = resultObject.optString("clientId");
                    setStringSharePrefs(context,"clientId",clientId);
                    JSONObject uuIdObject = resultObject.getJSONObject("uuId");
                    String uuidValue = uuIdObject.optString("value");
                    int uuidVersion = uuIdObject.optInt("version");
                    int uuidUpdatedStamp = uuIdObject.optInt("updatedStamp");
                    String signature = resultObject.getString("signature");
                    setStringSharePrefs(context,"uuid_value",uuidValue);
                    setIntSharePrefs(context,"uuid_version",uuidVersion);
                    setIntSharePrefs(context,"uuid_updatedStamp",uuidUpdatedStamp);
                    setStringSharePrefs(context,"signature",signature);

                    if(topicListener != null){
                        topicListener.onTopicReceived("topic_"+s_consumerKey);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void processFailed(String error) {
                Log.e("Response Failed", error);
            }
        });
    }




    static private void setEventCall(Context context,Event event){
        HashMap<String, Object> req_data = new HashMap<String, Object>();
        HashMap<String, Object> eventData = new HashMap<String, Object>();
        eventData.put("data",event.data);
        eventData.put("attr",event.attr);
        req_data.put("eventName",event.eventName);
        req_data.put("eventData",eventData);

        Map<String,String> headerMap = new HashMap<String,String>();
        if(!getStringSharePrefs(context,"clientId").equals("")){
            headerMap.put("clientId",getStringSharePrefs(context,"clientId"));
        }
        headerMap.put("consumerKey",getStringSharePrefs(context,"consumerKey"));
        String domain = "https://apib-kwt.almullaexchange.com/xms";
        if(!getStringSharePrefs(context,"domain").equals("")){
            domain = getStringSharePrefs(context,"domain");
        }
        new HTTPRequest().makeCall(context,domain+"/api/v1/client/track/event", new JSONObject(req_data),headerMap, new HTTPCallback() {
            @Override
            public void processFinish(String response) {
                Log.e("Response",response);
            }
            @Override
            public void processFailed(String error) {
                Log.e("Response Failed", error);
            }
        });
    }

    static private void setStringSharePrefs(Context context,String name, String value){
        mPrefs = context.getSharedPreferences("SDKPrefs", Context.MODE_PRIVATE);
        editor = mPrefs.edit();
        editor.putString(name, value);
        editor.apply();
    }

    static private void setIntSharePrefs(Context context,String name, int value){
        mPrefs = context.getSharedPreferences("SDKPrefs", Context.MODE_PRIVATE);
        editor = mPrefs.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    static private String getStringSharePrefs(Context context,String name){
        mPrefs = context.getSharedPreferences("SDKPrefs", Context.MODE_PRIVATE);
        return mPrefs.getString(name,"");
    }

    static private int getIntSharePrefs(Context context,String name){
        mPrefs = context.getSharedPreferences("SDKPrefs", Context.MODE_PRIVATE);
        return mPrefs.getInt(name,0);
    }

}




