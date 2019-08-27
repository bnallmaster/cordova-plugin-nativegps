# cordova-plugin-nativegps

This plugin was designed to get the current GPS location from IOS and Android devices using updated native code. The key thing is that is manages the permissions flow for iOS and Android automatically. Call one method and get your users GPS Latitude and Longitude! (Tested with Ionic 4). It should work in most Cordova enviroments. 

## Add the plugin to your Ionic 4 project

```ionic cli
ionic cordova plugin add  https://github.com/bnallmaster/cordova-plugin-nativegps.git --variable LOCATION_USAGE_DESCRIPTION="The app needs access to get the user's current location to show on the map"
```
You can add your custom **LOCATION_USAGE_DESCRIPTION** for iOS apps when you install the plugin, it will automatically fill in Location Permissions for the .plist using the required *NSLocationWhenInUseUsageDescription* and *NSLocationAlwaysAndWhenInUseUsageDescription*. 

## Usage in Ionic 4 - Angular TypeScript

```typescript
import { Component } from '@angular/core';

/**Declare cordova here to access the plugins**/
declare var cordova;

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {
  constructor() {
  }
  
  /**Basic method implementation of triggering permissions and getting users GPS location*/
  getLocation(){
    const nativegps = cordova.plugins.NativeGps;
    nativegps.getLocation((location) => {
      alert(JSON.stringify(location));
    },
    (err) => {
      alert(JSON.stringify(err));
    });
  }
}
```

## Data Response (JSON) 

```json
{success : true,
message: 
	{lat: 41.50309848, 
	lng: -95.86908869}
}
```

- **success** will be false if there is an error and provide a description in the message 
- The response will be same format for iOS and Android as JSON object

## Permissions 

Plugin handles getting location permissions for you! It uses callbacks or delegates for monitoring the authorization / permissions statuses. It will look for location only after it gets the proper permissions accepted by the user.