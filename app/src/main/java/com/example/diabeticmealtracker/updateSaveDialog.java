package com.example.diabeticmealtracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class updateSaveDialog extends AppCompatDialogFragment {
    // variables
    private updateSaveDialog.updateSaveDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // MyDiaLogTheme is found in themes.xml
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.updatesave_dialog, null);

        builder.setView(view)
                .setTitle("Update Food")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean update = false;
                        listener.update(update);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean update = true;
                        listener.update(update);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (updateSaveDialog.updateSaveDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement updateSaveDialogListener");
        }
    }

    public interface updateSaveDialogListener{
        void update(boolean update);
    }
}
