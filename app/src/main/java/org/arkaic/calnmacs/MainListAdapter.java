package org.arkaic.calnmacs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by henry on 3/11/16.
 */
public class MainListAdapter extends ArrayAdapter<Food> {

    private List<Food> objects;
    private LayoutInflater inflater;

    public MainListAdapter(Context context, List<Food> objects) {
        super(context, 0, objects);
        this.objects = objects;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        Food item = getItem(position);
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.foodrow_main, parent, false);

        if (item != null) {
            TextView foodNameView = (TextView)v.findViewById(R.id.foodNameMain);
            TextView unitView = (TextView)v.findViewById(R.id.unitMain);
            TextView fatView = (TextView)v.findViewById(R.id.fatMain);
            TextView carbsView = (TextView)v.findViewById(R.id.carbsMain);
            TextView proteinView = (TextView)v.findViewById(R.id.proteinMain);
            TextView calsView = (TextView)v.findViewById(R.id.calsMain);
            TextView ratioView = (TextView)v.findViewById(R.id.proteinCalRatioMain);

            foodNameView.setText(item.name());
            unitView.setText(item.unit());
            fatView.setText((Integer.toString(item.fat())) + "g");
            carbsView.setText(Integer.toString(item.carbs()) + "g");
            proteinView.setText(Integer.toString(item.protein()) + "g");
            calsView.setText(Integer.toString(item.calories()) + " kcals");
            ratioView.setText(Double.toString(item.proteinToCalRatio()) + "%");
        }
        return v;
    }
}
