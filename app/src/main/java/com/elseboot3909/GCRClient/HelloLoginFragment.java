package com.elseboot3909.GCRClient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elseboot3909.GCRClient.Utils.Transitions;
import com.google.android.material.transition.MaterialSharedAxis;

public class HelloLoginFragment extends Fragment {

    private Button buttonNext;
    private Button buttonWhatIs;

    public HelloLoginFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hello_login, container, false);

        //setEnterTransition(Transitions.getForwardTransition());
        //setExitTransition(Transitions.getBackwardTransition());

        buttonWhatIs = view.findViewById(R.id.what_is);
        buttonWhatIs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gerritcodereview.com")));
            }
        });
        buttonNext = view.findViewById(R.id.next_button);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.login_container, new ServerInputFragment()).addToBackStack(null).commit();
            }
        });
        return view;
    }

}