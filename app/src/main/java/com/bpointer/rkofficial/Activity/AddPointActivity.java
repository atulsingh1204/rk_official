package com.bpointer.rkofficial.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bpointer.rkofficial.Adapter.NotificationAdapter;
import com.bpointer.rkofficial.Api.Api;
import com.bpointer.rkofficial.Api.Authentication;
import com.bpointer.rkofficial.BuildConfig;
import com.bpointer.rkofficial.Common.CustomDialog;
import com.bpointer.rkofficial.Common.PreferenceManager;
import com.bpointer.rkofficial.Model.EasebuzzAccessTokenResponseModel;
import com.bpointer.rkofficial.Model.EasebuzzPaymentStatusSuccessFailureResponse;
import com.bpointer.rkofficial.Model.RequestBody;
import com.bpointer.rkofficial.Model.Response.AddFundResponseModel.AddFundResponseModel;
import com.bpointer.rkofficial.Model.Response.GetActiveUpiModel;
import com.bpointer.rkofficial.Model.Response.HomeResponseModel.HomeResponseModel;
import com.bpointer.rkofficial.Model.Response.NotificationResponseModel.NotificationResponseModel;
import com.bpointer.rkofficial.Model.upigateway.Data;
import com.bpointer.rkofficial.Model.upigateway.UpiGatewayOrderRequest;
import com.bpointer.rkofficial.Model.upigateway.UpiGatewayOrderResponse;
import com.bpointer.rkofficial.Model.upigateway.paymentstatus.UpiGatewayOrderPaymentStatus;
import com.bpointer.rkofficial.R;
import com.easebuzz.payment.kit.PWECheckoutActivity;
import com.gpfreetech.IndiUpi.IndiUpi;
import com.gpfreetech.IndiUpi.entity.TransactionResponse;
import com.gpfreetech.IndiUpi.listener.PaymentStatusListener;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bpointer.rkofficial.Common.AppConstant.ADMIN_WHATSAPP;
import static com.bpointer.rkofficial.Common.AppConstant.ID;
import static com.bpointer.rkofficial.Common.AppConstant.MOBILE;
import static com.bpointer.rkofficial.Common.AppConstant.NAME;
import static com.bpointer.rkofficial.Common.AppConstant.UPI_ID;
import static com.bpointer.rkofficial.Common.AppConstant.USER_ID;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class AddPointActivity extends AppCompatActivity implements View.OnClickListener,
        PaymentStatusListener, ModalBottomSheetDialog.Listener {

    EditText et_point;
    Button bt_submit;
    TextView tv_title, tv_whatsapp, tv_wallet;
    PreferenceManager preferenceManager;
    int userId, wallet_amount = 0;
    ImageView iv_back;
    CustomDialog customDialog;
    String finalAmount;
    String upi_id, display_name = "R K Group";
   // private TextView tv_response;

    private ActivityResultLauncher<Intent> pweActivityResultLauncher;

    static final  int  UPI_GATEWAY_REQUEST_CODE = 10101;
    static final  int  TYPE_UPI_ID = 0;
    static final  int  TYPE_UPI_GATEWAY = 1;

    private int paymentType = TYPE_UPI_GATEWAY;
    private Data upiGatewayPaymentData= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);
    
        initView();

        getHomeDataAPI();
        getActiveUpiAPI();

        pweActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        String resultStr = data.getStringExtra("result"); // e.g. "payment_successfull"
                        String paymentResponse = data.getStringExtra("payment_response"); // JSON string

                        Log.d("EasebuzzResult", "Result: " + resultStr);
                        Log.d("EasebuzzResponse", "Response: " + paymentResponse);

                        try {

                            JSONObject responseJson = new JSONObject(paymentResponse);

                            // ✅ Extract values directly
                            String status = responseJson.optString("status");
                            String txnId = responseJson.optString("txnid");
                            String amount = responseJson.optString("amount");
                            String email = responseJson.optString("email");
                            String easePayId = responseJson.optString("easepayid");
                            String phone = responseJson.optString("phone");
                            String firstname = responseJson.optString("firstname");

                            switch (resultStr) {
                                case "payment_successfull":
                                    // ✅ SUCCESS
                                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
                                    // Parse and use `paymentResponse` as needed
                                    eassBuzzPaymentSuccessAndFailureApi(
                                            true,
                                            txnId,amount,email,easePayId,phone,firstname,"success"
                                    );
                                    break;

                                case "payment_failed":
                                    // ❌ FAILURE
                                    eassBuzzPaymentSuccessAndFailureApi(
                                            false,
                                            txnId,amount,email,easePayId,phone,firstname,"failed"
                                    );
                                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
                                    break;

                                case "payment_cancelled":
                                    // ❌ USER CANCELLED
                                    eassBuzzPaymentSuccessAndFailureApi(
                                            false,
                                            txnId,amount,email,easePayId,phone,firstname,"failed"
                                    );
                                    Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    // ❓ UNEXPECTED
                                    Toast.makeText(this, "Unknown result", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing payment result", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

/*    private String generateEasebuzzHash(String key, String txnId, String amount,
                                        String productInfo, String firstName, String email, String salt) {
        try {
            String hashString = key + "|" + txnId + "|" + amount + "|" + productInfo + "|" +
                    firstName + "|" + email + "|||||||||||" + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] bytes = digest.digest(hashString.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }*/

//    private void initiateEasebuzzPayment(int amt) {
//        try {
//            String easebuzzPaymentUrl = "https://testpay.easebuzz.in/payment/initiateLink";
//            OkHttpClient client = new OkHttpClient();
//            String key = BuildConfig.RK_UPI_GATEWAY_KEY;
//            String txnId = "TXN" + System.currentTimeMillis();
//            String amount = String.valueOf(amt);
//            String productInfo = "Points Value";
//            String firstName = preferenceManager.getStringPreference(NAME);
//            String phone = preferenceManager.getStringPreference(MOBILE);
//            String email = preferenceManager.getStringPreference(MOBILE) + "@bpointer.com";
//            String salt = "BXS4O2542";
//            String hash = generateEasebuzzHash(key, txnId, amount, productInfo, firstName, email, salt);
//            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//            String postData = "key=" + key +
//                    "&txnid=" + txnId +
//                    "&amount=" + amount +
//                    "&productinfo=" + productInfo +
//                    "&firstname=" + firstName +
//                    "&phone=" + phone +
//                    "&email=" + email +
//                    "&surl=https://yourdomain.com/api/v1/creditReport/easebuzz/success" +
//                    "&furl=https://yourdomain.com/api/v1/creditReport/easebuzz/failure" +
//                    "&hash=" + hash;
//            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, postData);
//            Request request = new Request.Builder()
//                    .url(easebuzzPaymentUrl)
//                    .post(body)
//                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                    .addHeader("Accept", "application/json")
//                    .build();
//            okhttp3.Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                String responseData = response.body() != null ? response.body().string() : null;
//                Log.d("EasebuzzResponse", responseData != null ? responseData : "No response body");
//                if (responseData != null) {
//                    JSONObject jsonObject = new JSONObject(responseData);
//                    int status = jsonObject.getInt("status");
//                    String data = jsonObject.getString("data");
//                    Log.d("EasebuzzResponse", "Status: " + status + ", Data: " + data);
//                    runOnUiThread(() -> initOpenEaseBuzz(data));
//
//                    if (status) {
//                        // Toast.makeText(AddPointActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
//                        navigateToGatewayWebView(result.getData());
//
//                    } else {
//                        Toast.makeText(AddPointActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } else {
//                Log.e("EasebuzzError", "Request failed with code: " + response.code());
//            }
//        } catch (Exception e) {
//            Log.e("EasebuzzException", "Error occurred: " + e.getMessage(), e);
//        }
//    }

    private void eassBuzzPaymentSuccessAndFailureApi(Boolean isSuccess,String txnId,String amount,String email,String easePayId,String phone,String firstName,String status) {
        Map<String, Object> requestBody = new HashMap<>();
        BigDecimal paymentAmount = new BigDecimal(amount); // or new BigDecimal(amountDouble)

        requestBody.put("txnid", txnId);
        requestBody.put("easepayid", easePayId);
        requestBody.put("amount", amount);
        requestBody.put("productinfo", "Points Value");
        requestBody.put("firstname", firstName);
        requestBody.put("email", email);
        requestBody.put("phone", phone);
        requestBody.put("status", status);
        requestBody.put("user_id",userId);

        Call<EasebuzzPaymentStatusSuccessFailureResponse> call = isSuccess
                ? Api.getClient().create(Authentication.class).eassBuzzPaymentSuccessResponseSave(requestBody)
                : Api.getClient().create(Authentication.class).eassBuzzPaymentFailureResponseSave(requestBody);

//        customDialog.showLoader(); // Optional: show before request

        call.enqueue(new Callback<EasebuzzPaymentStatusSuccessFailureResponse>() {
            @Override
            public void onResponse(Call<EasebuzzPaymentStatusSuccessFailureResponse> call,
                                   Response<EasebuzzPaymentStatusSuccessFailureResponse> response) {
//                customDialog.closeLoader();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    System.out.println("Response data: " + response.body().getData());
                    if (isSuccess){
                        customDialog.showSuccessDialog(response.body().getMessage());
                        getHomeDataAPI();
                    }
                } else {
                    System.out.println("Empty or error response: " + (response.body() != null ? response.body().getData() : "null"));
                }
            }

            @Override
            public void onFailure(Call<EasebuzzPaymentStatusSuccessFailureResponse> call, Throwable t) {
//                customDialog.closeLoader();
                System.out.println("API failure: " + t.getMessage());
            }
        });

    }
    public void createAccessKeyForEassBuzzPayment(int amt) {
        customDialog.showLoader();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id",userId);
        requestBody.put("payment_amount", amt); // Or pass dynamic amount

        Call<EasebuzzAccessTokenResponseModel> call = Api.getClient().create(Authentication.class).getAccessKey(requestBody);

        call.enqueue(new Callback<EasebuzzAccessTokenResponseModel>() {
            @Override
            public void onResponse(Call<EasebuzzAccessTokenResponseModel> call, Response<EasebuzzAccessTokenResponseModel> response) {
                customDialog.closeLoader();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    String accessToken = response.body().getData().getAccessToken();
                    Log.d("Easebuzz", "Access token: " + accessToken);
                    initOpenEaseBuzz(accessToken);
                    // Proceed to initiate payment
                } else {
                    String message = "Failed to get access token";
                    Toast.makeText(AddPointActivity.this, message, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<EasebuzzAccessTokenResponseModel> call, Throwable t) {
                customDialog.closeLoader();
                String message = "API failure: " + t.getMessage();
                Toast.makeText(AddPointActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initOpenEaseBuzz(String accessKey) {
        Intent intentProceed = new Intent(getBaseContext(), PWECheckoutActivity.class);
        intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intentProceed.putExtra("access_key", accessKey);
        intentProceed.putExtra("pay_mode", "test");
        pweActivityResultLauncher.launch(intentProceed);
    }

    private void getHomeDataAPI() {
        customDialog.showLoader();

        RequestBody requestBody = new RequestBody();
        requestBody.setUser_id(String.valueOf(userId));

        Call<HomeResponseModel> call = Api.getClient().create(Authentication.class).getHomeData(requestBody);
        call.enqueue(new Callback<HomeResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponseModel> call, @NonNull Response<HomeResponseModel> response) {
                customDialog.closeLoader();
                if (response.body() != null) {
                    if (response.body().getStatus().equals("true")) {
                        wallet_amount = Integer.parseInt(response.body().getWallet());
                        tv_wallet.setText("" + wallet_amount);
                    } else {
                        Toast.makeText(AddPointActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<HomeResponseModel> call, Throwable t) {
                customDialog.closeLoader();
                Log.e("LoginResponse", "onFailure: " + t.getMessage());
            }
        });
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_wallet = findViewById(R.id.tv_wallet);
        tv_whatsapp = findViewById(R.id.tv_whatsapp);
        bt_submit = findViewById(R.id.bt_submit);
        et_point = findViewById(R.id.et_point);
        //tv_response = findViewById(R.id.tv_response);


        preferenceManager = new PreferenceManager(this);
        userId = preferenceManager.getIntPreference(ID);
        customDialog = new CustomDialog(this);

        tv_title.setText("Add Funds");
        tv_whatsapp.setText(preferenceManager.getStringPreference(ADMIN_WHATSAPP));

        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.bt_submit:
                hideKeyboard();
                if (et_point.getText().toString().isEmpty()) {
                    et_point.setError("Point Required !");
                    et_point.requestFocus();
                } else if (Integer.parseInt(et_point.getText().toString()) < 1) {
                    et_point.setError("Minimum 100/- point Accepted !");
                    et_point.requestFocus();
                } else {
                    int abc  = 2;
//                    if(paymentType == abc) {
//                        createUpiGatewayOrder(Integer.parseInt(et_point.getText().toString().trim()));
//                    }
//                    else if(abc == 3){
                        createAccessKeyForEassBuzzPayment(Integer.parseInt(et_point.getText().toString().trim()));
//                    }
//                    else {
//                        paymentIndi(Integer.parseInt(et_point.getText().toString().trim()));
//                    }
                }
                break;
        }
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void paymentIndi(int amt) {
        if(TextUtils.isEmpty(upi_id))
            upi_id=UPI_ID;

        if(TextUtils.isEmpty(display_name))
            upi_id= "R K Group";

        String transactionId = "TID" + System.currentTimeMillis();
        String desc = "Add to wallet";
        double amount = amt * 1.00;
        finalAmount = String.valueOf(amount);

        IndiUpi indiUpi = new IndiUpi.Builder()
                .with(this)
                .setPayeeVpa(upi_id)
                .setAmount(finalAmount)
                .setPayeeName(display_name)
                .setDescription(desc)
                .setTransactionId(transactionId)
                .setTransactionRefId(transactionId)
                //internal parameter automatically add in URL same as above UPI request
                .build();

        indiUpi.pay("Pay With");
        indiUpi.setPaymentStatusListener(this);

    }

    @Override
    public void onTransactionCompleted(TransactionResponse transactionResponse) {
        // Transaction Completed
        Log.e("TransactionResponse", transactionResponse.toString());
        //tv_response.setText("onTransactionCompleted--->"+transactionResponse);
    }

    @Override
    public void onTransactionSuccess(TransactionResponse transactionResponse) {
        // Payment Success
        Log.e("AddPointActivity", "transactionResponse--->"+transactionResponse);
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();

        //tv_response.setText("onTransactionSuccess--->"+transactionResponse);
        addFundAPI(finalAmount, transactionResponse.getTransactionId());
    }

    @Override
    public void onTransactionSubmitted() {
        // Payment Pending
        Toast.makeText(this, "Payment Pending Or Submitted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionFailed() {
        // Payment Failed
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionCancelled() {
        // Payment Process Cancelled by User
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void addFundAPI(String point, String txnumber) {
        customDialog.showLoader();

        RequestBody requestBody = new RequestBody();
        requestBody.setUser_id(String.valueOf(userId));
        requestBody.setDeposit_amount(point);
        requestBody.setTransaction_number(txnumber);

        Call<AddFundResponseModel> call = Api.getClient().create(Authentication.class).addFund(requestBody);
        call.enqueue(new Callback<AddFundResponseModel>() {
            @Override
            public void onResponse(Call<AddFundResponseModel> call, Response<AddFundResponseModel> response) {
                customDialog.closeLoader();
                if (response.body() != null) {
                    if (response.body().getStatus().equals("true")) {
                        customDialog.showSuccessDialog(response.body().getMessage());
                        getHomeDataAPI();
                    } else {
                        Toast.makeText(AddPointActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddFundResponseModel> call, Throwable t) {
                customDialog.closeLoader();
                Log.e("LoginResponse", "onFailure: " + t.getMessage());
            }
        });
    }



    private void getActiveUpiAPI() {


        Call<GetActiveUpiModel> call = Api.getClient().create(Authentication.class).getActiveUpiId();
        call.enqueue(new Callback<GetActiveUpiModel>() {
            @Override
            public void onResponse(Call<GetActiveUpiModel> call, Response<GetActiveUpiModel> response) {
                customDialog.closeLoader();
                if (response.body() != null  ) {
                    GetActiveUpiModel result = response.body();
                    if (result.isStatus() && result.getData() != null && result.getData().getPaymentOption() != null) {
//                       upi_id = result.getData().getPaymentOption().getPaymentId();
                       upi_id = "7755965976@ucobank";
                       display_name = result.getData().getPaymentOption().getPayeeName();
                       paymentType = result.getData().getPaymentOption().getPaymentType();
                    } else {
                        Toast.makeText(AddPointActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetActiveUpiModel> call, Throwable t) {
                customDialog.closeLoader();
                Log.e("LoginResponse", "onFailure: " + t.getMessage());
            }
        });

    }


    private void createUpiGatewayOrder(int amt) {
        customDialog.showLoader();

        double amount = amt * 1.00;
        finalAmount = String.valueOf(amount);

        UpiGatewayOrderRequest request = new UpiGatewayOrderRequest();
        request.setKey(BuildConfig.RK_UPI_GATEWAY_KEY);
        request.setClientTxnId("TID" + System.currentTimeMillis());
        request.setAmount(String.valueOf(amount));
        request.setpInfo("add points");
        request.setCustomerName(preferenceManager.getStringPreference(NAME));
        request.setCustomerMobile(preferenceManager.getStringPreference(MOBILE));
        request.setCustomerEmail(preferenceManager.getStringPreference(MOBILE) +"@bpointer.com");
        request.setRedirectUrl("http://google.com");
        request.setUdf1("UserId="+preferenceManager.getIntPreference(ID));
        request.setUdf2("NA");
        request.setUdf3("NA");

        Call<UpiGatewayOrderResponse> call = Api.getUpiGateWayClient().create(Authentication.class).createUpiGatewayOrder(request);
        call.enqueue(new Callback<UpiGatewayOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpiGatewayOrderResponse> call, @NonNull Response<UpiGatewayOrderResponse> response) {
                customDialog.closeLoader();
                if (response.body() != null) {
                    UpiGatewayOrderResponse result = response.body();
                    if (result.getStatus()) {
                       // Toast.makeText(AddPointActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        navigateToGatewayWebView(result.getData());

                    } else {
                        Toast.makeText(AddPointActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpiGatewayOrderResponse> call, Throwable t) {
                customDialog.closeLoader();
                Log.e("UpiGatewayOrderResponse", "onFailure: " + t.getMessage());
            }
        });
    }

    private void navigateToGatewayWebView(Data gatewayPaymentData) {
        upiGatewayPaymentData = gatewayPaymentData;

        Intent intent = new Intent(this, UpiPaymentGatewayActivity.class);
        intent.putExtra("paymentUrl",upiGatewayPaymentData.getPaymentUrl());
        startActivityForResult(intent, UPI_GATEWAY_REQUEST_CODE);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(upiGatewayPaymentData.getPaymentUrl()));
//        startActivity(intent);
        /*new ModalBottomSheetDialog.Builder()
                .setHeader("Select payment option") // optional
                .add(R.menu.payment_menu) // can be used more then once
                .setRoundedModal(true) // optional (default is false)
                .show(getSupportFragmentManager(), "dialog_payment");*/
    }

//    private void navigateToGatewayWebView(Data gatewayPaymentData){
//        Intent intent = new Intent(this, PaymentWebViewActivity.class);
//        intent.putExtra("paymentUrl", gatewayPaymentData.getPaymentUrl());
//        startActivity(intent);
//    }


    @Override
    public void onItemSelected(String tag, Item item) {
        Toast.makeText(getApplicationContext(), "Tag: " + tag + ", clicked on: " + item.getTitle(),
                Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (item.getId()) {
            case R.id.menuQrCode:

                intent = new Intent(this, UpiPaymentGatewayActivity.class);
                intent.putExtra("paymentUrl",upiGatewayPaymentData.getPaymentUrl());
                startActivityForResult(intent, UPI_GATEWAY_REQUEST_CODE);
                break;
            case R.id.menuGpay:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upiGatewayPaymentData.getUpiIntent().getGpayLink()));
                startActivity(intent);
                break;
            case R.id.menuPaytm:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upiGatewayPaymentData.getUpiIntent().getPaytmLink()));
                startActivity(intent);
                break;
            case R.id.menuPhonepe:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upiGatewayPaymentData.getUpiIntent().getPhonepeLink()));
                startActivity(intent);
                break;

            case R.id.menuBhim:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upiGatewayPaymentData.getUpiIntent().getBhimLink()));
                startActivity(intent);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPI_GATEWAY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String clientTxnId =  data.getStringExtra("clientTxnId");
            String txnId =  data.getStringExtra("txnId");
            checkUpiGatewayOrderPaymentStatus(clientTxnId);
           // addFundAPI(finalAmount,clientTxnId);

        }
    }

    private void checkUpiGatewayOrderPaymentStatus( String clientTxnId) {
        customDialog.showLoader();

        RequestBody requestBody = new RequestBody();
       // requestBody.setUser_id(String.valueOf(userId));
        requestBody.setClientTxnId(clientTxnId);

        Call<UpiGatewayOrderPaymentStatus> call = Api.getClient().create(Authentication.class).checkUpiGatewayOrderStatus(requestBody);
        call.enqueue(new Callback<UpiGatewayOrderPaymentStatus>() {
            @Override
            public void onResponse(Call<UpiGatewayOrderPaymentStatus> call, Response<UpiGatewayOrderPaymentStatus> response) {
                customDialog.closeLoader();
                if (response.body() != null) {
                    UpiGatewayOrderPaymentStatus result = response.body();
                    if (result.getStatus()) {
                       // customDialog.showSuccessDialog(response.body().getMessage());
                        com.bpointer.rkofficial.Model.upigateway.paymentstatus.Data resultData = result.getData();
                        if("success".equals(resultData.getTransactionStatus())) {
                            addFundAPI(finalAmount,clientTxnId);
                        }else {
                            customDialog.showFailureDialog(resultData.getRemark());
                        }



                    } else {
                        customDialog.showFailureDialog( result.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<UpiGatewayOrderPaymentStatus> call, Throwable t) {
                customDialog.closeLoader();
                Log.e("LoginResponse", "onFailure: " + t.getMessage());
            }
        });
    }



}