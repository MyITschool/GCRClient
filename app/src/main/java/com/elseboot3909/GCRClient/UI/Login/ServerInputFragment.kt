package com.elseboot3909.GCRClient.UI.Login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elseboot3909.GCRClient.API.ConfigAPI
import com.elseboot3909.GCRClient.Entities.ServerData
import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.NetManager
import com.elseboot3909.GCRClient.Utils.ServerDataManager
import com.elseboot3909.GCRClient.databinding.FragmentServerInputBinding
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.MalformedURLException
import java.net.URL

class ServerInputFragment : Fragment() {

    private lateinit var binding: FragmentServerInputBinding

    private lateinit var serverURL: String
    private var prefixURL: String = "/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverURL = savedInstanceState?.getString(Constants.ARG_LOGIN_SERVER_URL).toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServerInputBinding.inflate(inflater, container, false)

        binding.progressBar.visibility = View.GONE
        binding.serverName.setText(serverURL)

        binding.serverName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setError(binding.serverNameTextField, "")
            }

            override fun afterTextChanged(p0: Editable?) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                serverURL = binding.serverName.text.toString().trim()
            }
        })

        binding.nextButton.setOnClickListener { checkServerName() }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.ARG_LOGIN_SERVER_URL, serverURL)
    }

    private fun setError(Layout: TextInputLayout, msg: String) {
        Layout.errorContentDescription = msg
        Layout.error = msg
    }

    private fun checkServerName() {
        binding.nextButton.isClickable = false
        binding.progressBar.visibility = View.VISIBLE
        setError(binding.serverNameTextField, "")

        for (serverData in ServerDataManager.serverDataList) {
            if (serverData.serverURL == serverURL) {
                binding.progressBar.visibility = View.GONE
                setError(binding.serverNameTextField, resources.getString(R.string.input_fragment_already_logged))
                binding.nextButton.isClickable = true
                return
            }
        }

        try {
            URL(serverURL)
        } catch (e: MalformedURLException) {
            setError(binding.serverNameTextField, resources.getString(R.string.input_fragment_bad_server_name))
            binding.nextButton.isClickable = true
            return
        }

        val retrofit = NetManager.getRetrofitConfiguration(ServerData("", "", serverURL, prefixURL), false)
        val config = retrofit.create(ConfigAPI::class.java)

        config.getVersion().enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val loginFragment = LoginFragment()
                    val args = Bundle()
                    args.putString(Constants.ARG_LOGIN_SERVER_URL, serverURL)
                    args.putString(Constants.ARG_LOGIN_PREFIX_URL, prefixURL)
                    loginFragment.arguments = args
                    parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left, R.anim.enter_from_left, R.anim.quit_to_right)
                            .replace(R.id.login_container, loginFragment)
                            .addToBackStack(null)
                            .commit()
                } else {
                    if (prefixURL.endsWith("r/")) {
                        prefixURL += "r/"
                        checkServerName()
                    } else {
                        Log.e(Constants.LOG_TAG, response.toString())
                        setError(binding.serverNameTextField, resources.getString(R.string.input_fragment_bad_connection))
                        binding.nextButton.isClickable = true
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                setError(binding.serverNameTextField, resources.getString(R.string.input_fragment_bad_connection))
                binding.nextButton.isClickable = true
            }

        })
    }

}