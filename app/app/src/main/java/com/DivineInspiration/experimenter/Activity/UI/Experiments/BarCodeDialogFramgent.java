package com.DivineInspiration.experimenter.Activity.UI.Experiments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.DivineInspiration.experimenter.R;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarCodeDialogFramgent extends DialogFragment {


    String params;
    boolean allowCamera = false;
    boolean openCamera = false;
    CodeScannerView scannerView;
    Button scan;
    private CodeScanner mCodeScanner;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.scan_fragment, container);

        params = getArguments().getString("message");

        scannerView = view.findViewById(R.id.scanner);
        scan = view.findViewById(R.id.scanButton);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if camera is open
                if (allowCamera) {
                    try
                    {
                        Thread.sleep(20);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    mCodeScanner.startPreview();
                } else {
                    Toast.makeText(getActivity(), "A code cannot be scanned if the camera is off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // check camera permissions
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
            mCodeScanner.startPreview();
        } else {
            // request camera permissions
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CAMERA}, 401);
        }

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this.getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void openCamera() {
        // from https://www.youtube.com/watch?v=drH63NpSWyk by Code Palace
        // (https://www.youtube.com/channel/UCuudpdbKmQWq2PPzYgVCWlA)
        if (openCamera) {
            return;
        }
        openCamera = true;
        allowCamera = true;

        mCodeScanner = new CodeScanner(getActivity(), scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // do something after scanning

                        if (result.getBarcodeFormat() == BarcodeFormat.UPC_A // check if it is 1d bar code
                                || result.getBarcodeFormat() == BarcodeFormat.UPC_E
                                || result.getBarcodeFormat() == BarcodeFormat.EAN_8
                                || result.getBarcodeFormat() == BarcodeFormat.EAN_13
                                || result.getBarcodeFormat() == BarcodeFormat.UPC_EAN_EXTENSION
                                || result.getBarcodeFormat() == BarcodeFormat.CODE_39
                                || result.getBarcodeFormat() == BarcodeFormat.CODE_93
                                || result.getBarcodeFormat() == BarcodeFormat.CODE_128
                                || result.getBarcodeFormat() == BarcodeFormat.CODABAR
                                || result.getBarcodeFormat() == BarcodeFormat.ITF
                        ){
                            SharedPreferences pref = getContext().getSharedPreferences("Barcode", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(result.getText(), params);
                            editor.apply();
                            Toast.makeText(getContext(), "barcode instance saved", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getContext(), "Invalid bar code!", Toast.LENGTH_LONG).show();
                        }


                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }


    /**
     * When the fragment is back
     */
    @Override
    public void onResume() {

        super.onResume();
        Log.d("woah", String.valueOf(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED));
        if (!openCamera && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
            mCodeScanner.startPreview();
        }
    }

    /**
     * When the fragment is paused
     */
    @Override
    public void onPause() {
        // remove QR scanner resources (I think it closes the camera?)
        if (openCamera) mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (openCamera) mCodeScanner.releaseResources();
    }
}

