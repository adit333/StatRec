package com.DivineInspiration.experimenter.Controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.DivineInspiration.experimenter.Model.Experiment;
import com.DivineInspiration.experimenter.Model.User;
import com.DivineInspiration.experimenter.Model.UserContactInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExperimentManager extends ArrayList<Experiment> {
    //Singleton ArrayList
    private static ExperimentManager singleton;
    private ArrayList<Experiment> experiments;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG = "ExperimentManager";

    public interface ExperimentReadyCallback{
         void onExperimentReady(List<Experiment> experiments);
    }









    private ExperimentManager(){
        experiments = new ArrayList<>();
    }


    public void queryUserSubs(String userId, ExperimentReadyCallback callback){
        db.collection("Experiments").whereArrayContains("SubscriberID", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(callback!=null){
                        List<Experiment> output = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot: task.getResult()){
                            output.add(expFromSnapshot(snapshot));
                        }
                        callback.onExperimentReady(output);
                    }
                }
                else{
                    Log.d("stuff", "query user subscriptions failed!");
                }
            }
        });

    }


    public void queryUserExperiments(String userId, ExperimentReadyCallback callback){
        db.collection("Experiments").whereEqualTo("OwnerID", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(callback!=null){
                        List<Experiment> output = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot: task.getResult()){
                            output.add(expFromSnapshot(snapshot));
                        }
                        callback.onExperimentReady(output);
                    }
                }
                else{
                    Log.d("stuff", "query user experiments failed!");
                }
            }
        });
    }

    private Experiment expFromSnapshot(QueryDocumentSnapshot snapshot){
        return new Experiment(
                snapshot.getString("ExperimentID"),
                snapshot.getString("ExperimentName"),
                snapshot.getString("OwnerID"),
                snapshot.getString("ExperimentDescription"),
                Objects.requireNonNull(snapshot.getLong("TrialType")).intValue(),
                snapshot.getString("Region"),
                Objects.requireNonNull(snapshot.getLong("MinimumTrials")).intValue()
        );

    }

    


//    public ArrayList<Experiment> getExperiments1(){
//
//        Task<QuerySnapshot> queryTask = db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "Success retrieving documents");
//                } else {
//                    Log.d(TAG, "Error retrieving documents", task.getException());
//                }
//            }
//        });
//
//        while(!queryTask.isComplete()) { }
//
//        QuerySnapshot query = queryTask.getResult();
//        List<DocumentSnapshot> queryData = query.getDocuments();
//
//        experiments.clear();
//        for (DocumentSnapshot doc : queryData) {
//
//            String name = doc.get("Name").toString();
//            String oID = doc.get("OwnerID").toString();
//            String description = doc.get("Description").toString();
//            String eID = doc.get("ExperimentID").toString();
//
//            Task<DocumentSnapshot> userQueryTask = db.collection("Users").document(oID).get();
//            while(!userQueryTask.isComplete()) { }
//
//            DocumentSnapshot userQuery = userQueryTask.getResult();
//
//            String username = userQuery.get("UserName").toString();
//            String userDescription = userQuery.get("UserDescription").toString();
//            String address = userQuery.get("Address").toString();
//            String city = userQuery.get("CityName").toString();
//            String email = userQuery.get("Email").toString();
//            String phone = userQuery.get("PhoneNumber").toString();
//
//            UserContactInfo contactInfo = new UserContactInfo(city, email);
//            User owner = new User(username, oID, contactInfo, userDescription);
//
//            Experiment experiment = new Experiment(name, owner, description, eID);
//        }
//
//        return experiments;
//    }
//
//    public static ExperimentManager getInstance(){
//        if(singleton == null){
//            singleton = new ExperimentManager();
//        }
//
//        return singleton;
//    }
//
//    /**
//    * Adds an experiment to the database
//    * @param experiment
//    * @return
//     *      returns true if the experiment is added successfully
//     *      returns false if the experiment is not added successfully
//     */
//    public boolean addExperiment1(Experiment experiment) {
//
//        Log.d(TAG, "addExperiment: Working");
//
//        boolean successful = true;
//        Map<String, Object> doc = new HashMap<>();
//        doc.put("Name", experiment.getExperimentName());
//        doc.put("OwnerID", experiment.getExperimentOwner().getUserId());
//        doc.put("ExperimentID", experiment.getExperimentID());
//        doc.put("Description", experiment.getExperimentDescription());
//        Map<String, Object> Subscribers = new HashMap<>();
//        Subscribers.put("0", experiment.getExperimentOwner().getUserId() );
//
//        Log.d(TAG, "working 2");
//
//        db.collection("Experiments")
//                    .document(experiment.getExperimentID())
//                    .set(doc)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "working 4");
//                        }
//                    });
//
//
//            return true;
//    }
//
//    /**
//     * Checks if an experiment is in the database. Searches by id
//     * @param experiment
//     * @return
//     *      returns true if that experiment exists in the database
//     *      returns false if the experiment does not exist in the database
//     */
//    public boolean hasExperiment1(Experiment experiment) {
//
//        String experimentID = experiment.getExperimentID();
//        Task<DocumentSnapshot> queryTask = db.collection("Experiments").document(experimentID).get();
//
//        while(!queryTask.isComplete()) { }
//
//        return queryTask.isSuccessful();
//    }
//
//    /**
//     * Checks if an experiment is in the database. Searches by id
//     * @param experimentID
//     * @return
//     *      returns true if that experiment exists in the database
//     *      returns false if the experiment does not exist in the database
//     */
//    public boolean hasExperiment1(String experimentID) {
//        Log.d(TAG, "working Has experiment");
//        Task<DocumentSnapshot> queryTask = db.collection("Experiments").document(experimentID).get();
//        Log.d(TAG, "working Has experiment Task");
//        while(!queryTask.isComplete()) { }
//
//        Log.d(TAG, "working Has experiment2");
//        return queryTask.isSuccessful();
//    }

//    /**
//     * Checks if an experiment is in the database and if it is it deletes it
//     * @param experiment
//     */
//    public void deleteExperiment(Experiment experiment) {
//        if (hasExperiment(experiment) == false) {
//            throw new IllegalArgumentException();
//        }
//        db.collection("Experiments").document(experiment.getExperimentID())
//                .delete().addOnSuccessListener(new OnSuccessListener < Void > () {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "Experiment has been Deleted Successfully");
//            }
//        });
//
//    }
//
//    public Experiment getExperiment(String experimentID) {
//
//        Experiment experiment = null;
//
//        if (hasExperiment(experimentID)) {
//
//            Task<DocumentSnapshot> queryTask = db.collection("Experiments").document(experimentID).get();
//            while(!queryTask.isComplete()) { }
//
//            DocumentSnapshot query = queryTask.getResult();
//
//            String name = query.get("Name").toString();
//            String oID = query.get("OwnerID").toString();
//            String description = query.get("Description").toString();
//            String eID = query.get("ExperimentID").toString();
//
//            queryTask = db.collection("Users").document(oID).get();
//            while(!queryTask.isComplete()) { }
//
//            query = queryTask.getResult();
//
//            String username = query.get("UserName").toString();
//            String userDescription = query.get("UserDescription").toString();
//            String address = query.get("Address").toString();
//            String city = query.get("CityName").toString();
//            String email = query.get("Email").toString();
//            String phone = query.get("PhoneNumber").toString();
//
//            UserContactInfo contactInfo = new UserContactInfo(city, email);
//            User owner = new User(username, oID, contactInfo, userDescription);
//            experiment = new Experiment(name, owner, description, eID);
//        }
//
//        return experiment;
//    }
}
