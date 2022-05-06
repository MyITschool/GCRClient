package com.elseboot3909.GCRClient.UI.Login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.databinding.FragmentHelloLoginBinding

class HelloLoginFragment : Fragment() {

    private lateinit var binding: FragmentHelloLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentHelloLoginBinding.inflate(inflater, container, false)

        binding.whatIs.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gerritcodereview.com")))
        }

        binding.nextButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left, R.anim.enter_from_left, R.anim.quit_to_right)
                    .replace(R.id.login_container, ServerInputFragment())
                    .addToBackStack(null)
                    .commit()
        }

        return binding.root
    }
}