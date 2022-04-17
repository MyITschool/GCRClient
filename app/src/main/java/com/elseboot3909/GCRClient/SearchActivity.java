package com.elseboot3909.GCRClient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.elseboot3909.GCRClient.databinding.ActivitySearchBinding;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());

        binding.searchBar.requestFocus();

        setContentView(binding.getRoot());
    }
}