package org.arkaic.calnmacs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arkaic.calnmacs.FoodDbContract.FoodDbColumns;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMainFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends ListFragment {

    private ArrayAdapter<String> mAdapter;
    private SQLiteDatabase mDb;
    private OnMainFragmentInteractionListener mListener;
    private ArrayList<String> mFoodsEaten = new ArrayList<>();
    private String mSelectedSpinnerFood = null;
    private int mTotalCals = 0;
    private double mTotalFat = 0;
    private double mTotalCarbs = 0;
    private double mTotalProtein = 0;

    private int MAX_PICKER_VALUE = 1000;
//    private double STEP_PICKER_VALUE = 0.5;

    public MainFragment() {}

    public static MainFragment newInstance(int position) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.main_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        mDb = (new FoodDbHelper(getActivity().getApplicationContext())).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        // todo need to subclass arrayadapter to have it output multiple columns
        // http://stackoverflow.com/questions/11678909/use-array-adapter-with-more-views-in-row-in-listview

        ((Toolbar)getActivity().findViewById(R.id.main_toolbar)).setTitle(totalsString());
        ((Toolbar)getActivity().findViewById(R.id.main_toolbar)).setTitleTextColor(Color.WHITE);

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFoodsEaten);
        final ListView listView = (ListView)view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // TODO bring up dialog for edit numbers or delete
                if (getActivity() != null) {
                    new AlertDialog.Builder(getActivity())
                            .setPositiveButton("Change amount", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // TODO
                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // TODO use position
                                    mFoodsEaten.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                }
                            })
                            .show();
                }
            }
        });

        /* -----------------------------------------------------------------------------------------
         *                                     ADD FOOD BUTTON
         * -----------------------------------------------------------------------------------------
         */
        FloatingActionButton addButton = (FloatingActionButton)getView().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    // Create dialog box
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_add_main, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setTitle("Add food");

                    // Populate list with food names and make adapter for spinner
                    final List<String> spinnerItems = new ArrayList<>();
                    Cursor foodNameCursor = mDb.rawQuery(
                            "SELECT " + FoodDbColumns.FOOD_NAME_COLUMN + " FROM " + FoodDbColumns.TABLE_NAME,
                            null);
                    if (foodNameCursor.moveToFirst()) {
                        do { spinnerItems.add(foodNameCursor.getString(0));
                        } while (foodNameCursor.moveToNext());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getActivity(), android.R.layout.simple_spinner_item, spinnerItems);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Spinner config
                    Spinner spinner = (Spinner)dialogView.findViewById(R.id.food_spinner);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //TODO query from db and add to foods
                            mSelectedSpinnerFood = spinnerItems.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            mSelectedSpinnerFood = spinnerItems.get(0);
                        }
                    });

                    // Number picker config
                    final NumberPicker picker = (NumberPicker)dialogView.findViewById(R.id.amount_number_picker);
                    picker.setMinValue(0);
                    picker.setMaxValue(MAX_PICKER_VALUE);
                    picker.setWrapSelectorWheel(false);

                    // OK button config
                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int amount = picker.getValue();
                            Cursor cursor = mDb.rawQuery(
                                "SELECT * FROM " + FoodDbColumns.TABLE_NAME + " WHERE " +
                                        FoodDbColumns.FOOD_NAME_COLUMN + "=\"" +
                                        mSelectedSpinnerFood + "\"",
                                null);

                            // rowStr represents the listview row displaying chosen food's data
                            String rowStr = "";
                            if (cursor.moveToFirst()) {
                                String colName;
                                String item;
                                for (int i = 0; i < cursor.getColumnCount(); i++) {
                                    colName = cursor.getColumnName(i);
                                    item = cursor.getString(i);
                                    if (colName.equals(FoodDbColumns.ID_COLUMN))
                                        continue;
                                    switch (colName) {
                                        case FoodDbColumns.FAT_COLUMN:
                                            mTotalFat += Double.parseDouble(item); break;
                                        case FoodDbColumns.CARBS_COLUMN:
                                            mTotalCarbs += Double.parseDouble(item); break;
                                        case FoodDbColumns.PROTEIN_COLUMN:
                                            mTotalProtein += Double.parseDouble(item); break;
                                        case FoodDbColumns.CALS_COLUMN:
                                            mTotalCals += Double.parseDouble(item); break;
                                    }
                                    rowStr += " " + item + FoodDbColumns.COLUMN_NAME_MAP.get(colName);
                                    if (colName.equals(FoodDbColumns.FOOD_NAME_COLUMN))
                                        rowStr += "\n";
                                }
                            } else {
                                rowStr = "UNEXPLAINED ERROR, DATABASE QUERY RETURNED NOTHING";
                            }
                            mFoodsEaten.add(rowStr);
                            mAdapter.notifyDataSetChanged();
                            ((Toolbar)getActivity().findViewById(R.id.main_toolbar)).setTitle(totalsString());
                        }
                    });

                    dialogBuilder.create().show();
                }
            }
        });

        FloatingActionButton clearAllButton = (FloatingActionButton)getView().findViewById(R.id.clearAll);
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFoodsEaten.clear();
                mAdapter.notifyDataSetChanged();
                mTotalCals = 0;
                mTotalCarbs = 0;
                mTotalFat = 0;
                mTotalProtein = 0;
            }
        });

        FloatingActionButton quitButton = (FloatingActionButton)getView().findViewById(R.id.quit);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMainFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMainFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMainFragmentInteraction(Uri uri);
    }

    private String totalsString() {
        String retVal = "Fat:" + mTotalFat + "  Carbs:" + mTotalCarbs + "  Protein:" + mTotalProtein + "  Calories:" + mTotalCals;
        return retVal;
    }

    private class CustomArrayAdapter extends ArrayAdapter {

        private List<String> mFoodList;

        public CustomArrayAdapter(Context context, int resource, List<String> foodsEaten) {
            super(context, resource, foodsEaten);
            mFoodList = foodsEaten;
        }
    }
}
