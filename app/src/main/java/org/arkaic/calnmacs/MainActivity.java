package org.arkaic.calnmacs;

import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import org.arkaic.calnmacs.FoodDbFragment.OnFoodDbFragmentInteractionListener;
import org.arkaic.calnmacs.MainFragment.OnMainFragmentInteractionListener;


public class MainActivity extends AppCompatActivity
        implements OnFoodDbFragmentInteractionListener, OnMainFragmentInteractionListener {

    static final int DUMMY_NUM_ITEMS = 2;
    private SQLiteDatabase mDb;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database stuff
        FoodDbHelper dbHelper = FoodDbHelper.getInstance(getApplicationContext());
        mDb = dbHelper.getWritableDatabase();

        // adapter setting
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), mPager));
    }

    @Override
    public void onFoodDelete(int id) {
        MainFragment mainFragment = (MainFragment)(((MyFragmentPagerAdapter)mPager.getAdapter()).getFragment(0));
        if (mainFragment != null)
            mainFragment.deleteFoodById(id);
        else
            Log.w("**** MY LOGS ****", "mainFragment is null!");
    }

    @Override
    public void onMainFragmentInteraction(Uri uri) {
        // only needed if calories page needs to tell food list somehting
    }


    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private Fragment mMainFrag;
        private Fragment mFoodDbFrag;
        private ViewPager mPager;

        public MyFragmentPagerAdapter(FragmentManager fm, ViewPager pager) {
            super(fm);
            mPager = pager;
        }

        @Override
        public int getCount() {
            return DUMMY_NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            // Called when adapter needs a fragment that doesn't exist in the FragmentManager
            switch(position) {
                case 0:  return MainFragment.newInstance(position);
                case 1:  return FoodDbFragment.newInstance(position);
                default: return MainFragment.newInstance(position);
            }
        }

        public Fragment getFragment(int position) {
            return getSupportFragmentManager().findFragmentByTag(makeFragmentName(mPager.getId(), position));
        }

        // It's the same as the private function in the super class
        private String makeFragmentName(int viewId, int index) {
            return "android:switcher:" + viewId + ":" + index;
        }

//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            Object frag = super.instantiateItem(container, position);
//            if (position == 0)
//                mMainFrag = (Fragment)frag;
//            else if (position == 1)
//                mFoodDbFrag = (Fragment)frag;
//            return frag;
//        }
    }

}
