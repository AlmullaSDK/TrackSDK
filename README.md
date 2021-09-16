# CherryAgent #

## Installation ##

Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 ```
 
 Add the dependency
 
 ```
 dependencies {
	        implementation 'com.github.AlmullaSDK:TrackSDK:1.0.1'
	}
 ```

## Initialization ##

Implement Topic listener to you application class 

`public class AppController extends Application implements TopicListener`

In 'onCreate' method of your application class add the following code 

`new CherryAgent().initSDK(getApplicationContext(),"CONSUMER_KEY",this).setDomain("domain");`

## Track Event ##

For tracking Event use the 'Event' class, Event class has 3 inputs

Parameters | Registration Required | Value Type
| :--- | ---: | :---:
eventName  | YES | String
attr  | YES | HashMap<String,Object>
data  | NO | HashMap<String,Object>

Example Code

```             
                Map<String,Object> payload = new HashMap<>();                
                payload.put("test_string","abcd");                
                payload.put("test_integer", 1234);
                Map<String,Object> data = new HashMap<>();
                Map<String,Object> subdata = new HashMap<>();
                subdata.put("sub_data_one","new");
                subdata.put("sub_data_two",123);
                subdata.put("sub_data_three",true);
                data.put("data_one", false);
                data.put("data_two",subdata);
                Event event = new Event("EVENT_TEST",payload,data);
                CherryAgent.handleEvent(MainActivity.this,event);
```
