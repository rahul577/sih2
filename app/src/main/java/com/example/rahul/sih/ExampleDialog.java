package com.example.rahul.sih;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;

/**
 * Created by rahul on 9/7/19.
 */

public class ExampleDialog extends AppCompatDialogFragment {

    RulerValuePicker maxValuePicker, minValuePicker;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        maxValuePicker = view.findViewById(R.id.max_ruler_picker);
        minValuePicker = view.findViewById(R.id.min_ruler_picker);

        maxValuePicker.getCurrentValue();

        builder.setView(view)
                .setTitle("Set threshold")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String max_value = String.valueOf(maxValuePicker.getCurrentValue());
                        String min_value = String.valueOf(minValuePicker.getCurrentValue());

                        listener.applyTexts(max_value, min_value);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String max_value, String min_value);
    }

}