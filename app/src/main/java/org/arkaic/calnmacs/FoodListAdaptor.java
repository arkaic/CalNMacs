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

    public FoodListAdaptor(Context context, int layout, Cursor cursor, String[] fromCols, int[] toViews, int flags) {
        super(context, layout, cursor, fromCols, toViews, flags);
    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View v = convertView;
//
//        if (v == null) {
//            LayoutInflater vi;
//            vi = LayoutInflater.from(parent.getContext());
//            v = vi.inflate(R.layout.listview_row, null);
//        }
//
//        ClipData.Item item = getItem(position);
//        return null;
//    }

    @Override
    public View newView(Context ctx, Cursor csr, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View v, Context ctx, Cursor csr) {
    }
}

