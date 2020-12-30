package com.softgyan.whatsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.utils.variables.Var;

public class UserDetails {
    private Context mContext;
    private static UserDetails userInfo;
    private static SharedPreferences sp;

    private UserDetails(Context context) {
        this.mContext = context;
        sp = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public static UserDetails getInstance(Context context) {
        if (userInfo == null) {
            userInfo = new UserDetails(context);
        }
        return userInfo;
    }

    public void setUserInfo(String name) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Var.USER_NAME, name);
        editor.apply();
    }

    public void setStatus(String status) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Var.STATUS, status);
        editor.apply();
    }

    public void setImageProfile(String profileURL) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Var.IMAGE_PROFILE, profileURL);
        editor.apply();
    }

    public String getStatus() {
        String status = sp.getString(Var.STATUS, null);
        if (status == null) {
            return "Hey, I am using WhatsApp";
        } else {
            return status;
        }
    }

    public String getImageProfile() {
        return sp.getString(Var.IMAGE_PROFILE, null);
    }

    public String getUserName() {
        String userName = sp.getString(Var.USER_NAME, null);
        if (userName == null) {
            return "name din't set";
        } else {
            return userName;
        }
    }

    public void setUserNameSet(boolean isUserNameSet) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("user_record", isUserNameSet);
        editor.apply();
    }

    public boolean isUserNameSet() {
        return sp.getBoolean("user_record", false);
    }

    public void logout() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }


}
