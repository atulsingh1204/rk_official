package com.bpointer.rkofficial.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bpointer.rkofficial.R;

public class UpiPaymentGatewayActivity extends AppCompatActivity {



    WebView mWebView;
    Context context;
    String TAG = "UpiPaymentGatewayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_payment_gateway);

        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        context = this;
        mWebView = (WebView) findViewById(R.id.payment_webview);
        initWebView();


        String paymentUrl = getIntent().getStringExtra("paymentUrl");
//        String PAYMENT_URL = "upi://pay?pa=...";


        if(paymentUrl != null){

            if (paymentUrl.startsWith("upi:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(paymentUrl));
                startActivity(intent);
            }else{
                mWebView.loadUrl(paymentUrl);
            }
        }else{
            Toast.makeText(this, "Invalid Payment URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint({ "SetJavaScriptEnabled" })
    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        // Do not change Useragent otherwise it will not work. even if not working uncommit below
       //  mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.135 Mobile Safari/537.36");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new WebviewInterface(), "Interface");
    }

    public class WebviewInterface {
        @JavascriptInterface
        public void paymentResponse(String client_txn_id, String txn_id) {
            Log.e(TAG, client_txn_id);
            Log.e(TAG, txn_id);
            // this function is called when payment is done (success, scanning ,timeout or cancel by user).
            // You must call the check order status API in server and get update about payment.
            // 🚫 Do not Call UpiGateway API in Android App Directly.
           // Toast.makeText(context, "Order ID: "+client_txn_id+", Txn ID: "+txn_id, Toast.LENGTH_SHORT).show();
            Log.e(TAG,"client_txn_id -->"+client_txn_id);
            Log.e(TAG,"txn_id -->"+txn_id);
            Intent returnIntent = getIntent();
            returnIntent.putExtra("clientTxnId",client_txn_id);
            returnIntent.putExtra("txnId",txn_id);

            setResult(RESULT_OK,returnIntent);
            finish();
            // Close the Webview.
        }

        @JavascriptInterface
        public void errorResponse() {
            // this function is called when Transaction in Already Done or Any other Issue.
            Toast.makeText(context, "Transaction Error.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}