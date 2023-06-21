package com.mqtt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mqtt.databinding.FragmentFirstBinding;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private View.OnClickListener buttonClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) requireActivity();
                activity.onButtonClicked();
            }
        };

        binding.buttonFirst.setOnClickListener(buttonClickListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        buttonClickListener = null;
    }

}