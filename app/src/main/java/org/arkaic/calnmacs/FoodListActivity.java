package org.arkaic.calnmacs;

import android.app.ListActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
//import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.MessageFormat;

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

        // List view click event
        final ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // view is the toplevel object type defined in the table row xml
                TableRow foodRow = ((TableRow)view.findViewById(R.id.foodRow));
                CharSequence name = ((TextView)foodRow.findViewById(R.id.foodName)).getText();

                AlertDialog alertDialog = new AlertDialog.Builder(FoodListActivity.this).create();
                alertDialog.setTitle("Chosen food");
                alertDialog.setMessage(MessageFormat.format("Food: {0}", name));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                        });
                alertDialog.show();
            }
        });

        // button on click
        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make adaptor
                Cursor csr = mDb.rawQuery("SELECT * from foods;", null);

                String[] fromColumns = {
                        FoodDbColumns.COLUMN_NAME_COL1,
                        FoodDbColumns.COLUMN_NAME_COL2,
                        FoodDbColumns.COLUMN_NAME_COL3,
                        FoodDbColumns.COLUMN_NAME_COL4,
                        FoodDbColumns.COLUMN_NAME_COL5,
                        FoodDbColumns.COLUMN_NAME_COL6,
                        FoodDbColumns.COLUMN_NAME_COL7,
                        FoodDbColumns.COLUMN_NAME_COL8
                };
                int[] toViews = {R.id._id, R.id.foodName, R.id.unit, R.id.fat, R.id.carbs, R.id.protein,
                                 R.id.cals, R.id.proteinCalRatio};
                mAdapter = new SimpleCursorAdapter(
                    getApplicationContext(),
                    R.layout.listview_row,
                    csr,
                    fromColumns,
                    toViews,
                    0
                );

                // get and display listview
                listView.setBackgroundColor(Color.BLACK);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//                listView.setAdapter(mAdapter);
                setListAdapter(mAdapter);
            }
        });

        FloatingActionButton closeButton = (FloatingActionButton) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
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

        return super.onOptionsItemSelected(item);
    }
}
