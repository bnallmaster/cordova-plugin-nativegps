/********* NativeGps.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <CoreLocation/CoreLocation.h>

@interface NativeGps : CDVPlugin <CLLocationManagerDelegate>{
  // Member variables go here.
}
@property (nonatomic,strong) CLLocationManager *locationManager;
@property (strong, nonatomic) NSString* locationCommandId;

- (void)getLocation:(CDVInvokedUrlCommand*)command;

@end

@implementation NativeGps 

- (void)getLocation:(CDVInvokedUrlCommand*)command
{
    self.locationManager = [[CLLocationManager alloc] init]; // initializing locationManager
    self.locationManager.delegate = self; // we set the delegate of locationManager to self. 
    self.locationManager.desiredAccuracy = kCLLocationAccuracyBest; // setting the accuracy
    self.locationCommandId = command.callbackId;
    [self.locationManager requestWhenInUseAuthorization];
    [self.locationManager startUpdatingLocation];  //requesting location updates

  //  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{

    NSLog(@"Error: %@",error.description);

    CDVPluginResult* pluginResult = nil;

    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    [dict setObject: error.description forKey:@"message"];
    [dict setObject:@"false" forKey:@"success"];

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary: dict];

    [self.commandDelegate sendPluginResult:pluginResult callbackId: self.locationCommandId];
}
-(void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    CLLocation *crnLoc = [locations lastObject];
    NSLog([NSString stringWithFormat:@"%.8f",crnLoc.coordinate.latitude]);
    NSLog([NSString stringWithFormat:@"%.8f",crnLoc.coordinate.longitude]);
    
    CDVPluginResult* pluginResult = nil;

    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    NSMutableDictionary* message = [[NSMutableDictionary alloc] init];
    [message setObject:[NSString stringWithFormat:@"%.8f", crnLoc.coordinate.latitude] forKey:@"lat"];
    [message setObject:[NSString stringWithFormat:@"%.8f", crnLoc.coordinate.longitude] forKey:@"lng"];
    [dict setObject:message forKey:@"message"];
    [dict setObject:@"true" forKey:@"success"];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary: dict];
  
    [self.locationManager stopUpdatingLocation];
    [self.commandDelegate sendPluginResult:pluginResult callbackId: self.locationCommandId];
    //latitude.text = [NSString stringWithFormat:@"%.8f",crnLoc.coordinate.latitude];
    //longitude.text = [NSString stringWithFormat:@"%.8f",crnLoc.coordinate.longitude];
    //altitude.text = [NSString stringWithFormat:@"%.0f m",crnLoc.altitude];
    //speed.text = [NSString stringWithFormat:@"%.1f m/s", crnLoc.speed];
}

- (void)locationManager:(CLLocationManager*)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {

    CDVPluginResult* pluginResult = nil;

    switch (status) {
        case kCLAuthorizationStatusNotDetermined: {
           // DDLogVerbose(@"User still thinking granting location access!");
            [self.locationManager startUpdatingLocation]; // this will access location automatically if user granted access manually. and will not show apple's request alert twice. (Tested)
        } break;
        case kCLAuthorizationStatusDenied: {
          
            [self.locationManager stopUpdatingLocation];
           // [loadingView stopLoading];

            NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
            [dict setObject: @"To re-enable, please go to Settings and turn on Location Service for this app." forKey:@"message"];
            [dict setObject:@"false" forKey:@"success"];

            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary: dict];

            [self.commandDelegate sendPluginResult:pluginResult callbackId: self.locationCommandId];

        } break;
        case kCLAuthorizationStatusAuthorizedWhenInUse:
        case kCLAuthorizationStatusAuthorizedAlways: {

            [self.locationManager startUpdatingLocation]; //Will update location immediately
       
        } break;
        default:
            break;
    }
}


@end
