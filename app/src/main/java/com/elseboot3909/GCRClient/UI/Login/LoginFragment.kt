package com.elseboot3909.GCRClient.UI.Login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elseboot3909.GCRClient.API.AccountAPI
import com.elseboot3909.GCRClient.Entities.AccountInfo
import com.elseboot3909.GCRClient.Entities.ServerData
import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.JsonUtils
import com.elseboot3909.GCRClient.Utils.NetManager
import com.elseboot3909.GCRClient.Utils.ServerDataManager
import com.elseboot3909.GCRClient.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var serverURL: String
    private lateinit var prefixURL: String
    private lateinit var mPassword: String

    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        serverURL = arguments?.getString(Constants.ARG_LOGIN_SERVER_URL) ?: ""
        prefixURL = arguments?.getString(Constants.ARG_LOGIN_PREFIX_URL) ?: ""

        binding.progressBar.visibility = View.GONE

        binding.username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setError(binding.usernameTextField, "")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) { }
        })

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setError(binding.passwordTextField, "")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) { }
        })

        binding.nextButton.setOnClickListener {
            binding.nextButton.isClickable = false
            binding.progressBar.visibility = View.VISIBLE

            setError(binding.usernameTextField, "")
            setError(binding.passwordTextField, "")

            mPassword = binding.password.text.toString().trim()

            var retrofit = NetManager.getRetrofitConfiguration(ServerData("", "", serverURL, prefixURL), false)

            retrofit.create(AccountAPI::class.java).getAccountInfo(binding.username.text.toString().trim()).enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body()?.contains("not found") != true) {
                        val username = gson.fromJson(JsonUtils.trimJson(response.body()), AccountInfo::class.java).username

                        retrofit = NetManager.getRetrofitConfiguration(ServerData(username, mPassword, serverURL, prefixURL), true)

                        retrofit.create(AccountAPI::class.java).getSelfAccountDetails().enqueue(object: Callback<String> {
                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                binding.progressBar.visibility = View.GONE
                                if (response.isSuccessful && response.body()?.contains("Unauthorized") != true) {
                                    val accountInfo = gson.fromJson(JsonUtils.trimJson(response.body()), AccountInfo::class.java)
                                    ServerDataManager.serverDataList.add(ServerData(accountInfo.username, mPassword, serverURL, prefixURL))
                                    context?.let {
                                        ServerDataManager.writeServerDataList(it)
                                        ServerDataManager.writeNewPosition(it, ServerDataManager.serverDataList.size - 1)
                                    }
                                    activity?.setResult(ServerDataManager.serverDataList.size)
                                    activity?.finish()
                                } else {
                                    setError(binding.passwordTextField, resources.getString(R.string.input_fragment_bad_password))
                                    binding.nextButton.isClickable = true
                                }
                            }

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                binding.progressBar.visibility = View.GONE
                                setError(binding.passwordTextField, resources.getString(R.string.input_fragment_bad_password))
                                binding.nextButton.isClickable = true
                            }

                        })
                    } else {
                        binding.progressBar.visibility = View.GONE
                        setError(binding.usernameTextField, resources.getString(R.string.input_fragment_bad_username))
                        binding.nextButton.isClickable = true
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    setError(binding.usernameTextField, resources.getString(R.string.input_fragment_bad_username))
                    binding.nextButton.isClickable = true
                }

            })
        }

        return binding.root
    }

    private fun setError(Layout: TextInputLayout,msg: String) {
        Layout.errorContentDescription = msg
        Layout.error = msg
    }

}