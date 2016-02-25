package org.arkaic.calnmacs;

import android.provider.BaseColumns;

/**
 * Created by arkaic on 2/18/16.
 */
public final class FoodDbContract {

    public static final String DATABASE_NAME = "food.db";
    public static final int DATABASE_VERSION = 4;

    public FoodDbContract() {}

    public static abstract class FoodDbColumns implements BaseColumns {
        public static final String TABLE_NAME       = "foods";
        public static final String COLUMN_NAME_COL1 = "_id";
        public static final String COLUMN_NAME_COL2 = "foodName";
        public static final String COLUMN_NAME_COL3 = "unit";
        public static final String COLUMN_NAME_COL4 = "fat";
        public static final String COLUMN_NAME_COL5 = "carbs";
        public static final String COLUMN_NAME_COL6 = "protein";
        public static final String COLUMN_NAME_COL7 = "cals";
        public static final String COLUMN_NAME_COL8 = "proteinCalRatio";
    }
}
