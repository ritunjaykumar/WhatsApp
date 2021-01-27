package com.softgyan.whatsapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.models.ChatList;
import com.softgyan.whatsapp.utils.common.Common;
import com.softgyan.whatsapp.widgets.activity.ChatActivity;
import com.softgyan.whatsapp.widgets.activity.ProfileActivity;
import com.softgyan.whatsapp.widgets.activity.UserProfileActivity;
import com.softgyan.whatsapp.widgets.activity.ViewImageActivity;

public class DialogViewUser {
    private final Context mContext;

    public DialogViewUser(Context context, ChatList chatList, ViewUserCallback callback) {
        this.mContext = context;
        initContent(chatList, callback);
    }

    public void initContent(ChatList chatList, ViewUserCallback callback) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.dialog_view_user);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageView profile;
        TextView userName;
        userName = dialog.findViewById(R.id.tv_user_name);
        profile = dialog.findViewById(R.id.iv_profile_image);

        userName.setText(chatList.getUserName());
        Glide.with(mContext).load(chatList.getUrlProfile()).placeholder(R.drawable.ic_place_holder).into(profile);

        dialog.findViewById(R.id.ib_message).setOnClickListener(view -> {
            callback.onClickedMessage();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.ib_info).setOnClickListener(view -> {
            Intent intent = new Intent(mContext, UserProfileActivity.class);
            intent.putExtra(ChatActivity.RECEIVER_ID, chatList.getUserId());
            intent.putExtra(ChatActivity.RECEIVER_PROFILE, chatList.getUrlProfile());
            intent.putExtra(ChatActivity.RECEIVER_NAME, chatList.getUserName());
            mContext.startActivity(intent);
            dialog.dismiss();
        });

        profile.setOnClickListener(view->{
            profile.invalidate();
            Drawable dr = profile.getDrawable();
            Common.BIT_MAP = ((BitmapDrawable) dr.getCurrent()).getBitmap();
            ActivityOptionsCompat aoCompact = ActivityOptionsCompat
                    .makeSceneTransitionAnimation((Activity) mContext, profile, "image");
            Intent intent = new Intent(mContext, ViewImageActivity.class);
            mContext.startActivity(intent, aoCompact.toBundle());
        });

        dialog.show();
    }

    public interface ViewUserCallback {
        void onClickedMessage();
    }
}
