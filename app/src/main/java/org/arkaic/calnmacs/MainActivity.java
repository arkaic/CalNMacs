package org.arkaic.calnmacs;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

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
        FoodDbHelper dbHelper = new FoodDbHelper(getApplicationContext());
        mDb = dbHelper.getWritableDatabase();

        // adapter setting
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onFoodDbFragmentInteraction(Uri uri) {
        // TODO implement interaction of fragment and any needed information passing to this activity
        // and to any other fragments this activity may need to pass info to.
        // http://developer.android.com/training/basics/fragments/communicating.html
        // In fragment's onAttach() call, it will assign reference to this Activity casted as the
        // interface. Then in fragment.onButtonClick(), it'll handle the button click by calling
        // the implemented method(s) of the referenced Activity.

        // use cases: deletion or addition of foods into the foodlist
    }

    @Override
    public void onMainFragmentInteraction(Uri uri) {
        // only needed if calories page needs to tell food list somehting
    }


    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
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
    }

}
