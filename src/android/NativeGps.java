package com.appsky.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeGps extends CordovaPlugin {

    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 101;
    private Location mGetedLocation;
    private double currentLat, currentLng;
    CordovaInterface cordova;
    private static final String TAG = "NativeGPSPlugin";
    public static final String LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    CallbackContext locationCallbackContext;

    @Override
    public void initialize(CordovaInterface cor, CordovaWebView webView) {
        super.initialize(cor, webView);
        this.cordova = cor;
        // your init code here
        this.mFusedLocationClient = getFusedLocationProviderClient(this.cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getLocation")) {
           // String message = args.getString(0);
            this.getLocation(callbackContext);
            return true;
        }
        return false;
    }

    private void getLocation(CallbackContext callbackContext) {
        
        this.locationCallbackContext = callbackContext;
        if(!cordova.hasPermission(LOCATION))
        {
            cordova.requestPermission(this, 0, LOCATION);
        }
        else{
            this.getGPS();
        }
       
    }

    private void getGPS(){

        if(cordova.hasPermission(LOCATION)){
           
            this.mFusedLocationClient.getLastLocation()
            .addOnCompleteListener(this.cordova.getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mGetedLocation = task.getResult();
                        currentLat = mGetedLocation.getLatitude();
                        currentLng = mGetedLocation.getLongitude();
                       // Log.d(TAG, "Latitude : " + currentLat + "\nLongitude : " + currentLng);
                        String latLng = "{lat: " + currentLat + ", lng: " + currentLng + "}";
                        sendResponse("true", latLng, locationCallbackContext);

                    } else {
                        
                        String message = "Error getting location.";
                        sendResponse("false", message , locationCallbackContext);
                    }
                }
            });
        }
        else{
            String message = "Permission request failed on GPS call.";
            this.sendResponse("false", message, this.locationCallbackContext);
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
    {
        Log.d(TAG, "updated permissions");
        for(int r:grantResults)
         {
           if(r == PackageManager.PERMISSION_DENIED)
           {    String message = "Permission request failed.";
                this.sendResponse("false", message, this.locationCallbackContext);
           }
        }
        if(cordova.hasPermission(LOCATION)){
             this.getGPS();
        }
    }

    private void sendResponse(String success, String message, CallbackContext callback){
       
        try {
           
            if(success.equals("true") == true){
                String jsonString = "{success: " + success + ", message: " +  message + "}";
                JSONObject json = new JSONObject(jsonString);
                callback.success(json);
            }
            else{
                String jsonString = "{success: " + success + ", message: '" +  message.toString() + "'}";
                JSONObject json = new JSONObject(jsonString);
                callback.error(json);
            }
          
        } catch (Throwable t) {

            callback.error("{success: false, message: Error parsing JSON response.}" + t);
           
        }

    }
}
