package com.codepath.vijay.getfeed;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.codepath.vijay.getfeed.Activity.SearchActivity;

/**
 * Created by uttamavillain on 2/10/16.
 */
public class FilterDialog extends DialogFragment {

    private int year;
    private int month;
    private int date;
    private boolean oldestFirst;

    private Button btFilterDate;
    private Button btFilterSave;
    private RadioButton rbOldest, rbNewest;
    private CheckBox cbArts, cbFashionStyle, cbSports;
    private boolean isArts, isFashionStyle, isSports;

    public FilterDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterDialog newInstance(int year,
                                           int month,
                                           int date,
                                           boolean oldestFirst,
                                           boolean isArts,
                                           boolean isFashionStyle,
                                           boolean isSports) {
        FilterDialog frag = new FilterDialog();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("date", date);
        args.putBoolean("oldestFirst", oldestFirst);
        args.putBoolean("isarts", isArts);
        args.putBoolean("isfashionstyle", isFashionStyle);
        args.putBoolean("issports", isSports);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVeiws(view);
        // Fetch arguments from bundle and set title
        year = getArguments().getInt("year");
        month = getArguments().getInt("month");
        date = getArguments().getInt("date");
        oldestFirst = getArguments().getBoolean("oldestFirst");
        isArts = getArguments().getBoolean("isarts");
        isFashionStyle = getArguments().getBoolean("isfashionstyle");
        isSports = getArguments().getBoolean("issports");

        getDialog().setTitle("Filters");

        rbOldest.setChecked(oldestFirst);
        rbNewest.setChecked(!oldestFirst);

        cbArts.setChecked(isArts);
        cbFashionStyle.setChecked(isFashionStyle);
        cbSports.setChecked(isSports);

        btFilterDate.setText(year+"/"+String.format("%02d", month)+"/"+String.format("%02d", date));

        initListeners(view);
    }

    private void initListeners(View view) {
        btFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        btFilterSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveValues();
            }
        });

        cbArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                isArts = ((CheckBox) v).isChecked();
            }
        });

        cbFashionStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                isFashionStyle = ((CheckBox) v).isChecked();
            }
        });

        cbSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                isSports = ((CheckBox) v).isChecked();
            }
        });
    }

    private void initVeiws(View view) {
        // Get field from view
        btFilterDate = (Button) view.findViewById(R.id.btFilterDate);
        btFilterSave = (Button) view.findViewById(R.id.btFilterSave);
        rbOldest = (RadioButton) view.findViewById(R.id.rboldest);
        rbNewest = (RadioButton) view.findViewById(R.id.rbnewest);
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        cbFashionStyle = (CheckBox) view.findViewById(R.id.cbFasionStyle);
        cbSports = (CheckBox) view.findViewById(R.id.cbSports);
    }

    private void saveValues() {
        oldestFirst = rbOldest.isChecked();
        ((SearchActivity)getActivity()).refresh(year, month, date, oldestFirst, isArts, isFashionStyle, isSports);
        dismiss();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month-1);
        args.putInt("date", date);
        newFragment.setArguments(args);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void updateDate(int year, int month, int date) {
        this.date = date;
        this.month = month;
        this.year = year;
        btFilterDate.setText(year+"/"+String.format("%02d", month)+"/"+String.format("%02d", date));
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Activity needs to implement this interface
            DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), listener,
                    getArguments().getInt("year"),
                    getArguments().getInt("month"),
                    getArguments().getInt("date"));
        }
    }
}
