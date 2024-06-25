package com.example.user.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.R;
import com.example.user.databinding.FragmentHomeBinding;
import com.example.user.registration.JoinMemberActivity;
import com.example.user.utilities.Constants;
import com.example.user.utilities.PreferenceManager;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        preferenceManager = new PreferenceManager(getActivity());

        if (preferenceManager.getBoolean(Constants.IS_MEMBER)) {
            binding.mainContainer.setBackgroundColor(getResources().getColor(R.color.white));
            binding.greetingText.setText("Hai, Bunda " + preferenceManager.getString(Constants.USER_FIRST_NAME));
            binding.secondBaner.setVisibility(view.VISIBLE);
            binding.buttonJoinMember.setVisibility(view.GONE);
        }

        setListener();
        return view;
    }

    private void setListener(){
        binding.buttonJoinMember.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), JoinMemberActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}