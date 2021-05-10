package com.serbus.amxsdk;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AMX {

    private Context context;
    private  String apiKey;
    public AMX(Context context){
        this.context = context;
    }

    public void setKey(String apiKey){
        this.apiKey = apiKey;
        final String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(android_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    HashMap<String,Object> users = (HashMap<String,Object>) task.getResult().getValue();
                    if(users != null) {
                        Log.e("USERS", users.toString());
                        Long launches = (Long)users.get("launches");
                        Boolean isActive = (Boolean) users.get("isActive");
                        mDatabase.child("users").child(android_id).child("launches").setValue(launches+1);
                        mDatabase.child("users").child(android_id).child("isActive").setValue(isActive);
                    }
                    if (task.getResult().getValue() == null){
                        mDatabase.child("users").child(android_id).child("launches").setValue(1);
                        mDatabase.child("users").child(android_id).child("isActive").setValue(false);
                    }else{

                    }
                }
            }
        });
    }

    public void handleNotification(Map<String,String> data){
        final String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(android_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    HashMap<String,Object> users = (HashMap<String,Object>) task.getResult().getValue();
                    if(users != null) {
                        Log.e("USERS", users.toString());
                        Long launches = (Long)users.get("launches");
                        Boolean isActive = (Boolean) users.get("isActive");
                        mDatabase.child("users").child(android_id).child("launches").setValue(launches);
                        mDatabase.child("users").child(android_id).child("isActive").setValue(true);
                    }
                }
            }
        });
    }



}


