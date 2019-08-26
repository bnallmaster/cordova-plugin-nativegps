# NativeGps

A Cordova plugin designed to easily get the GPS coordinates of a users device from the native mobile environments rather than Google Maps Web API.

Command to add plugin with custom description

ionic cordova plugin add https://github.com/bnallmaster/cordova-plugin-nativegps.git --variable LOCATION_USAGE_DESCRIPTION="The app needs access to get the user's current location to show on the map"


Usage with Ionic 4 

declare var cordova;

getLocation(){

const nativegps = cordova.plugins.NativeGps;

nativegps.getLocation((val) => {
var location = val;
alert(JSON.stringify(location));
},
(err) => {
alert(JSON.stringify(err));
});

}

Data Response (JSON) - success will be false and provide a description in the message - response will be same format for iOS and Android

{success : true, message: {lat: 41.50309848, lng: -95.86908869}}

Permissions 

Plugin handles getting location permissions for you!
