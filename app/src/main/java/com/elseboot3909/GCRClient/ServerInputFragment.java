package com.elseboot3909.GCRClient;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.API.ConfigAPI;
import com.google.android.material.textfield.TextInputLayout;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServerInputFragment extends Fragment {

    private EditText ServerName;
    private TextInputLayout ServerNameTextField;
    private Button CheckConnection;
    private ProgressBar progressBar;

    private static final String ARG_SERVER_NAME = "ServerName";
    private String SavedServerName;

    public ServerInputFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) SavedServerName = savedInstanceState.getString(ARG_SERVER_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_input, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        ServerName = view.findViewById(R.id.server_name);
        ServerNameTextField = view.findViewById(R.id.server_name_text_field);

        ServerName.setText(SavedServerName);

        ServerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setError(ServerNameTextField, "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                SavedServerName = ServerName.getText().toString();
            }
        });


        CheckConnection = view.findViewById(R.id.next_button);
        CheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setError(ServerNameTextField, "");
                String server = ServerName.getText().toString();
                try {
                    new URL(server);
                } catch (MalformedURLException e) {
                    setError(ServerNameTextField, getResources().getString(R.string.input_fragment_bad_server_name));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(server)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                ConfigAPI service = retrofit.create(ConfigAPI.class);
                Call<String> version = service.getVersion();

                version.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            LoginFragment loginFragment = new LoginFragment();
                            Bundle args = new Bundle();
                            args.putString(ARG_SERVER_NAME, server);
                            loginFragment.setArguments(args);
                            getParentFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left, R.anim.enter_from_left, R.anim.quit_to_right)
                                    .replace(R.id.login_container, loginFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            setError(ServerNameTextField, getResources().getString(R.string.input_fragment_bad_connection));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        setError(ServerNameTextField, getResources().getString(R.string.input_fragment_bad_connection));
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_SERVER_NAME, SavedServerName);
    }

    private void setError(TextInputLayout Layout, String msg) {
        Layout.setErrorContentDescription(msg);
        Layout.setError(msg);
    }

}