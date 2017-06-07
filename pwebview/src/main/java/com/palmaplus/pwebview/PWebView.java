package com.palmaplus.pwebview;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeUtil;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jian.feng on 2017/4/10.
 */

public class PWebView extends FrameLayout implements BeaconConsumer, RangeNotifier {

    public static final String PWV_INIT = "PWebView_init";
    public static final String PWV_REGISTERREGION = "PWebView_registerRegion";
    public static final String PWV_UNREGISTERREGION = "PWebView_unRegisterRegion";
    public static final String PWV_DESTROY = "PWebView_destroy";
    public static final String PWV_ONSCANBEACONS = "PWebView_onScanBeacons";
    public static final String PWV_CHECKSTATUS = "PWebView_checkStatus";
    public static final String PWV_URLCHANGED = "PWebView_urlChanged";
    public static final String MAJOR = "major";
    public static final String UUID = "uuid";
    public static final String MINOR = "minor";
    public static final String RSSI = "rssi";
    public static final String DISTANCE = "distance";
    public static final String POWER = "power";
    public static final String BRIDGE_PATH = "bridge.js";

    private BeaconManager beaconManager;
    private List<Region> regions = new ArrayList<>();
    private BridgeWebView webView;
    private String identity = null;

    private WebViewClient webViewClient;

    private static final String TAG = "PWebView";

    public PWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public String getUrl() {
        return webView.getOriginalUrl();
    }

    public PWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PWebView(Context context) {
        super(context);
        init();
    }

    protected void init() {
        webView = new BridgeWebView(this.getContext());
        webView.setVerticalFadingEdgeEnabled(false);
        webView.setOverScrollMode(OVER_SCROLL_NEVER);
        initWebView();
        webView.setWebViewClient(new BridgeWebViewClient(webView) {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                BridgeUtil.webViewLoadLocalJs(webView, BRIDGE_PATH);
            }
        });
        this.addView(webView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        beaconManager = BeaconManager.getInstanceForApplication(this.getContext().getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setForegroundScanPeriod(2000);
        beaconManager.bind(this);

        webView.setDefaultHandler(new DefaultHandler());
        webView.setWebChromeClient(new WebChromeClient());
        webView.registerHandler(PWV_INIT, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject options = new JSONObject(data);
                    identity = options.optString(UUID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        webView.registerHandler(PWV_CHECKSTATUS, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject result = new JSONObject();
                    result.put("bluetoothStatus", beaconManager.checkAvailability());
                    function.onCallBack(result.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        webView.registerHandler(PWV_URLCHANGED, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    Log.e(TAG, "PWV_URLCHANGED: " + data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        webView.registerHandler(PWV_REGISTERREGION, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject options = new JSONObject(data);
                    String uuid = options.optString(UUID);
                    int major = options.optInt(MAJOR, -1);
                    int minor = options.optInt(MINOR, -1);
                    Region r = new Region(
                            "",
                            Identifier.parse(uuid),
                            major == -1 ? null : Identifier.fromInt(major),
                            minor == -1 ? null : Identifier.fromInt(minor)
                    );
                    regions.add(r);
                    if (beaconManager.isBound(PWebView.this)) {
                        beaconManager.startRangingBeaconsInRegion(r);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        webView.registerHandler(PWV_UNREGISTERREGION, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    if (beaconManager.isBound(PWebView.this)) {
                        JSONObject options = new JSONObject(data);
                        String uuid = options.optString(UUID);
                        int major = options.optInt(MAJOR, -1);
                        int minor = options.optInt(MINOR, -1);
                        Region r = new Region(
                                "",
                                Identifier.parse(uuid),
                                major == -1 ? null : Identifier.fromInt(major),
                                minor == -1 ? null : Identifier.fromInt(minor)
                        );
                        regions.remove(r);
                        beaconManager.stopRangingBeaconsInRegion(r);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        webView.registerHandler(PWV_DESTROY, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                exitBeaconScan();
            }
        });
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        // webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(false);//support zoom/
        webSettings.setUseWideViewPort(true);// 适应手机
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDomStorageEnabled(true);
    }

    private void exitBeaconScan(){
        try {
            if (beaconManager.isBound(PWebView.this)) {
                for (Region r : regions) {
                    beaconManager.stopRangingBeaconsInRegion(r);
                }
                beaconManager.removeRangeNotifier(PWebView.this);
                beaconManager.unbind(PWebView.this);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setWebChromeClient(WebChromeClient client) {
        webView.setWebChromeClient(client);
    }

    public void setWebViewClient(WebViewClient client) {
        this.webViewClient = client;
    }

    public void loadURL(String url) {
        webView.loadUrl(url);
    }

    public void debug(boolean enable) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(enable);
        } else {
            throw new UnsupportedOperationException("该版本不支持该特性");
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.addRangeNotifier(this);
            for (Region r : this.regions) {
                beaconManager.startRangingBeaconsInRegion(r);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return this.getContext().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        this.getContext().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return this.getContext().bindService(intent, serviceConnection, i);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        if (collection.size() == 0) {
            return;
        }
        try {
            final JSONObject result = new JSONObject();
            JSONArray beacons = new JSONArray();
            for (Beacon beacon : collection) {
                JSONObject b = new JSONObject();
                b.put(UUID, beacon.getId1().toString());
                b.put(MAJOR, beacon.getId2().toInt());
                b.put(MINOR, beacon.getId3().toInt());
                b.put(RSSI, beacon.getRssi());
                b.put(DISTANCE, beacon.getDistance());
                b.put(POWER, beacon.getTxPower());
                beacons.put(b);
            }
            result.put("beacons", beacons);

            JSONObject r = new JSONObject();
            r.put(UUID, region.getId1().toString());
            if (null != region.getId1()) {
                r.put(MAJOR, region.getId2().toInt());
            }
            if (null != region.getId3()) {
                r.put(MINOR, region.getId3().toInt());
            }
            result.put("region", r);
            result.put(UUID, identity);
            post(new Runnable() {
                @Override
                public void run() {
                    webView.callHandler(PWV_ONSCANBEACONS, result.toString(), new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {

                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void stopLoading() {
        webView.stopLoading();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }

    public void destroy() {
        exitBeaconScan();
        webView.destroy();
    }

}
