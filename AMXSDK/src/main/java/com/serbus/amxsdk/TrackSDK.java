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

public class TrackSDK {

    static private  String apiKey;
    static private String deviceId;
    static private final String s_platform = "ANDROID";
    static private String s_version = "";
    static private String s_osVersion;
    static private SharedPreferences mPrefs;
    static private SharedPreferences.Editor editor;
    static private String s_identity;
    static private String s_appID;
    static private String s_appKey;
    static private String s_origin;
    static public TopicListener topicListener;

    static public void initSDK(Context context, String key, String appId, String appKey,TopicListener topicListener){
        TrackSDK.topicListener = topicListener;
        mPrefs = context.getSharedPreferences("SDKPrefs", Context.MODE_PRIVATE);
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            s_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        s_osVersion = Build.VERSION.RELEASE;
        s_identity = mPrefs.getString("identity", "");
        s_origin = context.getPackageName();
        s_appID = appId;
        s_appKey = appKey;

        setStringSharePrefs(context,"version",s_version);
        setStringSharePrefs(context,"osVersion",s_osVersion);
        setStringSharePrefs(context,"identity",s_identity);
        setStringSharePrefs(context,"origin",s_origin);
        setStringSharePrefs(context,"appId",s_appID);
        setStringSharePrefs(context,"appKey",s_appKey);

        makeInitCall(context);

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


    static private void makeInitCall(final Context context){
        HashMap<String, Object> req_data = new HashMap<String, Object>();;
        req_data.put("appId",getStringSharePrefs(context,"appId"));
        req_data.put("appKey",getStringSharePrefs(context,"appKey"));
        req_data.put("appVersion",getStringSharePrefs(context,"version"));
        req_data.put("clientFp", Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID));


        if(!getStringSharePrefs(context,"identity").equals("")){
            req_data.put("identity",getStringSharePrefs(context,"identity"));
        }

        if(!getStringSharePrefs(context,"clientId").equals("")){
            req_data.put("clientId",getStringSharePrefs(context,"clientId"));
        }

        req_data.put("osVersion",getStringSharePrefs(context,"osVersion"));

        HashMap<String , Object> uuidMap = new HashMap<>();
        uuidMap.put("version", getIntSharePrefs(context, "uuid_version"));
        uuidMap.put("value", getStringSharePrefs(context, "uuid_value"));
        uuidMap.put("updatedStamp", getIntSharePrefs(context, "uuid_updatedStamp"));
        req_data.put("uuId",uuidMap);


        HashMap<String,String> userAgent = new HashMap<>();
        userAgent.put("appType" , "ANDROID");
        userAgent.put("channel" , "ANDROID");
        userAgent.put("devicePlatform" , "ANDROID");
        userAgent.put("deviceType" , "MOBILE");

        req_data.put("userAgent",userAgent);

        Log.e("REQUEST", new JSONObject(req_data).toString());

        new HTTPRequest().makeCall(context,"https://apib-kwt.almullaexchange.com/xms/api/v1/data/device-init", new JSONObject(req_data), new HTTPCallback() {
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
                    setStringSharePrefs(context,"uuid_value",uuidValue);
                    setIntSharePrefs(context,"uuid_version",uuidVersion);
                    setIntSharePrefs(context,"uuid_updatedStamp",uuidUpdatedStamp);

                    if(topicListener != null){
                        topicListener.onTopicReceived("topic");
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
        List<HashMap<String ,String>> links = new ArrayList<>();
        req_data.put("data",event.payload);
        req_data.put("eventName",event.eventName);

        String clientFp = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if(clientFp != null){
            HashMap<String, String> link = new HashMap<>();
            link.put("linkName","clientFp");
            link.put("linkType","DEVICE");
            link.put("linkValue",clientFp);
            links.add(link);
        }


        if(!getStringSharePrefs(context,"identity").equals("")){
            HashMap<String, String> link = new HashMap<>();
            link.put("linkName","identity");
            link.put("linkType","CUSTOMER");
            link.put("linkValue",getStringSharePrefs(context,"identity"));
            links.add(link);
        }

        req_data.put("links",links);

        new HTTPRequest().makeCall(context,"https://apib-kwt.almullaexchange.com/xms/api/v1/event/push", new JSONObject(req_data), new HTTPCallback() {
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




