(function() {
    // for Android
    var pwb = {};

    var pwbObjs = {};
    var bridge;

    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    var uuid = new Array( 36 );
    var rnd = 0, r;

    function generateUUID() {
        for ( var i = 0; i < 36; i ++ ) {
            if ( i === 8 || i === 13 || i === 18 || i === 23 ) {
                uuid[ i ] = '-';
            } else if ( i === 14 ) {
                uuid[ i ] = '4';
            } else {
                if ( rnd <= 0x02 ) rnd = 0x2000000 + ( Math.random() * 0x1000000 ) | 0;
                r = rnd & 0xf;
                rnd = rnd >> 4;
                uuid[ i ] = chars[ ( i === 19 ) ? ( r & 0x3 ) | 0x8 : r ];
            }
        }
        return uuid.join( '' );
    };


    pwb.ready = function(callback) {
        function init() {
            if (!bridge) {
                bridge = window.WebViewJavascriptBridge;
                bridge.init(function(message, responseCallback) { });
                bridge.registerHandler("PWebView_onScanBeacons", function(data, responseCallback) {
                    var result = JSON.parse(data);
                    var pwebView = pwbObjs[result.uuid];
                    if (pwebView) {
                        if (result.beacons && result.region) {
                            for (var index = 0; index < pwebView._callbacks.length; index++) {
                                var callback = pwebView._callbacks[index];
                                callback(result.beacons, result.region);
                            }
                        }
                    }
                });
            }
            callback();
        }
        if (window.WebViewJavascriptBridge) {
            init();
        } else {
            document.addEventListener(
                'WebViewJavascriptBridgeReady'
                , init,
                false
            );
        }
    }

    pwb.init = function(options) {
        if (bridge) {
            function PWebView(op) {
                this._op = Object.assign({
                    uuid: generateUUID(),
                }, op);
                pwbObjs[this._op.uuid] = this;
                this._callbacks = [];
                var self = this;
                bridge.callHandler(
                    'PWebView_init',
                    this._op,
                    function() {}
                );
            };
            PWebView.prototype.registerRegion = function(region) {
                var r = region || {};
                bridge.callHandler(
                    'PWebView_registerRegion',
                    r,
                    function() {}
                )

            };
            PWebView.prototype.unRegisterRegion = function(region) {
                var r = region || {};
                bridge.callHandler(
                    'PWebView_unRegisterRegion',
                    r,
                    function() {}
                )
            };
            PWebView.prototype.onScanBeacons = function(callback) {
                this._callbacks[this._callbacks.length] = callback;
            };
            PWebView.prototype.removeScanBeacons = function(callback) {
                var indexOf = this._callbacks.indexOf(callback)
                if (indexOf !== -1) {
                    this._callbacks.splice(indexOf, 1);
                }
            }
            PWebView.prototype.destroy = function() {
                delete pwbObjs[this.uuid];
                bridge.callHandler(
                    'PWebView_destroy',
                    {},
                    function() {}
                )
            };
            return new PWebView(options);
        } else {
            throw new Error('无法使用!');
        }
    };

    if (typeof module === 'object' && typeof module.exports === 'object') {
        module.exports = pwb;
    } else if (typeof define === 'function' && define.amd) {
        define(pwb);
    } else {
        window.pwb = pwb;
    }
})();