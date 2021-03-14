package com.DivineInspiration.experimenter.Activity.UI.Profile;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.DivineInspiration.experimenter.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    // TODO: switch pager programmatically

    /* view pager madness
    https://developer.android.com/guide/navigation/navigation-swipe-view-2
    */


    public HomeFragment(){
        super(R.layout.fragment_home);
    }

    CollapsingToolbarLayout toolbar;
    AppBarLayout appBar;
    FloatingActionButton fab;
    Button editProfileButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (CollapsingToolbarLayout) view.findViewById(R.id.CollaspingToolBar);
        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        editProfileButton = (Button)view.findViewById(R.id.edit_profile_button);

        // title is transparent when expanded
        // FIX: below code crashes everything
//        toolbar.setTitle(LocalUserManager.getInstance().getUser().getUserName());
        toolbar.setCollapsedTitleTextAppearance(R.style.toolBarCollapsed);
        toolbar.setExpandedTitleTextAppearance(R.style.toolBarExpanded);

        // fab onclick
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Snackbar.make(view, "Woah dude", Snackbar.LENGTH_LONG).show();
            }
        });


        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfileDialogFragment().show(getChildFragmentManager(), EditProfileDialogFragment.TAG);
            }
        });



        /*
        appbar stuff
        https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed
         */
//        appBar = (AppBarLayout)view.findViewById(R.id.appBar);
//        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean showing = true;
//            int scrollRange = -1;
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if(scrollRange == -1){
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if(scrollRange + verticalOffset == 0){
//                    showing = true;
//                    toolbar.setTitle("Put user here");
//                }
//                else if (showing){
//                    showing = false;
//                 toolbar.setTitle(" ");
//                }
//            }
//        });
    }

    private class PagerAdapter extends FragmentStateAdapter {

        public PagerAdapter(Fragment frag){
            super(frag);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment frag = new TestFrag();
            Bundle args = new Bundle();
            args .putString("stuff", String.valueOf((position + 1) * 1000));
            frag.setArguments(args);
            return frag;
        }


        @Override
        public int getItemCount() {
            return 3;
        }
    }

    private class TestFrag extends Fragment {
        public TestFrag(){
            super(R.layout.id_popup);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ( (TextView)view.findViewById(R.id.main_title)).setText(savedInstanceState.getString("stuff"));
        }
    }
}
