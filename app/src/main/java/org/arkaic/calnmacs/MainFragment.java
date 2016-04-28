package org.arkaic.calnmacs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
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

    private MainListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private OnMainFragmentInteractionListener mListener;
    private List<Food> mFoodsEaten = new ArrayList<>();
    private String mSelectedSpinnerFood = null;
    private double mTotalCals = 0;
    private double mTotalFat = 0;
    private double mTotalCarbs = 0;
    private double mTotalProtein = 0;

    private int MAX_PICKER_VALUE = 1000;
    private String SAVED_DATA_FILENAME = "saveddata.ser";
//    private double STEP_PICKER_VALUE = 0.5;

    public MainFragment() {}

    public static MainFragment newInstance(int position) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("something", 7521);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.main_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        mDb = (FoodDbHelper.getInstance(getActivity().getApplicationContext())).getWritableDatabase();

        // Restore saved food choices and cal&mac totals
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(getContext().openFileInput(SAVED_DATA_FILENAME));
            mFoodsEaten = (List)ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            mFoodsEaten = new ArrayList<>();
        }
        for (Food food : mFoodsEaten) {
            mTotalCals += food.calories();
            mTotalProtein += food.protein();
            mTotalFat += food.fat();
            mTotalCarbs += food.carbs();
        }
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
         *                                     MAIN LISTVIEW CONFIG
         * -----------------------------------------------------------------------------------------
         */

        // ArrayAdapter to map simple string arrays to
        mAdapter = new MainListAdapter(getActivity(), (List)mFoodsEaten);
        final ListView listView = (ListView)view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
                    final List<String> foodNames = new ArrayList<>();
                    Cursor foodNameCursor = mDb.rawQuery(
                            "SELECT " + FoodDbColumns.FOOD_NAME_COLUMN + " FROM " + FoodDbColumns.TABLE_NAME,
                            null);
                    if (foodNameCursor.moveToFirst()) {
                        do { foodNames.add(foodNameCursor.getString(0));
                        } while (foodNameCursor.moveToNext());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getActivity(), android.R.layout.simple_list_item_1, foodNames);

                    // Listview of foods config
                    final ListView lv = (ListView)(dialogView.findViewById(android.R.id.list));
                    lv.setAdapter(adapter);
                    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mSelectedSpinnerFood = foodNames.get(position);
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
                            if (mSelectedSpinnerFood != null) {
                                int amount = picker.getValue();
                                Food selectedFood = new Food(mSelectedSpinnerFood, amount, mDb);
                                mFoodsEaten.add(selectedFood);
                                mAdapter.notifyDataSetChanged();
                                mTotalCals += selectedFood.calories();
                                mTotalProtein += selectedFood.protein();
                                mTotalCarbs += selectedFood.carbs();
                                mTotalFat += selectedFood.fat();
                                toolbar.setTitle(totalsString());
                                mSelectedSpinnerFood = null;
                            }
                        }
                    });

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    // Set fixed dialog size
                    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                    Display dis = wm.getDefaultDisplay();
                    Point pt = new Point();
                    dis.getSize(pt);
                    int width = pt.x;
                    int height = pt.y;
                    dialog.getWindow().setLayout(width, height - 700);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        FileOutputStream fos = null;
        try {
            fos = getContext().openFileOutput(SAVED_DATA_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mFoodsEaten);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMainFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        // before oncreate
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

    private Spanned totalsString() {
        String fatColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.fat_color) & 0x00ffffff);
        String carbColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.carb_color) & 0x00ffffff);
        String proteinColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.protein_color) & 0x00ffffff);
        String calColor = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.cal_color) & 0x00ffffff);
        String retVal = MessageFormat.format("<font color=#{0}>{1} cal</font>&nbsp;&nbsp;<font color=#{2}>{3}g</font>" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#{4}>{5}g</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "<font color=#{6}>{7}g</font>",
                calColor, mTotalCals, fatColor, mTotalFat, carbColor, mTotalCarbs, proteinColor, mTotalProtein);
        return Html.fromHtml(retVal);
    }
}
