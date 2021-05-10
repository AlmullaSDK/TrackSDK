package com.serbus.amxsdk;

import java.util.Map;

public class Event{
    public String eventName;
    public Map<String,Object> payload;

    public Event(String eventName, Map<String, Object> payload) {
        this.eventName = eventName;
        this.payload = payload;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", payload=" + payload +
                '}';
    }
}
