package com.softgyan.whatsapp.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolBar);
        setWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

//        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                changeFabIcon(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    private void setWithViewPager(ViewPager viewPager) {
        MainActivity.SelectionPagerAdapter adapter = new SelectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ChatFragment.getInstance(), getString(R.string.chat));
        adapter.addFragment(StatusFragment.getInstance(), getString(R.string.status));
        adapter.addFragment(CallsFragment.getInstance(), getString(R.string.calls));
        viewPager.setAdapter(adapter);
    }

    public static class SelectionPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private final ArrayList<String> mFragmentTitleList = new ArrayList<>();

        public SelectionPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            //todo for search
            return true;
        } else if (id == R.id.menu_group) {
            //todo for more
            return true;
        }  else if (id == R.id.menu_broadcast) {
            //todo for more
            return true;
        }  else if (id == R.id.menu_web) {
            //todo for more
            return true;
        }  else if (id == R.id.menu_stared) {
            //todo for more
            return true;
        }  else if (id == R.id.menu_setting) {
            startActivity(new Intent(MainActivity.this,SettingActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

//    private void changeFabIcon(final int index){
//        binding.fabAction.hide();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                switch (index){
//                    case 0: binding.fabAction.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_chat));break;
//                    case 1: binding.fabAction.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_camera));break;
//                    case 2: binding.fabAction.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_call));break;
//                }
//                binding.fabAction.show();
//            }
//        },400);
//
//    }
}