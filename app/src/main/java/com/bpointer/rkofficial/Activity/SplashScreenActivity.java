package com.bpointer.rkofficial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bpointer.rkofficial.Api.Api;
import com.bpointer.rkofficial.Api.Authentication;
import com.bpointer.rkofficial.Common.CustomDialog;
import com.bpointer.rkofficial.Common.PreferenceManager;
import com.bpointer.rkofficial.Common.SessionManager;
import com.bpointer.rkofficial.Model.Response.GetAppVersionResponse.GetAppVersionResponse;
import com.bpointer.rkofficial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.bpointer.rkofficial.Common.AppConstant.AUTH_TOKEN;
import static com.bpointer.rkofficial.Common.AppConstant.TOKEN_ID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {
   SessionManager sessionManager;
   PreferenceManager preferenceManager;
   CustomDialog customDialog;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_splash_screen);
   
      customDialog = new CustomDialog(this);
      sessionManager = new SessionManager(SplashScreenActivity.this);
      preferenceManager = new PreferenceManager(SplashScreenActivity.this);

      // Restore persisted JWT auth token so it is sent with every API request
      String savedAuthToken = preferenceManager.getStringPreference(AUTH_TOKEN);
      if (savedAuthToken != null && !savedAuthToken.isEmpty()) {
         Api.setAuthToken(savedAuthToken);
      }

      FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
               @Override
               public void onComplete(@NonNull Task<String> task) {
                  if (!task.isSuccessful()) {
                     return;
                  }
                  // Get new FCM registration token
                  String token = task.getResult();
                  preferenceManager.setPreference(TOKEN_ID, token);
                  Log.e("check_token", "onComplete: " + token);
               }
            });
   }
   
   @Override
   protected void onResume() {
      super.onResume();
      checkAppVersion(getCurrentVersionName());
   }

   private String getCurrentVersionName() {
      try {
         PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
         return pInfo.versionName;
      } catch (PackageManager.NameNotFoundException e) {
         e.printStackTrace();
         return "0";
      }
   }

   // Returns true if backendVersion is newer than currentVersion
   // Compares each segment numerically, e.g. "1.0.1.3" > "1.0.1.1"
   private boolean isUpdateRequired(String currentVersion, String backendVersion) {
      try {
         String[] current = currentVersion.split("\\.");
         String[] backend = backendVersion.split("\\.");
         int maxLen = Math.max(current.length, backend.length);
         for (int i = 0; i < maxLen; i++) {
            int c = i < current.length ? Integer.parseInt(current[i]) : 0;
            int b = i < backend.length ? Integer.parseInt(backend[i]) : 0;
            if (b > c) return true;
            if (b < c) return false;
         }
         return false; // versions are equal
      } catch (NumberFormatException e) {
         Log.e("SplashScreen", "Version parse error: " + e.getMessage());
         return false;
      }
   }
   
   private void checkAppVersion(String currentVersion) {
      customDialog.showLoader();
      Call<GetAppVersionResponse> call = Api.getClient().create(Authentication.class).getAppVersion();
      call.enqueue(new Callback<GetAppVersionResponse>() {
         @Override
         public void onResponse(Call<GetAppVersionResponse> call, Response<GetAppVersionResponse> response) {
            customDialog.closeLoader();
            if (response.body() != null) {
               if (response.body().getStatus().equals("true")) {
                  String backendVersion = response.body().getData().getAppVersion();
                  if (isUpdateRequired(currentVersion, "1.0.1.1")) {
                     updateApp();
                  } else {
                     callIntent();
                  }
               } else {
                  Toast.makeText(SplashScreenActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
               }
            }
         }
         
         @Override
         public void onFailure(Call<GetAppVersionResponse> call, Throwable t) {
            customDialog.closeLoader();
            Log.e("LoginResponse", "onFailure: " + t.getMessage());
         }
      });
   }
   
   private void callIntent() {
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
            if (sessionManager.isLoggedIn()) {
               Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
               startActivity(i);
               finish();
            } else {
               Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
               startActivity(i);
               finish();
            }
         }
      }, 2000);
   }
   

   private void updateApp() {
      runOnUiThread(() -> {
         if (isFinishing() || isDestroyed()) return;
         try {
            new androidx.appcompat.app.AlertDialog.Builder(SplashScreenActivity.this)
                  .setTitle("Update Available")
                  .setMessage("You are using an older version, please update!")
                  .setCancelable(false)
                  .setPositiveButton("Update Now", (dialog, which) -> {
                     final String appPackageName = getPackageName();
                     try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                     } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                     }
                  })
                  .show();
         } catch (Exception e) {
            Log.e("SplashScreen", "updateApp dialog error: " + e.getMessage());
         }
      });
   }
}