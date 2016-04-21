package org.arkaic.calnmacs;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.io.Serializable;
import java.text.MessageFormat;

import org.arkaic.calnmacs.FoodDbContract.FoodDbColumns;

public class Food implements Serializable {

    private int mId = -1;
    private String mName = null;
    private String mUnit = null;
    private double mProteinRatio = -1;
    private double mFat = -1;
    private double mCarbs = -1;
    private double mProtein = -1;
    private double mCals = -1;
    private int mAmount = -1;

    public Food(String foodName, int amount, SQLiteDatabase db) {
        mName = foodName;
        mAmount = amount;
        String query = "SELECT * FROM "+FoodDbColumns.TABLE_NAME+" WHERE "+FoodDbColumns.FOOD_NAME_COLUMN+"=?";
        Cursor cursor = db.rawQuery(query, new String[] {mName});
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String column = cursor.getColumnName(i);
                String item = cursor.getString(i);
                switch (column) {
                    case FoodDbColumns.ID_COLUMN:
                        mId = Integer.parseInt(item); break;
                    case FoodDbColumns.UNIT_COLUMN:
                        mUnit = item; break;
                    case FoodDbColumns.RATIO_COLUMN:
                        mProteinRatio = Double.parseDouble(item); break;
                    case FoodDbColumns.PROTEIN_COLUMN:
                        mProtein = Double.parseDouble(item); break;
                    case FoodDbColumns.FAT_COLUMN:
                        mFat = Double.parseDouble(item); break;
                    case FoodDbColumns.CARBS_COLUMN:
                        mCarbs = Double.parseDouble(item); break;
                    case FoodDbColumns.CALS_COLUMN:
                        mCals = Double.parseDouble(item); break;
                }
            }
        } else {
            // TODO should throw an exception here
        }
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }
    public int amount() {
        return mAmount;
    }
    public int fat() {
        return (int)(mFat * mAmount);
    }
    public int carbs() {
        return (int)(mCarbs * mAmount);
    }
    public int protein() {
        return (int)(mProtein * mAmount);
    }
    public int calories() {
        return (int)(mCals * mAmount);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}  {1} {2}\nFat:{3}g  Carbs:{4}g  Protein:{5}g\n{6} kcals",
                mName, mAmount, mUnit, fat(), carbs(), protein(), calories());
    }
}
