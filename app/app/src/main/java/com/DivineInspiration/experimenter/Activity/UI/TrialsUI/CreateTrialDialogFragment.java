package com.DivineInspiration.experimenter.Activity.UI.TrialsUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.DivineInspiration.experimenter.Activity.UI.QRBarCode.BarCodeDialogFramgent;
import com.DivineInspiration.experimenter.Activity.UI.QRBarCode.QRCodeDialogFragment;
import com.DivineInspiration.experimenter.Controller.TrialManager;
import com.DivineInspiration.experimenter.Model.Experiment;
import com.DivineInspiration.experimenter.Model.Trial.BinomialTrial;
import com.DivineInspiration.experimenter.Model.Trial.CountTrial;
import com.DivineInspiration.experimenter.Model.Trial.MeasurementTrial;
import com.DivineInspiration.experimenter.Model.Trial.NonNegativeTrial;
import com.DivineInspiration.experimenter.Model.Trial.Trial;
import com.DivineInspiration.experimenter.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A dialog fragment to create experiment trials
 */
public class CreateTrialDialogFragment extends DialogFragment implements EasyPermissions.PermissionCallbacks {

    private final OnTrialCreatedListener callback;
    String trialTypeCheck;              // The type of the trial we are dealing with
    CheckBox geoTrialCheckBox;
    View valueHolder;
    EditText measurementTextBox;        // View for the measurement trial
    TextView countNNTrial;              // View for the non-negative trial
    Button negativeCountNNButton;       // View for the non-negative trial
    Button positiveCountNNButton;       // View for the non-negative trial
    TextView failNumTrial;              // View for the binomial trial
    TextView trueNumTrial;              // View for the binomial trial
    TextView locationWarning;           // TextView to show the Location warning while adding Trial
    Button passButton;                  // View for the binomial trial
    Button failButton;                  // View for the binomial trial
    Button decrementFailNumButton;      // View for the binomial trial
    Button decrementPassNumButton;      // View for the binomial trial
    Button generateQR;                  // Button to show QR dialog
    Button generateBar;                 // Button to show barcode dialog
    int failNum = 0;                    // Count no. of fails for the binomial trial
    int passNum = 0;                    // Count no. of fails for the binomial trial
    int count = 0;                      // Count for both non-negative and count trials
    double myLat = 0;                   // Latitude of the trial
    double myLong = 0;                  // Longitude of the Trial
    LatLng trialLocation = null;        // LatLng to store the latitude and longitude
    boolean needLocation = false;       // Boolean to see if the Experiment requires geoLocation or not

