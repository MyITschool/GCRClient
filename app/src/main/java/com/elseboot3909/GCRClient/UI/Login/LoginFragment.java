package com.elseboot3909.GCRClient.UI.Login;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.API.AccountAPI;
import com.elseboot3909.GCRClient.Entities.AccountInfo;
import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.R;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.NetManager;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.FragmentLoginBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;

    private String serverURL;
    private String prefixURL;
    private String Password;

    private final Gson gson = new Gson();

    private final Handler mHandler = new Handler();

    public LoginFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serverURL = getArguments().getString(Constants.ARG_LOGIN_SERVER_URL);
            prefixURL = getArguments().getString(Constants.ARG_LOGIN_PREFIX_URL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.progressBar.setVisibility(View.GONE);

        binding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(binding.usernameTextField, "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(binding.passwordTextField, "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // TODO: Check for the internet connection before trying anything
        binding.nextButton.setOnClickListener(view ->
                mHandler.post(() -> {
                    binding.nextButton.setClickable(false);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    setError(binding.usernameTextField, "");
                    setError(binding.passwordTextField, "");

                    Password = binding.password.getText().toString().trim();

                    Log.e(Constants.LOG_TAG, serverURL + " " + prefixURL);
                    Retrofit retrofit = NetManager.getRetrofitConfiguration(new ServerData(null, null, serverURL, prefixURL), false);

                    AccountAPI account = retrofit.create(AccountAPI.class);
                    Call<String> accountInfo = account.getAccountInfo(binding.username.getText().toString().trim());

                    accountInfo.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && !(response.body() != null && response.body().contains("not found"))) {
                                Log.e(Constants.LOG_TAG, response.body() != null ? JsonUtils.TrimJson(response.body()) : "OK!");



                                String username = gson.fromJson(JsonUtils.TrimJson(response.body()), AccountInfo.class).getUsername();

                                Retrofit retrofit = NetManager.getRetrofitConfiguration(new ServerData(username, Password, serverURL, prefixURL), true);

                                AccountAPI account = retrofit.create(AccountAPI.class);
                                Call<String> accountDetails = account.getSelfAccountDetails();

                                accountDetails.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        if (!response.isSuccessful() || (response.body() != null && response.body().contains("Unauthorized"))) {
                                            setError(binding.passwordTextField, getResources().getString(R.string.input_fragment_bad_password));
                                            Log.e(Constants.LOG_TAG, response.toString());
                                            binding.nextButton.setClickable(true);
                                        } else {
                                            Log.e(Constants.LOG_TAG, response.body() != null ? JsonUtils.TrimJson(response.body()) : null);
                                            AccountInfo accountInfo = gson.fromJson(JsonUtils.TrimJson(response.body()), AccountInfo.class);
                                            ServerDataManager.serverDataList.add(new ServerData(accountInfo.getUsername(), Password, serverURL, prefixURL));
                                            ServerDataManager.writeServerDataList(getContext());
                                            ServerDataManager.writeNewPosition(getContext(), ServerDataManager.serverDataList.size() - 1);
                                            getActivity().setResult(ServerDataManager.serverDataList.size());
                                            getActivity().finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        Log.e(Constants.LOG_TAG, t.toString());
                                        setError(binding.passwordTextField, getResources().getString(R.string.input_fragment_bad_password));
                                        binding.nextButton.setClickable(true);
                                    }
                                });

                            } else {
                                Log.e(Constants.LOG_TAG, response.toString());
                                binding.progressBar.setVisibility(View.GONE);
                                setError(binding.usernameTextField, getResources().getString(R.string.input_fragment_bad_username));
                                binding.nextButton.setClickable(true);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            binding.progressBar.setVisibility(View.GONE);
                            setError(binding.usernameTextField, getResources().getString(R.string.input_fragment_bad_username));
                            binding.nextButton.setClickable(true);
                        }
                    });
                }));

        return binding.getRoot();
    }

    private void setError(TextInputLayout Layout, String msg) {
        Layout.setErrorContentDescription(msg);
        Layout.setError(msg);
    }

}