package com.softgyan.whatsapp.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softgyan.whatsapp.R;

public class GalleryDialog extends BottomSheetDialog {
    private final FloatingActionButton[] floatingActionButtons;
    private final GalleryCallback callback;

    public GalleryDialog(@NonNull Context context, GalleryCallback callback) {
        super(context);
        this.callback = callback;
        this.floatingActionButtons = new FloatingActionButton[6];
        setContentView(R.layout.layout_gallery);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLinearLayout();
    }

    //user define functions

    private void getLinearLayout() {
        LinearLayout linearLayout;
        linearLayout = findViewById(R.id.layout_1);
        for (int i = 0; i < 3; i++) {
            floatingActionButtons[i] = (FloatingActionButton) ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(0);
            int finalI = i;
            floatingActionButtons[i].setOnClickListener(view -> {
                onClickListener(view, finalI);
            });
        }
//        LinearLayout linearLayout1 = findViewById(R.id.layout_2);
//        for (int i = 0; i < 3; i++) {
//            floatingActionButtons[i + 3] = (FloatingActionButton) ((LinearLayout) linearLayout1.getChildAt(i)).getChildAt(0);
//            int finalI = i + 3;
//            floatingActionButtons[i].setOnClickListener(view -> {
//                onClickListener(view, finalI);
//            });
//        }

    }

    private void onClickListener(View v, int i) {
        Toast.makeText(getContext(), "clicked" + i, Toast.LENGTH_SHORT).show();
        switch (i) {
            case 2:
                callback.onGalleryClick();
                break;

        }
        dismiss();
    }


    //interface for listener
    public interface GalleryCallback {
        void onGalleryClick();
    }


}
