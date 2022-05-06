package com.elseboot3909.GCRClient.UI.Change

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.elseboot3909.GCRClient.API.AccountAPI
import com.elseboot3909.GCRClient.Entities.ChangeInfo
import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.UI.Main.ChangesListFragment
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.NetManager
import com.elseboot3909.GCRClient.databinding.ActivityChangeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeBinding

    private var mName = ""
    private val bundle = Bundle()
    private var isStarred = false

    private var mProgressBarRequests = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left)
        binding = ActivityChangeBinding.inflate(layoutInflater)

        val changeInfo = intent.extras?.getSerializable("changeInfo") as ChangeInfo

        bundle.putString("id", changeInfo.id)
        binding.subject.text = changeInfo.subject

        when (changeInfo.status) {
            "NEW" -> {
                binding.active.status.setText(R.string.active)
                binding.active.status.visibility = View.VISIBLE
            }
            "MERGED" -> {
                binding.active.status.setText(R.string.active)
                binding.active.status.visibility = View.VISIBLE
            }
            "ABANDONED" -> {
                binding.abandoned.status.setText(R.string.abandoned)
                binding.abandoned.status.visibility = View.VISIBLE
            }
        }

        if (changeInfo.work_in_progress) {
            binding.wip.status.setText(R.string.wip)
            binding.wip.status.visibility = View.VISIBLE
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        mName = ChangeInfoFragment::class.java.name
        supportFragmentManager.findFragmentByTag(mName).let {
            if (it != null) {
                fragmentTransaction
                    .replace(R.id.change_container, it, mName)
            } else {
                val fragment = ChangeInfoFragment()
                fragment.arguments = bundle
                fragmentTransaction
                    .replace(R.id.change_container, fragment, mName)
                    .addToBackStack(mName)
            }
        }

        fragmentTransaction.commit()

        binding.exit.setOnClickListener { finish() }

        isStarred = changeInfo.starred
        if (isStarred) {
            binding.star.setImageResource(R.drawable.ic_baseline_star)
        }

        binding.star.setOnClickListener { updateStarredChange(changeInfo.id) }

        binding.bottomNavMain.setOnItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.change_container)
            val fgTransaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.info -> {
                    if (currentFragment !is ChangeInfoFragment) {
                        fgTransaction.setCustomAnimations(
                            R.anim.enter_from_left,
                            R.anim.quit_to_right
                        )
                        mName = ChangeInfoFragment::class.java.name
                        supportFragmentManager.findFragmentByTag(mName).let {
                            if (it != null) {
                                fgTransaction
                                    .replace(R.id.change_container, it, mName)
                            } else {
                                val fragment = ChangeInfoFragment()
                                fragment.arguments = bundle
                                fgTransaction
                                    .replace(R.id.change_container, fragment, mName)
                                    .addToBackStack(mName)
                            }
                        }

                        fgTransaction.commit()
                    }
                    true
                }
                R.id.code -> {
                    if (currentFragment !is CodeFragment) {
                        if (currentFragment is ChangeInfoFragment) {
                            fgTransaction.setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.quit_to_left
                            )
                        } else {
                            fgTransaction.setCustomAnimations(
                                R.anim.enter_from_left,
                                R.anim.quit_to_right
                            )
                        }
                        mName = CodeFragment::class.java.name
                        supportFragmentManager.findFragmentByTag(mName).let {
                            if (it != null) {
                                fgTransaction
                                    .replace(R.id.change_container, it, mName)
                            } else {
                                val fragment = CodeFragment()
                                fragment.arguments = bundle
                                fgTransaction
                                    .replace(R.id.change_container, fragment, mName)
                                    .addToBackStack(mName)
                            }
                        }
                        fgTransaction.commit()
                    }
                    true
                }
                R.id.vote -> {
                    if (currentFragment !is VoteFragment) {
                        if (currentFragment is ChangeInfoFragment || currentFragment is CodeFragment) {
                            fgTransaction.setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.quit_to_left
                            )
                        } else {
                            fgTransaction.setCustomAnimations(
                                R.anim.enter_from_left,
                                R.anim.quit_to_right
                            )
                        }
                        mName = VoteFragment::class.java.name
                        supportFragmentManager.findFragmentByTag(mName).let {
                            if (it != null) {
                                fgTransaction
                                    .replace(R.id.change_container, it, mName)
                            } else {
                                Log.e(Constants.LOG_TAG, "Vote called")
                                val fragment = VoteFragment()
                                fragment.arguments = bundle
                                fgTransaction
                                    .replace(R.id.change_container, fragment, mName)
                                    .addToBackStack(mName)
                            }
                        }
                        fgTransaction.commit()
                    }
                    true
                }
                R.id.log -> false
                R.id.comment -> false
                else -> false
            }
        }

        setContentView(binding.root)
    }

    private fun updateStarredChange(id: String) {
        binding.star.isClickable = false
        progressBarManager(true)

        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        val accountAPI = retrofit.create(AccountAPI::class.java)
        val request: Call<String> = if (isStarred) accountAPI.removeStarredChange(id) else accountAPI.putStarredChange(id)

        request.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                progressBarManager(false)
                isStarred = !isStarred
                if (isStarred) {
                    binding.star.setImageResource(R.drawable.ic_baseline_star)
                } else {
                    binding.star.setImageResource(R.drawable.ic_outline_star)
                }
                binding.star.isClickable = true
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                progressBarManager(false)
                binding.star.isClickable = true
            }

        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.change_container)
        if (currentFragment is ChangeInfoFragment) {
            finish()
        } else {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            mName = ChangesListFragment::class.java.name
            val searchFragment = supportFragmentManager.findFragmentByTag(mName)
            if (searchFragment != null) {
                fragmentTransaction.replace(R.id.change_container, searchFragment, mName)
            } else {
                finish()
            }
        }
    }

    fun progressBarManager(enabled: Boolean) {
        if (enabled) {
            mProgressBarRequests++
        } else {
            mProgressBarRequests--
        }
        if (mProgressBarRequests > 0 && binding.progressBar.visibility != View.VISIBLE) {
            binding.progressBar.visibility = View.VISIBLE
        } else if (mProgressBarRequests <= 0 && binding.progressBar.visibility == View.VISIBLE) {
            binding.progressBar.visibility = View.GONE
        }
    }

}