package com.DivineInspiration.experimenter.Activity.UI.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.DivineInspiration.experimenter.Controller.ExperimentManager;
import com.DivineInspiration.experimenter.Controller.UserManager;
import com.DivineInspiration.experimenter.Model.Experiment;
import com.DivineInspiration.experimenter.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExperimentTabFragment extends Fragment implements CreateExperimentDialogFragment.ExperimentAddedCallback, ExperimentManager.ExperimentReadyCallback
{
    private ExperimentAdapter adapter;
    ArrayList<Experiment> exps = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("stuff", "onCreateView");
        return inflater.inflate(R.layout.experiment_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        adapter = new ExperimentAdapter(exps);
        RecyclerView recycler = view.findViewById(R.id.experimentList);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        ExperimentManager.getInstance().queryUserExperiment(UserManager.getInstance().getLocalUser().getUserId(), this);



    }


    @Override
    public void experimentAdded(Experiment experiment) {
        exps.add(0,experiment);
        adapter.notifyItemInserted(0);
    }

    @Override
    public void onExperimentsReady(List<Experiment> experiments) {
        exps.clear();
        exps.addAll(experiments);
        Collections.sort(exps, new Experiment.sortByDescDate());
        adapter.notifyDataSetChanged();
    }

}