    String message;
    String measure;


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getTrialLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        getDialog().dismiss();
    }

    /**
     * Interface definition for a callback to be invoked when
     * {@link com.DivineInspiration.experimenter.Activity.UI.TrialsUI.CreateTrialDialogFragment}
     * creates a trial
     */
    public interface OnTrialCreatedListener {

        /**
         * Called when {@link com.DivineInspiration.experimenter.Activity.UI.TrialsUI.CreateTrialDialogFragment}
         * creates a new trial
         * @param trial
         * The trial that was created
         */
        void onTrialAdded(Trial trial);
    }




    /**
     * Constructor.
     */
    public CreateTrialDialogFragment(OnTrialCreatedListener callback) {
        super();
        this.callback = callback;
    }

    /**
     * Runs when the dialog is first created.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.create_trial_dialog_fragment, null);

        Bundle args = getArguments();
        Experiment exp = (Experiment) args.getSerializable("experiment");       // Get the experiment the trial is for
        needLocation = exp.isRequireGeo();
        init(view);
        trialTypeCheck = exp.getTrialType();        // Get the trial type
        visibility(trialTypeCheck);
        geoCheckBox();

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.dialogColor)
                .setView(view)
                .setMessage("Create Trial")
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the appropriate method (when "OK" button of dialog is clicked) depending on type of the trial
                if(needLocation == true && geoTrialCheckBox.isChecked() == false){
                    Snackbar snackbar = Snackbar.make(getParentFragment().getView(),"GeoLocation is required to make a Trial",Snackbar.LENGTH_LONG);
                    snackbar.show();
                    getDialog().dismiss();
                }else {
                    switch (trialTypeCheck) {
                        case Trial.BINOMIAL:
                            binomialTrialDialog(args, exp);
                            break;
                        case Trial.COUNT:
                            countTrialDialog(args, exp);
                            message = String.valueOf(count);
                            break;
                        case Trial.NONNEGATIVE:
                            nonNegativeTrialDialog(args, exp);
                            message = String.valueOf(count);
                            break;
                        case Trial.MEASURE:
                            measure = measurementTextBox.getText().toString();
                            measurementTrialDialog(args, exp, measure);
                            break;
                        default:
                            break;
                    }
                }
                dialog.dismiss();
            }
        });

        // opens QR fragment
        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCodeDialogFragment frag = new QRCodeDialogFragment();
                Bundle dialogArgs = new Bundle();

                // most are in the format of experimentID-count except binomial which is pass-fail
                switch (trialTypeCheck) {
                    case Trial.BINOMIAL:
                        message = String.valueOf(passNum) + "-" + String.valueOf(failNum);
                        break;
                    case Trial.COUNT:
                        message = String.valueOf(count);
                    case Trial.NONNEGATIVE:
                        message = String.valueOf(count);
                        break;
                    case Trial.MEASURE:
                        message = measurementTextBox.getText().toString();
                        break;
                    default:
                        message = "null";
                        break;
                }
                dialogArgs.putString("message", args.getString("experimenterID") + "-" + trialTypeCheck + "-" + needLocation + "-" + message+ "-");
                frag.setArguments(dialogArgs);
                frag.show(getParentFragmentManager(), "QR code fragment");
            }
        });


        generateBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BarCodeDialogFramgent frag = new BarCodeDialogFramgent();
                Bundle dialogArgs = new Bundle();

                // most are in the format of experimentID-count except binomial which is pass-fail
                switch (trialTypeCheck) {
                    case Trial.BINOMIAL:
                        message = String.valueOf(passNum) + "-" + String.valueOf(failNum);
                        break;
                    case Trial.COUNT:
                        message = String.valueOf(count);
                    case Trial.NONNEGATIVE:
                        message = String.valueOf(count);
                        break;
                    case Trial.MEASURE:
                        message = measurementTextBox.getText().toString();
                        break;
                    default:
                        message = "null";
                        break;
                }
                dialogArgs.putString("message", args.getString("experimenterID") + "-" + trialTypeCheck + "-" + needLocation + "-" + message);
                frag.setArguments(dialogArgs);
                frag.show(getParentFragmentManager(), "Bar code fragment");
            }
        });

        return dialog;
    }

    /**
     * Information retrieval and object creation for binomial trials
     * @param args
     * A bundle created by this.getArguments(). Contains values for the fields "experimenterID" and "experimenterName"
     * @param exp
     * The experiment this trial is being performed for.
     */
    public void binomialTrialDialog(Bundle args, Experiment exp) {

        if(needLocation){
            trialLocation = new LatLng(myLat,myLong);
        }

        // We create a separate Trial for each 'Pass'
        for (int i = 0; i < passNum; i++) {
            BinomialTrial binomialTrial = new BinomialTrial(
                    args.getString("experimenterID"),
                    args.getString("experimenterName"),
                    exp.getExperimentID(),
                    true,
                    trialLocation
            );
            TrialManager.getInstance().addTrial(binomialTrial, trials -> {});
            callback.onTrialAdded(binomialTrial);
        }

        // Create a new Trial for each 'Fail'
        for (int i = 0; i < failNum; i++) {
            BinomialTrial binomialTrial = new BinomialTrial(
                    args.getString("experimenterID"),
                    args.getString("experimenterName"),
                    exp.getExperimentID(),
                    false,
                    trialLocation
            );
            TrialManager.getInstance().addTrial(binomialTrial, trials -> {});
            callback.onTrialAdded(binomialTrial);
        }
    }

    /**
     * Information retrieval and object creation for count trials
     * @param args
     * A bundle created by this.getArguments(). Contains values for the fields "experimenterID" and "experimenterName"
     * @param exp
     * The experiment this trial is being performed for.
     */
    public void countTrialDialog(Bundle args, Experiment exp) {
        if (needLocation) {
            trialLocation = new LatLng(myLat,myLong);
        }

        CountTrial countTrial = new CountTrial(
                args.getString("experimenterID"),
                args.getString("experimenterName"),
                exp.getExperimentID(),
                count,
                trialLocation
        );

        TrialManager.getInstance().addTrial(countTrial, trials -> {});
        callback.onTrialAdded(countTrial);
    }

    /**
     * Information retrieval and object creation for non-negative trials
     * @param args
     * A bundle created by this.getArguments(). Contains values for the fields "experimenterID" and "experimenterName"
     * @param exp
     * The experiment this trial is being performed for.
     */
    public void nonNegativeTrialDialog(Bundle args, Experiment exp) {

        if(needLocation){
            trialLocation = new LatLng(myLat,myLong);
        }

        NonNegativeTrial nonNegativeTrial = new NonNegativeTrial(
                args.getString("experimenterID"),
                args.getString("experimenterName"),
                exp.getExperimentID(),
                count,
                trialLocation
        );

        TrialManager.getInstance().addTrial(nonNegativeTrial, trials -> {});
        callback.onTrialAdded(nonNegativeTrial);
    }

    /**
     * Information retrieval and object creation for measurement trials
     * @param args
     * A bundle created by this.getArguments(). Contains values for the fields "experimenterID" and "experimenterName"
     * @param exp
     * The experiment this trial is being performed for.
     */
    public void measurementTrialDialog(Bundle args, Experiment exp, String measure) {

        if(needLocation){
            trialLocation = new LatLng(myLat,myLong);
        }
        double measureValue = Double.valueOf(measure);
        MeasurementTrial measurementTrial = new MeasurementTrial(
                args.getString("experimenterID"),
                args.getString("experimenterName"),
                exp.getExperimentID(),
                measureValue,
                trialLocation
        );
        TrialManager.getInstance().addTrial(measurementTrial, trials -> { });
        callback.onTrialAdded(measurementTrial);
    }

    /**
     * Initialize View instance variables.
     * @param view
     * The main dialog view
     */
    public void init(View view) {
        measurementTextBox = view.findViewById(R.id.editMeasurementValue);
        countNNTrial = view.findViewById(R.id.value_trial);
        failButton = view.findViewById(R.id.binomial_fail_button);
        passButton = view.findViewById(R.id.binomial_pass_button);
        negativeCountNNButton = view.findViewById(R.id.decrease_trial_value);
        positiveCountNNButton = view.findViewById(R.id.increase_trial_value);
        failNumTrial = view.findViewById(R.id.binomial_fail_textView);
        trueNumTrial = view.findViewById(R.id.binomial_pass_textView);
        decrementFailNumButton = view.findViewById(R.id.binomial_fail_decrement);
        decrementPassNumButton = view.findViewById(R.id.binomial_pass_decrement);
        generateQR = view.findViewById(R.id.qr_code_generator);
        generateBar = view.findViewById(R.id.barCodeButton);
        geoTrialCheckBox = view.findViewById(R.id.checkBoxTrial);
        valueHolder = view.findViewById(R.id.valueHolder);
        locationWarning = view.findViewById(R.id.locationWarning);
    }


    /**
     * Sets View visibility depending on the trial type
     * @param: trialType
     * The type of trial being created
     */
    public void visibility(String trialType){

        measurementTextBox.setVisibility(View.GONE);
        valueHolder.setVisibility(View.GONE);
        countNNTrial.setVisibility(View.GONE);
        failButton.setVisibility(View.GONE);
        passButton.setVisibility(View.GONE);
        negativeCountNNButton.setVisibility(View.GONE);
        positiveCountNNButton.setVisibility(View.GONE);
        failNumTrial.setVisibility(View.GONE);
        trueNumTrial.setVisibility(View.GONE);
        decrementFailNumButton.setVisibility(View.GONE);
        decrementPassNumButton.setVisibility(View.GONE);
        locationWarning.setVisibility(View.GONE);

        switch (trialType){
            case "Binomial trial":
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
                failNumTrial.setVisibility(View.VISIBLE);
                trueNumTrial.setVisibility(View.VISIBLE);
                decrementFailNumButton.setVisibility(View.VISIBLE);
                decrementPassNumButton.setVisibility(View.VISIBLE);
                BinomialTrialButtonController();
                break;

            case "Count trial":
                negativeCountNNButton.setVisibility(View.VISIBLE);
                positiveCountNNButton.setVisibility(View.VISIBLE);
                countNNTrial.setVisibility(View.VISIBLE);
                CountTrialButtonController();
                break;

            case "Non-Negative trial":
                negativeCountNNButton.setVisibility(View.VISIBLE);
                positiveCountNNButton.setVisibility(View.VISIBLE);
                countNNTrial.setVisibility(View.VISIBLE);
                NNTrialButtonController();
                break;

            case "Measurement trial":
                measurementTextBox.setVisibility(View.VISIBLE);
                valueHolder.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    /**
     *  Sets geolocation checkbox listener
     */
    public void geoCheckBox(){
        geoTrialCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    needLocation = true;
                    geoTrialCheckBox.setError("");
                    locationWarning.setText("Warning: Addition of trial will share your location!");
                    locationWarning.setVisibility(View.VISIBLE);
                    checkLocationPermission();
                }else{
                    locationWarning.setVisibility(View.GONE);

                }


            }

        });

        if(needLocation){
            checkLocationPermission();
        }
    }

    /**
     *  This method deals with the buttons control when the trial type is binomial.
     */
    public void BinomialTrialButtonController() {
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passNum = passNum + 1;
                trueNumTrial.setText(String.valueOf(passNum));
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failNum = failNum + 1;
                Log.d("wtf", String.valueOf(failNum));
                failNumTrial.setText( String.valueOf(failNum));
            }
        });

        decrementFailNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failNum = failNum - 1;
                failNumTrial.setText( String.valueOf(failNum));
            }
        });

        decrementPassNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passNum = passNum - 1;
                trueNumTrial.setText(String.valueOf(passNum));
            }
        });
    }

    /**
     *  This method deals with the buttons control when the trial type is count.
     */
    public void CountTrialButtonController(){
        positiveCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count + 1;
                countNNTrial.setText(String.valueOf(count));
            }
        });
        negativeCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count - 1;
                countNNTrial.setText(String.valueOf(count));
            }
        });
    }

    /**
     *  This method deals with the buttons control when the trial type is non-negative.
     */
    public void NNTrialButtonController(){
        positiveCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count + 1;
                countNNTrial.setText(String.valueOf(count));
            }
        });
        negativeCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count > 0){
                    count = count - 1;
                    countNNTrial.setText(String.valueOf(count));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /*
     * https://codinginflow.com/tutorials/android/easypermissions
     */
    @AfterPermissionGranted(123)
    private void checkLocationPermission() {

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this.getContext(), perms)) {
            getTrialLocation();
        } else {
            EasyPermissions.requestPermissions(this, "We need Location Services permission to add Trials",
                    123, perms);
        }
    }

    /*
     *https://howtodoandroid.medium.com/how-to-get-current-latitude-and-longitude-in-android-example-35437a51052a
     */
    @SuppressLint("MissingPermission")
    public void getTrialLocation(){

        LocationManager mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                myLong = location.getLongitude();
                myLat = location.getLatitude();
                Log.d("woah", "updating!");
                mLocationManager.removeUpdates(this);
            }
        });
    }
}