package org.arkaic.calnmacs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class FoodDbHelper extends SQLiteAssetHelper {

    private static FoodDbHelper mInstance;

    public static synchronized FoodDbHelper getInstance(Context c) {
        if (mInstance == null)
            mInstance = new FoodDbHelper(c.getApplicationContext());
        return mInstance;
    }

    private FoodDbHelper(Context c) {
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
