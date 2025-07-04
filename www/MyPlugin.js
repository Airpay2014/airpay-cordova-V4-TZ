var exec = require('cordova/exec');

exports.greet = function(name, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MyPlugin", "greet", [name]);
};

