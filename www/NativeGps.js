var exec = require('cordova/exec');

exports.getLastLocation = function (arg0, success, error) {
    exec(success, error, 'NativeGps', 'getLastLocation', [arg0]);
};

exports.getLocationPermissions = function (arg0, success, error) {
    exec(success, error, 'NativeGps', 'getLocationPermissions', [arg0]);
};
