package org.arkaic.calnmacs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


/**
 * Created by henry on 2/16/16.
 */
public class FoodDbHelper extends SQLiteAssetHelper {

    public FoodDbHelper(Context c) {
        super(c, FoodDbContract.DATABASE_NAME, null, FoodDbContract.VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int j) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {

    }
}
