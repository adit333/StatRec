package com.DivineInspiration.experimenter.Activity.ui.profile;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.DivineInspiration.experimenter.R;


public class editProfileDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage("Woahhh dude it works")
                .setPositiveButton("ok",null)
                .setNegativeButton("cancel",null)
                .create();
    }
    public static String TAG = "editProfileDialog";
}