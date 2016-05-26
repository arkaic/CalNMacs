package org.arkaic.calnmacs;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Objects;

import org.arkaic.calnmacs.FoodDbContract.FoodDbColumns;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link FoodDbFragmentListener}
 * interface.
 */
public class FoodDbFragment extends ListFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private SQLiteDatabase mDb;
    private FoodDbFragmentListener mFragmentListener;

    public FoodDbFragment() {}

    public static FoodDbFragment newInstance(int columnCount) {
        FoodDbFragment fragment = new FoodDbFragment();
        Bundle args = new Bundle();
        // set item position for this fragment
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    // assign database reference here
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.fooddb_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        if (getArguments() != null)
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        mDb = (FoodDbHelper.getInstance(getActivity().getApplicationContext())).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fooddb, container, false);
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        final Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.fooddb_toolbar);
        toolbar.setTitle("Food Unit    Carb    Fat    Protein   Cal     Ratio");
        toolbar.setTitleTextColor(Color.WHITE);

        /* -----------------------------------------------------------------------------------------
         *                                     LISTVIEW CONFIG
         * -----------------------------------------------------------------------------------------
         */

        mCursor = mDb.rawQuery("SELECT * FROM " + FoodDbColumns.TABLE_NAME + " ORDER BY " +
                FoodDbColumns.FOOD_NAME_COLUMN + " COLLATE NOCASE;", null);
        // column names
        String[] fromColumns = {
                FoodDbColumns.ID_COLUMN,
                FoodDbColumns.FOOD_NAME_COLUMN,
                FoodDbColumns.UNIT_COLUMN,
                FoodDbColumns.FAT_COLUMN,
                FoodDbColumns.CARBS_COLUMN,
                FoodDbColumns.PROTEIN_COLUMN,
                FoodDbColumns.CALS_COLUMN,
                FoodDbColumns.RATIO_COLUMN
        };
        // view id's defined in foodrow xml
        int[] toViews = {R.id.foodPk, R.id.foodName, R.id.unit, R.id.fat, R.id.carbs, R.id.protein,
                R.id.cals, R.id.proteinCalRatio};

        // SimpleCursorAdapter to map db cursor to listview
        mAdapter = new SimpleCursorAdapter(
                getActivity().getApplicationContext(),
                R.layout.foodrow_fooddb,
                mCursor,
                fromColumns,
                toViews,
                0
        );
        final ListView listView = (ListView)view.findViewById(android.R.id.list);
        listView.setBackgroundColor(Color.BLACK);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(mAdapter);
        // Pad toolbar
        View padding = new View(getActivity());
        padding.setMinimumHeight(150);
        listView.addHeaderView(padding);

        /* -----------------------------------------------------------------------------------------
         *                                   ON CLICKING A FOOD LISTENER
         *                                   (for modifiying or deleting foods)
         * -----------------------------------------------------------------------------------------
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Bring up a dialog for options to delete and/or modify the food
                if (getActivity() != null) {
                    // Retrieve food's db primary key in a hidden view within the ListView's TableRow
                    View hiddenView = view.findViewById(R.id.hidden_fooddb_layout);
                    final int foodid = Integer.parseInt(((TextView) hiddenView.findViewById(R.id.foodPk)).getText().toString());

                    // Retrieve the food infos
                    View foodRow = view.findViewById(R.id.foodRow);
                    final CharSequence originalName = ((TextView) foodRow.findViewById(R.id.foodName)).getText();
                    final CharSequence originalUnit = ((TextView) foodRow.findViewById(R.id.unit)).getText();
                    final CharSequence originalCarbs = ((TextView) foodRow.findViewById(R.id.carbs)).getText();
                    final CharSequence originalFat = ((TextView) foodRow.findViewById(R.id.fat)).getText();
                    final CharSequence originalProt = ((TextView) foodRow.findViewById(R.id.protein)).getText();
                    final CharSequence originalCals = ((TextView) foodRow.findViewById(R.id.cals)).getText();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    final View dialogView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_modrm_foodb, null);
                    builder.setView(dialogView);
                    builder.setTitle(originalName);

                    final EditText nameEdit = ((EditText)dialogView.findViewById(R.id.name_mod));
                    final EditText unitEdit = ((EditText)dialogView.findViewById(R.id.unit_type_mod));
                    final EditText carbEdit = ((EditText) dialogView.findViewById(R.id.carb_mod));
                    final EditText fatEdit = ((EditText) dialogView.findViewById(R.id.fat_mod));
                    final EditText proteinEdit = ((EditText) dialogView.findViewById(R.id.protein_mod));
                    final EditText calEdit = ((EditText) dialogView.findViewById(R.id.calorie_mod));

                    nameEdit.setHint("Name: " + originalName);
                    unitEdit.setHint("Unit type: " + originalUnit);
                    carbEdit.setHint("Carbs: " + originalCarbs);
                    fatEdit.setHint("Fat: " + originalFat);
                    proteinEdit.setHint("Protein: " + originalProt);
                    calEdit.setHint("Calories: " + originalCals);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CheckBox box = (CheckBox)dialogView.findViewById(R.id.delete_check);
                            if (box.isChecked()) {
                                mDb.delete(FoodDbColumns.TABLE_NAME, FoodDbColumns.ID_COLUMN + "=" + foodid, null);
                                refreshList();
                                mFragmentListener.onFoodDelete(foodid);  // reflect changes back to fooddb
                                // todo indicator of deletion
                            } else {
                                // TODO perform modifications if input. Change protein-calorie ratio
                                if (isInputDifferent()) {
                                    // TODO update food in database
                                    refreshList();
                                    // mFragmentListener.onFoodModify(foodid);
                                    // TODO indicator of modification
                                }

                            }
                        }

                        private boolean isInputDifferent() {
                            String newName = nameEdit.getText().toString();
                            String newUnit = unitEdit.getText().toString();
                            String newCarbs = carbEdit.getText().toString();
                            String newFat = fatEdit.getText().toString();
                            String newProtein = proteinEdit.getText().toString();
                            String newCals = calEdit.getText().toString();
                            // if input is not empty and not the same as original, change
                            if (!Objects.equals(newName, "") && !Objects.equals(newName, originalName) ||
                                    !Objects.equals(newUnit, "") && !Objects.equals(newUnit, originalUnit) ||
                                    !Objects.equals(newCarbs, "") && !Objects.equals(newCarbs, originalCarbs) ||
                                    !Objects.equals(newFat, "") && !Objects.equals(newFat, originalFat) ||
                                    !Objects.equals(newProtein, "") && !Objects.equals(newProtein, originalProt) ||
                                    !Objects.equals(newCals, "") && !Objects.equals(newCals, originalCals)) {
                                return true;
                            }
                            return false;
                        }
                    });

                    alertDialog.show();
                }
            }
        });


        /* -----------------------------------------------------------------------------------------
         *                                     ADD BUTTON CONFIG
         * -----------------------------------------------------------------------------------------
         */

        FloatingActionButton addButton = (FloatingActionButton) getView().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open a dialogue for adding custom food
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View diaView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_add_fooddb, null);
                final AlertDialog dia = builder.setView(diaView)
                        .setTitle("Add a new food to the food list")
                        .setPositiveButton(android.R.string.ok, null)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();

                dia.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        dia.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Check if any text fields are empty
                                String name = ((EditText)diaView.findViewById(R.id.name_add)).getText().toString();
                                String unit = ((EditText)diaView.findViewById(R.id.unit_type_add)).getText().toString();
                                String carbStr = ((EditText)diaView.findViewById(R.id.carb_add)).getText().toString();
                                String fatStr = ((EditText)diaView.findViewById(R.id.fat_add)).getText().toString();
                                String proteinStr = ((EditText)diaView.findViewById(R.id.protein_add)).getText().toString();
                                String calsStr = ((EditText)diaView.findViewById(R.id.calorie_add)).getText().toString();

                                // Retrieve values from input and add food to db only if ALL input
                                // fields are filled in, then close dialog
                                if (name.trim().length() == 0 || unit.trim().length() == 0 ||
                                        carbStr.trim().length() == 0 || fatStr.trim().length() == 0 ||
                                        proteinStr.trim().length() == 0 || calsStr.trim().length() == 0) {
                                    // TODO give a quick warning, but not a popup
                                } else {
                                    ContentValues cv = new ContentValues();
                                    cv.put(FoodDbColumns.FOOD_NAME_COLUMN, name);
                                    cv.put(FoodDbColumns.UNIT_COLUMN, unit);
                                    cv.put(FoodDbColumns.CARBS_COLUMN, Double.parseDouble(carbStr));
                                    cv.put(FoodDbColumns.FAT_COLUMN, Double.parseDouble(fatStr));
                                    cv.put(FoodDbColumns.PROTEIN_COLUMN, Double.parseDouble(proteinStr));
                                    cv.put(FoodDbColumns.CALS_COLUMN, Double.parseDouble(calsStr));
                                    cv.put(FoodDbColumns.RATIO_COLUMN,
                                            Double.parseDouble(proteinStr) * 4 / Double.parseDouble(calsStr));
                                    mDb.insert(FoodDbColumns.TABLE_NAME, null, cv);
                                    refreshList();
                                    dia.dismiss();
                                }
                            }
                        });
                    }
                });

                dia.show();
            }
        });
    }

    // Refresh the food database display
    private void refreshList() {
        mAdapter.changeCursor(mDb.rawQuery("SELECT * FROM " + FoodDbColumns.TABLE_NAME + " ORDER BY " +
                FoodDbColumns.FOOD_NAME_COLUMN + " COLLATE NOCASE;", null));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FoodDbFragmentListener) {
            mFragmentListener = (FoodDbFragmentListener)context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FoodDbFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        mFragmentListener = null;
        super.onDetach();
        mCursor.close();
    }

    @Override
    public void onStop() {
        super.onStop();
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
    public interface FoodDbFragmentListener {
        void onFoodDelete(int id);
    }
}
