package com.elseboot3909.GCRClient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.databinding.FragmentHelloLoginBinding;

public class HelloLoginFragment extends Fragment {

    FragmentHelloLoginBinding binding;

    public HelloLoginFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHelloLoginBinding.inflate(inflater, container, false);

        binding.whatIs.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gerritcodereview.com"))));

        binding.nextButton.setOnClickListener(view -> getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left, R.anim.enter_from_left, R.anim.quit_to_right)
                .replace(R.id.login_container, new ServerInputFragment())
                .addToBackStack(null)
                .commit());

        return binding.getRoot();
    }

}