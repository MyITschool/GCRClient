package com.elseboot3909.GCRClient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.databinding.FragmentCodeBinding;


public class CodeFragment extends Fragment {

    FragmentCodeBinding binding;

    public CodeFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}