package com.elseboot3909.GCRClient;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.API.AccountAPI;
import com.elseboot3909.GCRClient.Entities.AccountInfo;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginFragment extends Fragment {

    private static final String ARG_SERVER_NAME = "ServerName";

    private String ServerName;

    private EditText UserName;
    private TextInputLayout UserNameTextField;
    private EditText Password;
    private TextInputLayout PasswordTextField;
    private Button loginButton;
    private ProgressBar progressBar;

    public LoginFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ServerName = getArguments().getString(ARG_SERVER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        UserName = view.findViewById(R.id.username);
        UserNameTextField = view.findViewById(R.id.username_text_field);

        UserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(UserNameTextField, "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        Password = view.findViewById(R.id.password);
        PasswordTextField = view.findViewById(R.id.password_text_field);

        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(PasswordTextField, "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        loginButton = view.findViewById(R.id.next_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                setError(UserNameTextField, "");
                setError(PasswordTextField, "");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ServerName)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                AccountAPI account = retrofit.create(AccountAPI.class);
                Call<String> accountInfo = account.getAccountInfo(UserName.getText().toString());

                accountInfo.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            Log.e(Constants.LOG_TAG, response.body() != null ? response.body().substring(response.body().indexOf('\n') + 1) : null);

                            Gson gson = new Gson();
                            String username = gson.fromJson(response.body().substring(response.body().indexOf('\n') + 1), AccountInfo.class).getUsername();

                            OkHttpClient client = new OkHttpClient.Builder()
                                    .authenticator(new Authenticator() {
                                        @NonNull
                                        @Override
                                        public Request authenticate(@Nullable Route route, @NonNull okhttp3.Response response) {
                                            return response.request().newBuilder()
                                                    .header("Authorization", Credentials.basic(username, Password.getText().toString()))
                                                    .build();
                                        }
                                    })
                                    .followRedirects(false)
                                    .followSslRedirects(false)
                                    .build();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .client(client)
                                    .baseUrl(ServerName)
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .build();

                            AccountAPI account = retrofit.create(AccountAPI.class);
                            Call<String> oauth = account.getOAuthToken();

                            oauth.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    progressBar.setVisibility(View.GONE);
                                    if (!response.isSuccessful() || (response.body() != null && response.body().contains("Unauthorized"))) {
                                        setError(PasswordTextField, getResources().getString(R.string.input_fragment_bad_password));
                                    } else {
                                        Log.e(Constants.LOG_TAG, response.body() != null ? response.body().substring(response.body().indexOf('\n') + 1) : null);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.e(Constants.LOG_TAG, t.toString());
                                    setError(PasswordTextField, getResources().getString(R.string.input_fragment_bad_password));
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            setError(UserNameTextField, getResources().getString(R.string.input_fragment_bad_username));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        setError(UserNameTextField, getResources().getString(R.string.input_fragment_bad_username));
                    }
                });

            }
        });

        return view;
    }

    private void setError(TextInputLayout Layout, String msg) {
        Layout.setErrorContentDescription(msg);
        Layout.setError(msg);
    }

}