package com.softgyan.whatsapp.widgets.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityViewImageBinding;
import com.softgyan.whatsapp.utils.common.Common;

import java.util.Objects;

public class ViewImageActivity extends AppCompatActivity {
    private ActivityViewImageBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_image);
        setSupportActionBar(mBinding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mBinding.zvImage.setImageBitmap(Common.BIT_MAP);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.menu_edit) {
            //todo code for edit
        } else if (id == R.id.menu_search) {
            //todo for share image
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}