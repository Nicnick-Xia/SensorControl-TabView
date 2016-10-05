package com.nicnick.sensortabindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.nicnick.sensortabindicator.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerIndicator mViewPagerIndicator;

    private List<String> mTitles = Arrays.asList("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6", "Tab7", "Tab8", "Tab9", "Tab10", "Tab11");
    //private List<String> mBreifContent = Arrays.asList("message","friends","discovery","personal");

    private List<SimpleFragment> mContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        mViewPagerIndicator.setTabTitle(mTitles);
        mViewPagerIndicator.setViewPager(mViewPager, 0);

        mViewPager.setAdapter(mAdapter);

    }

    @Override
    protected void onPause() {
        mViewPagerIndicator.unregisterSensorListener();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mViewPagerIndicator.setGravitySensor(this);
        super.onResume();
    }

    private void initData() {
        int size = mTitles.size();
        for (int i = 0; i < size; i++) {
            SimpleFragment fragment = SimpleFragment.newInstance(mTitles.get(i));
            mContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };

    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.id_indicater);
    }

}
