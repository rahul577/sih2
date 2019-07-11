package com.example.rahul.sih;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by rahul on 11/7/19.
 */


    public class dialog_ota extends AppCompatDialogFragment {
        private EditText editTextIp;
        private OtaDialogListener listener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_ota, null);

            builder.setView(view)
                    .setTitle("Add sensor")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String ip = editTextIp.getText().toString();

                            listener.applyOtaTexts(ip);
                        }
                    });

            editTextIp = view.findViewById(R.id.ip);

            return builder.create();
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            try {
                listener = (OtaDialogListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() +
                        "must implement ExampleDialogListener");
            }
        }

        public interface OtaDialogListener {
            void applyOtaTexts(String ip);
        }
    }