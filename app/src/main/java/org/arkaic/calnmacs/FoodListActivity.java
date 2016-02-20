package org.arkaic.calnmacs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static org.arkaic.calnmacs.FoodDbContract.FoodDbColumns;

// TODO: Make database. Display

public class FoodListActivity extends AppCompatActivity {

    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_database);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Database stuff
        FoodDbHelper dbHelper = new FoodDbHelper(getApplicationContext());
        _db = dbHelper.getWritableDatabase();

        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t = (TextView) findViewById(R.id.txt);
                t.setText("FOOD DB\n");
                t.append(FoodDbColumns.COLUMN_NAME_COL1 + " ");
                t.append(FoodDbColumns.COLUMN_NAME_COL2 + " ");
                t.append(FoodDbColumns.COLUMN_NAME_COL3 + " ");
                t.append(FoodDbColumns.COLUMN_NAME_COL4 + " ");
                t.append(FoodDbColumns.COLUMN_NAME_COL5 + " ");
                t.append(FoodDbColumns.COLUMN_NAME_COL6 + " ");
                t.append(FoodDbColumns.COLUMN_NAME_COL7 + "\n");
                Cursor c = _db.rawQuery("SELECT * from foods;", null);
                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) {
                        String name = c.getString(c.getColumnIndex(FoodDbContract.FoodDbColumns.COLUMN_NAME_COL1));
                        t.append(c.getString(c.getColumnIndex(FoodDbColumns.COLUMN_NAME_COL1)) + " | ");
                        t.append(c.getString(c.getColumnIndex(FoodDbColumns.COLUMN_NAME_COL2)) + " | ");
                        t.append(c.getString(c.getColumnIndex(FoodDbColumns.COLUMN_NAME_COL3)) + " | ");
                        t.append(c.getString(c.getColumnIndex(FoodDbColumns.COLUMN_NAME_COL4)) + " | ");
                        t.append(c.getString(c.getColumnIndex(FoodDbColumns.COLUMN_NAME_COL5)) + " | ");
                        t.append(c.getString(c.getColumnIndex(FoodDbColumns.COLUMN_NAME_COL6)) + " | ");
                        t.append(c.getString(c.getColumnIndex(FoodDbColumns.COLUMN_NAME_COL7)) + "\n");
                        c.moveToNext();
                    }
                }
                c.close();
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
