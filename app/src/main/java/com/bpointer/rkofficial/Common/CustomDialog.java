package com.bpointer.rkofficial.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
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

    private boolean isContextAlive() {
        Activity activity = getActivity(context);
        if (activity == null) return true; // non-activity context, allow
        return !activity.isFinishing() && !activity.isDestroyed();
    }

    private static Activity getActivity(Context ctx) {
        while (ctx instanceof ContextWrapper) {
            if (ctx instanceof Activity) return (Activity) ctx;
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }
        return null;
    }

    public void showSuccessDialog(String msg) {
        if (!isContextAlive()) return;
        try {
            new AlertDialog.Builder(context)
                    .setTitle("Success")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", (d, which) -> d.dismiss())
                    .show();
        } catch (Exception ignored) { }
    }

    public void showFailureDialog(String msg) {
        if (!isContextAlive()) return;
        try {
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", (d, which) -> d.dismiss())
                    .show();
        } catch (Exception ignored) { }
    }

    public void showLoader() {
        if (!isContextAlive()) return;
        try {
            Dialog dialog2 = new Dialog(context, R.style.alert_dialog_light);
            this.dialog = dialog2;
            dialog2.setContentView(R.layout.loading_layout);
            if (dialog2.getWindow() != null) {
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            this.dialog.setCancelable(false);
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        } catch (Exception ignored) { }
    }

    public void closeLoader() {
        if (this.dialog == null) return;
        try {
            if (this.dialog.isShowing() && isContextAlive()) {
                this.dialog.dismiss();
            }
        } catch (Exception ignored) {
            // View not attached / window already gone — safe to ignore
        } finally {
            this.dialog = null;
        }
    }
}
