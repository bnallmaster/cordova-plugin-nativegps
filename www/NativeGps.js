var exec = require('cordova/exec');

exports.getLocation = function (success, error) {
    exec(success, error, 'NativeGps', 'getLocation', "");
};