package com.elseboot3909.GCRClient;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.databinding.ActivitySearchBinding;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());

        binding.searchBar.requestFocus();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString("search_string") != null) binding.searchBar.setText(bundle.getString("search_string"));

        binding.searchBar.requestFocus();

        binding.searchBar.setSelection(binding.searchBar.getText().length());

        binding.searchBar.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                Intent retIntent = new Intent();
                retIntent.putExtra("search_string", binding.searchBar.getText().toString().trim());
                this.setResult(Constants.SEARCH_ACQUIRED, retIntent);
                finish();
                return true;
            }
            return false;
        });

        setContentView(binding.getRoot());
    }
}