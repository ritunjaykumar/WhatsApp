package com.softgyan.whatsapp.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;
import com.softgyan.whatsapp.R;

public class DialogReviewSendImage {
    private final Context mContext;
    private final Bitmap bitmap;
    private final Dialog dialog;
    private ZoomageView zoomageView;
    private FloatingActionButton fabSend;

    public DialogReviewSendImage(Context mContext, Bitmap bitmap) {
        this.mContext = mContext;
        this.bitmap = bitmap;
        dialog = new Dialog(mContext);
        initDialog();
    }

    private void initDialog() {
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_review_image_chat);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        zoomageView = dialog.findViewById(R.id.zv_image);
        fabSend = dialog.findViewById(R.id.fab_send);

    }

    public void show(final OnCallBack onCallBack) {
        dialog.show();
        zoomageView.setImageBitmap(bitmap);
        fabSend.setOnClickListener(view -> {
            onCallBack.onButtonSendClick();
            dialog.dismiss();
        });
    }


    //interface
    public interface OnCallBack {
        void onButtonSendClick();
    }

}
