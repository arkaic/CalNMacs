package org.arkaic.calnmacs;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import static org.arkaic.calnmacs.FoodDbContract.FoodDbColumns;

// TODO: Make database. Display

public class FoodListActivity extends ListActivity {

    private SQLiteDatabase mDb;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_database);

        // Database stuff
        FoodDbHelper dbHelper = new FoodDbHelper(getApplicationContext());
        mDb = dbHelper.getWritableDatabase();

        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = mDb.rawQuery("SELECT * from foods;", null);
                String[] fromColumns = {FoodDbColumns.COLUMN_NAME_COL1,
                                        FoodDbColumns.COLUMN_NAME_COL2,
                                        FoodDbColumns.COLUMN_NAME_COL3,
                                        FoodDbColumns.COLUMN_NAME_COL4,
                                        FoodDbColumns.COLUMN_NAME_COL5,
                                        FoodDbColumns.COLUMN_NAME_COL6,
                                        FoodDbColumns.COLUMN_NAME_COL7,
                                        FoodDbColumns.COLUMN_NAME_COL8};
                int[] toViews = {android.R.id.text1};
                mAdapter = new SimpleCursorAdapter(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    c,
                    fromColumns,
                    toViews,
                    0
                );
                ListView listView = (ListView) findViewById(android.R.id.list);
                listView.setAdapter(mAdapter);
            }
        });

        FloatingActionButton closeb = (FloatingActionButton) findViewById(R.id.closeButton);
        closeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open a dialogue for adding custom food
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food_database, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
