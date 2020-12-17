package com.example.diabeticmealtracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class newSaveDialog extends AppCompatDialogFragment {

    // variables
    private newSaveDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // MyDiaLogTheme is found in themes.xml
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.newsave_dialog, null);

        builder.setView(view)
                .setTitle("Save New Food")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean save = false;
                        listener.save(save);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean save = true;
                        listener.save(save);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (newSaveDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement newSaveDialogListener");
        }
    }

    public interface newSaveDialogListener{
        void save(boolean save);
    }
}
