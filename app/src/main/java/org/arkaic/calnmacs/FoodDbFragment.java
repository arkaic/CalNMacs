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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.MessageFormat;

import org.arkaic.calnmacs.dummy.DummyContent.DummyItem;
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
    private OnFoodDbFragmentInteractionListener mListener;
    private SQLiteDatabase mDb;

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
        if (getArguments() != null)
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        mDb = (new FoodDbHelper(getActivity().getApplicationContext())).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fooddb, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final ListView listView = (ListView)view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // view is the toplevel object type defined in the table row xml
                TableRow foodRow = ((TableRow)view.findViewById(R.id.foodRow));
                CharSequence id_ = ((TextView)foodRow.findViewById(R.id._id)).getText();
                CharSequence name = ((TextView)foodRow.findViewById(R.id.foodName)).getText();
                CharSequence unit = ((TextView)foodRow.findViewById(R.id.unit)).getText();
                CharSequence carbs = ((TextView)foodRow.findViewById(R.id.carbs)).getText();
                CharSequence fat = ((TextView)foodRow.findViewById(R.id.fat)).getText();
                CharSequence prot = ((TextView)foodRow.findViewById(R.id.protein)).getText();
                CharSequence cals = ((TextView)foodRow.findViewById(R.id.cals)).getText();
                CharSequence protcals = ((TextView)foodRow.findViewById(R.id.proteinCalRatio)).getText();

                // Bring up message dialog displaying information
                if (getActivity() != null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Chosen food");
                    alertDialog.setMessage(MessageFormat.format(
                            "ID: {0}\nFood: {1}\nunit: {2}\ncarbs: {3}\nfat: {4}\nprotein: {5}\ncalories: {6}\nprotein->cal ratio: {7}",
                            id_, name, unit, carbs, fat, prot, cals, protcals));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { dialog.dismiss();}
                            });
                    alertDialog.show();
                }
            }
        });

        // button on click
        FloatingActionButton refresh = (FloatingActionButton) getView().findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make adaptor
                Cursor csr = mDb.rawQuery("SELECT * from foods;", null);

                String[] fromColumns = {
                        FoodDbColumns.COLUMN_NAME_COL1,
                        FoodDbColumns.COLUMN_NAME_COL2,
                        FoodDbColumns.COLUMN_NAME_COL3,
                        FoodDbColumns.COLUMN_NAME_COL4,
                        FoodDbColumns.COLUMN_NAME_COL5,
                        FoodDbColumns.COLUMN_NAME_COL6,
                        FoodDbColumns.COLUMN_NAME_COL7,
                        FoodDbColumns.COLUMN_NAME_COL8
                };
                int[] toViews = {R.id._id, R.id.foodName, R.id.unit, R.id.fat, R.id.carbs, R.id.protein,
                        R.id.cals, R.id.proteinCalRatio};
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        getActivity().getApplicationContext(),
                        R.layout.listview_row,
                        csr,
                        fromColumns,
                        toViews,
                        0
                );

                // get and display listview
                listView.setBackgroundColor(Color.BLACK);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setAdapter(adapter);
            }
        });

        FloatingActionButton closeButton = (FloatingActionButton)getView().findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) getView().findViewById(R.id.addButton);
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