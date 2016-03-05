package org.arkaic.calnmacs;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.arkaic.calnmacs.FoodDbFragment.OnFoodDbFragmentInteractionListener;
import org.arkaic.calnmacs.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements OnFoodDbFragmentInteractionListener {
    static final int DUMMY_NUM_ITEMS = 2;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO will need a fixed bar soon
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Database stuff
        FoodDbHelper dbHelper = new FoodDbHelper(getApplicationContext());
        mDb = dbHelper.getWritableDatabase();

        // adapter setting
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));

        // todo might need this too
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onFoodDbFragmentInteraction(DummyContent.DummyItem item) {
        // TODO implement interaction from fooddb to whatever
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
//                case 0:  return MainFragment.newInstance(position);
                case 1:  return FoodDbFragment.newInstance(position);
                default: return FoodDbFragment.newInstance(position);
                //TODO uncomment the top and change default to be the same
            }
        }
    }

}
