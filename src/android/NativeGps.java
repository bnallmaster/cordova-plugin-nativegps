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
    CallbackContext permissionsCallbackContext;

    @Override
    public void initialize(CordovaInterface cor, CordovaWebView webView) {
        super.initialize(cor, webView);
        this.cordova = cor;
        // your init code here
        this.mFusedLocationClient = getFusedLocationProviderClient(this.cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getLastLocation")) {
            String message = args.getString(0);
            this.getLastLocation(message, callbackContext);
            return true;
        }
        else if (action.equals("getLocationPermissions")) {
            String message = args.getString(0);
            this.getLocationPermissions(message, callbackContext);
            return true;
        }
        return false;
    }

    private void getLastLocation(String message, CallbackContext callbackContext) {

        if(cordova.hasPermission(LOCATION)){
            mFusedLocationClient.getLastLocation()
            .addOnCompleteListener(this.cordova.getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mGetedLocation = task.getResult();
                        currentLat = mGetedLocation.getLatitude();
                        currentLng = mGetedLocation.getLongitude();
                        Log.d(TAG, "Latitude : " + currentLat + "\nLongitude : " + currentLng);
                        //  locationTv.setText("Latitude : " + currentLat + "\nLongitude : " + currentLng);
                        callbackContext.success("{status: true, message: {lat: " + currentLat + ",lng: " + currentLng + "}}");
                    } else {
                        Log.e(TAG, "no location detected");
                        Log.w(TAG, "getLastLocation:exception", task.getException());
                        callbackContext.error("{status: false, message: " +  task.getException() + "}");
                    }
                }
            });
        }
        else{
            callbackContext.error("{status: false, message: 'Missing location permissions'}");
        }
    }

    private void getLocationPermissions(String message, CallbackContext callbackContext) {
        
        this.permissionsCallbackContext = callbackContext;

        if(!cordova.hasPermission(LOCATION))
        {
            this.cordova.requestPermission(this, 0, LOCATION);
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
    {
        Log.d(TAG, "updated permissions");
        for(int r:grantResults)
         {
           if(r == PackageManager.PERMISSION_DENIED)
           {
                this.permissionsCallbackContext.error("{status: false, message: 'Permission request failed'}");
           }
        }

       this.permissionsCallbackContext.success("{status: true, message: 'Permission request success'}");
    }
}
