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
        public static final String ID_COLUMN = "_id";
        public static final String FOOD_NAME_COLUMN = "foodName";
        public static final String UNIT_COLUMN = "unit";
        public static final String FAT_COLUMN = "fat";
        public static final String CARBS_COLUMN = "carbs";
        public static final String PROTEIN_COLUMN = "protein";
        public static final String CALS_COLUMN = "cals";
        public static final String RATIO_COLUMN = "proteinCalRatio";
    }
}
