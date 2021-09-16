package com.serbus.amxsdk;

import java.util.Map;

public class Event{
    public String eventName;
    public Map<String,Object> attr;
    public Map<String,Object> data;


    public Event(String eventName, Map<String, Object> attr,Map<String, Object> data) {
        this.eventName = eventName;
        this.attr = attr;
        this.data = data;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Map<String, Object> getAttr() {
        return attr;
    }

    public void setAttr(Map<String, Object> attr) {
        this.attr = attr;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", attr=" + attr +
                ", data=" + data +
                '}';
    }
}
