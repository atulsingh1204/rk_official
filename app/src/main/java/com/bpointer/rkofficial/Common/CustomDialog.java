package com.bpointer.rkofficial.Common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bpointer.rkofficial.R;

public class CustomDialog {
    Dialog dialog;
    Context context;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public void showSuccessDialog(String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Success")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (d, which) -> d.dismiss())
                .show();
    }

    public void showFailureDialog(String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (d, which) -> d.dismiss())
                .show();
    }

    public void showLoader() {
        Dialog dialog2 = new Dialog(context, R.style.alert_dialog_light);
        this.dialog = dialog2;
        dialog2.setContentView(R.layout.loading_layout);
        if (dialog2.getWindow() != null) {
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        this.dialog.setCancelable(false);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
    }

    public void closeLoader() {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }
}
