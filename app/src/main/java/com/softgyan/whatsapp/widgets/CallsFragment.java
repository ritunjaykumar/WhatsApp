package com.softgyan.whatsapp.widgets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.adapter.CallListAdapter;
import com.softgyan.whatsapp.adapter.ChatListAdapter;
import com.softgyan.whatsapp.models.CallList;
import com.softgyan.whatsapp.models.ChatList;

import java.util.ArrayList;
import java.util.List;

public class CallsFragment extends Fragment {

    private CallsFragment() {
        // Required empty public constructor
    }
    private static CallsFragment fragment;
    private List<CallList> callLists =new ArrayList<>();
    private RecyclerView rvCallList;
    private CallListAdapter callListAdapter;
    private FloatingActionButton fabCall;

    public static CallsFragment getInstance() {
        if(fragment == null){
            fragment = new CallsFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getList();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_calls, container, false);
        rvCallList = view.findViewById(R.id.rv_call_list);
        fabCall = view.findViewById(R.id.fab_action);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callListAdapter = new CallListAdapter(callLists);
        rvCallList.setAdapter(callListAdapter);
        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });
    }

    private void getList() {
        String img1 ="https://static.toiimg.com/photo/msid-77142971/77142971.jpg";
        String img2 ="https://i.ytimg.com/vi/zCcGRZ9kAhU/maxresdefault.jpg";
        String img3 = "https://i.ytimg.com/vi/csAPrTKNJPY/maxresdefault.jpg";
        String img4 ="https://vignette.wikia.nocookie.net/motu-patlu/images/3/3e/Profile_motu2.png";
        String img5 ="https://vignette.wikia.nocookie.net/jorjorswackyjourney/images/1/18/Motu_patlu.jpg";
        callLists.add(new CallList("11", "Ritunjay", "12/02/2002", img1,R.drawable.ic_phone_incoming));
        callLists.add(new CallList("11", "Arunjay",  "10/03/2020", img2,R.drawable.ic_phone_outgoing));
        callLists.add(new CallList("11", "Richa",  "24/03/2002", img3,R.drawable.ic_phone_incoming));
        callLists.add(new CallList("11", "Renu",  "01/05/2002", img4,R.drawable.ic_phone_missed));
        callLists.add(new CallList("11", "Jyoti",  "01/01/2002", img5,R.drawable.ic_phone_missed));
    }
}
