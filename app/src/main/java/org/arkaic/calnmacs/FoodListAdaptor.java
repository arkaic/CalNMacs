package org.arkaic.calnmacs;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

/**
 * Created by henry on 2/25/16.
 */
public class FoodListAdaptor extends SimpleCursorAdapter {

    private Context mContext;
    private int mLayout;

    public FoodListAdaptor(Context context, int layout, Cursor cursor, String[] fromCols, int[] toViews, int flags) {
        super(context, layout, cursor, fromCols, toViews, flags);
        this.mContext = context;
        this.mLayout = layout;
    }

    @Override
    public View newView(Context ctx, Cursor csr, ViewGroup parent) {
        Cursor c = getCursor();
        final LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(mLayout, parent, false);

        // TODO:layout - set text views
        return v;
    }

//    @Override
//    public void bindView(View v, Context ctx, Cursor csr) {
//    }
}

