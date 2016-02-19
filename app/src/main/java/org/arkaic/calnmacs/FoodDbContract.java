package org.arkaic.calnmacs;

import android.provider.BaseColumns;

/**
 * Created by arkaic on 2/18/16.
 */
public final class FoodDbContract {

    public static final String DATABASE_NAME = "fooddatabase.db";
    public static final int DATABASE_VERSION = 1;

    public FoodDbContract() {}

    public static abstract class FoodDbColumns implements BaseColumns {
        public static final String TABLE_NAME       = "foods";
        public static final String COLUMN_NAME_COL1 = "name";
        public static final String COLUMN_NAME_COL2 = "unit";
        public static final String COLUMN_NAME_COL3 = "fat";
        public static final String COLUMN_NAME_COL4 = "carbs";
        public static final String COLUMN_NAME_COL5 = "protein";
        public static final String COLUMN_NAME_COL6 = "cals";
        public static final String COLUMN_NAME_COL7 = "proteinCalRatio";
    }
}
