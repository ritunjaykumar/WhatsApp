package com.softgyan.whatsapp.widgets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.utils.UserDetails;

public class StatusFragment extends Fragment {

    private static StatusFragment statusFragment;
    private ShapeableImageView sivProfile;
    private TextView tvUserName;

    private StatusFragment() {
    }


    public static StatusFragment getInstance() {
        if (statusFragment == null) {
            statusFragment = new StatusFragment();
        }
        return statusFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        sivProfile = view.findViewById(R.id.siv_profile);
        tvUserName = view.findViewById(R.id.tv_user_name);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStatusData();
    }

    private void setStatusData() {
        UserDetails user = UserDetails.getInstance(getContext());
        if (user == null)
            return;
        tvUserName.setText(user.getUserName());
        Glide.with(getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_place_holder).into(sivProfile);
    }
}