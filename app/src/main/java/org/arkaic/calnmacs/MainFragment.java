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

import java.text.MessageFormat;
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
    private List<Food> mFoodsEaten = new ArrayList<>();
    private String mSelectedSpinnerFood = null;
    private double mTotalCals = 0;
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

        /* -----------------------------------------------------------------------------------------
         *                                     TOOLBAR CONFIG
         * -----------------------------------------------------------------------------------------
         */
        final Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.main_toolbar);
        toolbar.setTitle(totalsString());
        toolbar.setTitleTextColor(Color.WHITE);

        /* -----------------------------------------------------------------------------------------
         *                                     LISTVIEW CONFIG
         * -----------------------------------------------------------------------------------------
         */

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, (List)mFoodsEaten);
        final ListView listView = (ListView)view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(mAdapter);
        View padding = new View(getActivity());
        padding.setMinimumHeight(150);
        listView.addHeaderView(padding);

        /* -----------------------------------------------------------------------------------------
         *                                     FOOD CLICKED CONFIG
         * -----------------------------------------------------------------------------------------
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (getActivity() != null) {
                    final int index = position - 1;
                    final Food selectedFood = mFoodsEaten.get(index);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_edit_main, null);
                    builder.setView(dialogView);
                    builder.setTitle("Edit Food");

                    final NumberPicker picker = (NumberPicker)dialogView.findViewById(R.id.edit_amount_number_picker);
                    picker.setMinValue(0);
                    picker.setMaxValue(MAX_PICKER_VALUE);
                    picker.setWrapSelectorWheel(false);
                    picker.setValue(selectedFood.amount());

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int newAmount = picker.getValue();
                            if (newAmount != selectedFood.amount()) {
                                mTotalCals -= selectedFood.calories();
                                mTotalProtein -= selectedFood.protein();
                                mTotalCarbs -= selectedFood.carbs();
                                mTotalFat -= selectedFood.fat();
                                selectedFood.setAmount(newAmount);
                                mTotalCals += selectedFood.calories();
                                mTotalProtein += selectedFood.protein();
                                mTotalCarbs += selectedFood.carbs();
                                mTotalFat += selectedFood.fat();
                                mAdapter.notifyDataSetChanged();
                                toolbar.setTitle(totalsString());
                            }
                        }
                    });

                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mTotalCals -= selectedFood.calories();
                            mTotalProtein -= selectedFood.protein();
                            mTotalCarbs -= selectedFood.carbs();
                            mTotalFat -= selectedFood.fat();
                            mFoodsEaten.remove(index);
                            mAdapter.notifyDataSetChanged();
                            toolbar.setTitle(totalsString());
                        }
                    });
                    builder.show();
                }
            }
        });

        /* -----------------------------------------------------------------------------------------
         *                                     ADD FOOD BUTTON CONFIG
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
                            Food selectedFood = new Food(mSelectedSpinnerFood, amount);
                            mFoodsEaten.add(selectedFood);
                            mAdapter.notifyDataSetChanged();
                            mTotalCals += selectedFood.calories();
                            mTotalProtein += selectedFood.protein();
                            mTotalCarbs += selectedFood.carbs();
                            mTotalFat += selectedFood.fat();
                            toolbar.setTitle(totalsString());
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
                mTotalCals = 0;
                mTotalCarbs = 0;
                mTotalFat = 0;
                mTotalProtein = 0;
                toolbar.setTitle(totalsString());
                mAdapter.notifyDataSetChanged();
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
        String retVal = mTotalCals + " kcals: " + mTotalFat + "g F   " + mTotalCarbs + "g C   " + mTotalProtein + "g P";
        return retVal;
    }

    /*
       One instance of Food per Listview row in this fragment.
     */
    private class Food {

        private int mId;
        private String mName;
        private String mUnit;
        private double mProteinRatio;
        private double mFat;
        private double mCarbs;
        private double mProtein;
        private double mCals;
        private int mAmount;

        public Food(String foodName, int amount) {
            mName = foodName;
            mAmount = amount;
            Cursor cursor = mDb.rawQuery(
                    "SELECT * FROM " + FoodDbColumns.TABLE_NAME + " WHERE " +
                            FoodDbColumns.FOOD_NAME_COLUMN + "=\"" +
                            mSelectedSpinnerFood + "\"",
                    null);
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
}
