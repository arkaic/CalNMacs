package org.arkaic.calnmacs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


/**
 * Created by henry on 2/16/16.
 */
public class FoodDbHelper extends SQLiteAssetHelper {

    public FoodDbHelper(Context c) {
        super(c, FoodDbContract.DATABASE_NAME, null, FoodDbContract.DATABASE_VERSION);
        setForcedUpgrade();  // Forces db overwrite if database version is incremented in Contract class
    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int i, int j) {
//
//    }
//
//    @Override
//    public void onOpen(SQLiteDatabase db) {
//
//    }
}
