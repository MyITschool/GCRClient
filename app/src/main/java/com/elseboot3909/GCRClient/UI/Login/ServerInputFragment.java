package com.elseboot3909.GCRClient.UI.Login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.API.ConfigAPI;
import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.R;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.FragmentServerInputBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServerInputFragment extends Fragment {

    FragmentServerInputBinding binding;

    private String savedServerURL;
    private String prefixURL;

    public ServerInputFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            savedServerURL = savedInstanceState.getString(Constants.ARG_LOGIN_SERVER_URL);
        prefixURL = "/";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServerInputBinding.inflate(inflater, container, false);

        binding.progressBar.setVisibility(View.GONE);
        binding.serverName.setText(savedServerURL);

        binding.serverName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(binding.serverNameTextField, "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                Editable preSaved = binding.serverName.getText();
                savedServerURL = preSaved != null ? preSaved.toString().trim() : "";
            }
        });

        binding.nextButton.setOnClickListener(view -> checkServerName());
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ARG_LOGIN_SERVER_URL, savedServerURL);
    }

    private void setError(TextInputLayout Layout, String msg) {
        Layout.setErrorContentDescription(msg);
        Layout.setError(msg);
    }

    private void checkServerName() {
        binding.nextButton.setClickable(false);
        setError(binding.serverNameTextField, "");

        for (ServerData serverData : ServerDataManager.serverDataList) {
            if (serverData.getServerURL().equals(savedServerURL)) {
                binding.progressBar.setVisibility(View.GONE);
                setError(binding.serverNameTextField, getResources().getString(R.string.input_fragment_already_logged));
                binding.nextButton.setClickable(true);
                return;
            }
        }

        try {
            new URL(savedServerURL);
        } catch (MalformedURLException e) {
            setError(binding.serverNameTextField, getResources().getString(R.string.input_fragment_bad_server_name));
            binding.nextButton.setClickable(true);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(savedServerURL + prefixURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ConfigAPI service = retrofit.create(ConfigAPI.class);
        Call<String> version = service.getVersion();

        version.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    LoginFragment loginFragment = new LoginFragment();
                    Bundle args = new Bundle();
                    args.putString(Constants.ARG_LOGIN_SERVER_URL, savedServerURL);
                    args.putString(Constants.ARG_LOGIN_PREFIX_URL, prefixURL);
                    loginFragment.setArguments(args);
                    getParentFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left, R.anim.enter_from_left, R.anim.quit_to_right)
                            .replace(R.id.login_container, loginFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    if (!prefixURL.endsWith("r/")) {
                        prefixURL += "r/";
                        checkServerName();
                    } else {
                        Log.e(Constants.LOG_TAG, response.toString());
                        setError(binding.serverNameTextField, getResources().getString(R.string.input_fragment_bad_connection));
                        binding.nextButton.setClickable(true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                setError(binding.serverNameTextField, getResources().getString(R.string.input_fragment_bad_connection));
                binding.nextButton.setClickable(true);
            }
        });
    }

}