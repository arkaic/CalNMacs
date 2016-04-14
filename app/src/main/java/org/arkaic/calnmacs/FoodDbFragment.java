package org.arkaic.calnmacs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.MessageFormat;

import org.arkaic.calnmacs.FoodDbContract.FoodDbColumns;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFoodDbFragmentInteractionListener}
 * interface.
 */
public class FoodDbFragment extends ListFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private SQLiteDatabase mDb;
    private OnFoodDbFragmentInteractionListener mListener;

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
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.main_toolbar);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final ListView listView = (ListView)view.findViewById(android.R.id.list);

        /* -----------------------------------------------------------------------------------------
         *                                     LISTVIEW CONFIG
         * -----------------------------------------------------------------------------------------
         */

        mCursor = mDb.rawQuery("SELECT * from foods;", null);
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
        int[] toViews = {R.id._id, R.id.foodName, R.id.unit, R.id.fat, R.id.carbs, R.id.protein,
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
        listView.setBackgroundColor(Color.BLACK);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(mAdapter);
        // Pad toolbar
        View padding = new View(getActivity());
        padding.setMinimumHeight(150);
        listView.addHeaderView(padding);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // view is the toplevel object type defined in the table row xml
                TableRow foodRow = ((TableRow) view.findViewById(R.id.foodRow));
                CharSequence id_ = ((TextView) foodRow.findViewById(R.id._id)).getText();
                CharSequence name = ((TextView) foodRow.findViewById(R.id.foodName)).getText();
                CharSequence unit = ((TextView) foodRow.findViewById(R.id.unit)).getText();
                CharSequence carbs = ((TextView) foodRow.findViewById(R.id.carbs)).getText();
                CharSequence fat = ((TextView) foodRow.findViewById(R.id.fat)).getText();
                CharSequence prot = ((TextView) foodRow.findViewById(R.id.protein)).getText();
                CharSequence cals = ((TextView) foodRow.findViewById(R.id.cals)).getText();
                CharSequence protcals = ((TextView) foodRow.findViewById(R.id.proteinCalRatio)).getText();

                // Bring up message dialog displaying information
                if (getActivity() != null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle(name);
                    alertDialog.setMessage(MessageFormat.format(
                            "ID: {0}\nFood: {1}\nunit: {2}\ncarbs: {3}\nfat: {4}\nprotein: {5}\ncalories: {6}\nprotein->cal ratio: {7}",
                            id_, name, unit, carbs, fat, prot, cals, protcals));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });


        /* -----------------------------------------------------------------------------------------
         *                                     BUTTON CONFIG
         * -----------------------------------------------------------------------------------------
         */

        FloatingActionButton refresh = (FloatingActionButton) getView().findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make adaptor
                mAdapter.changeCursor(mDb.rawQuery("SELECT * FROM foods;", null));
                mAdapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton closeButton = (FloatingActionButton)getView().findViewById(R.id.quit);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) getView().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open a dialogue for adding custom food
            }
        });
    }


        @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFoodDbFragmentInteractionListener) {
            mListener = (OnFoodDbFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFoodDbFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFoodDbFragmentInteractionListener {
        void onFoodDbFragmentInteraction(Uri uri);
    }
}
