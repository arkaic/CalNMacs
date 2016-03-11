package org.arkaic.calnmacs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

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
    private int mTotalCals = 0;
    private double mTotalFat = 0;
    private double mTotalCarbs = 0;
    private double mTotalProtein = 0;

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

        final ListView listView = (ListView)view.findViewById(android.R.id.list);
        mFoodsEaten.add("soy");
        mFoodsEaten.add("soy");
        mFoodsEaten.add("soy");
        mFoodsEaten.add("soy");
        mFoodsEaten.add("soy");
        mFoodsEaten.add("soy");
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFoodsEaten);
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

        FloatingActionButton addButton = (FloatingActionButton)getView().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open a menu/dropdown, load up db food list, choose food,
                // extrapolate that out of db and add to listview
                if (getActivity() != null) {
                    // Create dialog box and spinner for it
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_add_main, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setTitle("Add food");

                    Spinner spinner = (Spinner)dialogView.findViewById(R.id.food_spinner);
                    // TODO query from mdb and populate spinner
                    List<String> spinnerItems = new ArrayList<>();
                    Cursor foodNameCursor = mDb.rawQuery(
                            "SELECT " + FoodDbColumns.FOOD_NAME_COLUMN + " FROM foods",
                            null);
                    if (foodNameCursor.moveToFirst()) {
                        do {
                            spinnerItems.add(foodNameCursor.getString(0));
                        } while (foodNameCursor.moveToNext());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getActivity(), android.R.layout.simple_spinner_item, spinnerItems);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //TODO query from db and add to foods
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO idk
                        }
                    });

                    dialogBuilder.create().show();
                }

                // test adding
                mFoodsEaten.add("Haw Hamburger");
                mAdapter.notifyDataSetChanged();
                // TODO add this to adapter or listview directly?// make adaptor
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

//    private class MySpinnerAdapter extends ArrayAdapter implements SpinnerAdapter {
//        public MySpinnerAdapter() {
//            super
//        }
////        @Override
////        public View getDropDownView(int position, View convertView, ViewGroup parent) {
////            return null;
////        }
//    }
}
