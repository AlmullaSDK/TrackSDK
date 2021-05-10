package com.serbus.amxsdk;

import java.io.Serializable;

public class Users implements Serializable {

    int launches;
    boolean isActive;


    public Users(int launches, boolean isActive) {
        this.launches = launches;
        this.isActive = isActive;
    }


    public int getLaunches() {
        return launches;
    }

    public void setLaunches(int launches) {
        this.launches = launches;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    @Override
    public String toString() {
        return "Users{" +
                "launches=" + launches +
                ", isActive=" + isActive +
                '}';
    }
}
