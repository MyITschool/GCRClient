package com.elseboot3909.GCRClient.UI.Main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.UI.Login.LoginActivity
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.Utils.ServerDataManager
import com.elseboot3909.GCRClient.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mHandler = Handler(Looper.getMainLooper())
    private var mName = ""
    private var mProgressBarRequests = 0

    private val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode > 0) {
            reloadActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ServerDataManager.loadServerDataList(applicationContext)
        ServerDataManager.loadSavedPosition(applicationContext)

        if (ServerDataManager.selectedPos == -1 && ServerDataManager.serverDataList.isEmpty()) {
            ServerDataManager.writeNewPosition(applicationContext, 0)
        }

        if (ServerDataManager.serverDataList.isEmpty()) {
            activityResultLauncher.launch(Intent(this, LoginActivity::class.java))
        } else {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            mName = ChangesListFragment::class.java.name
            supportFragmentManager.findFragmentByTag(mName).let {
                if (it != null) {
                    fragmentTransaction
                            .replace(R.id.main_container, it, mName)
                } else {
                    fragmentTransaction
                            .replace(R.id.main_container, ChangesListFragment(), mName)
                            .addToBackStack(mName)
                }
            }

            fragmentTransaction.commit()
        }

        val textView = binding.serverNavSelection.getHeaderView(0).findViewById<TextView>(R.id.showed_name)
        if (ServerDataManager.serverDataList.isNotEmpty())  {
            textView.text = ServerDataManager.serverDataList[ServerDataManager.selectedPos].toString()
        }

        val materialDialogSwitchServer = MaterialAlertDialogBuilder(this)
        materialDialogSwitchServer.setSingleChoiceItems(ServerDataManager.serverDataList.map{it.toString()}.toTypedArray(), ServerDataManager.selectedPos, null)
        materialDialogSwitchServer.setTitle("Select server")
        materialDialogSwitchServer.setNeutralButton("Cancel", null)
        materialDialogSwitchServer.setPositiveButton("Select") { _, i ->
            ServerDataManager.writeNewPosition(applicationContext, i)
            reloadActivity()
        }

        val materialAlertDialogExit = MaterialAlertDialogBuilder(this)
        materialAlertDialogExit.setTitle("Are you sure you want to log out?")
        materialAlertDialogExit.setNeutralButton("Cancel", null)
        materialAlertDialogExit.setPositiveButton("Yes") { _, _ ->
            ServerDataManager.serverDataList.removeAt(ServerDataManager.selectedPos)
            ServerDataManager.writeServerDataList(applicationContext)
            ServerDataManager.writeNewPosition(applicationContext, 0)
            reloadActivity()
        }

        binding.serverNavSelection.setNavigationItemSelectedListener { item ->
            mHandler.removeCallbacksAndMessages(null)
            binding.serverNavDL.close()
            when (item.itemId) {
                R.id.add -> mHandler.postDelayed({ activityResultLauncher.launch(Intent(this, LoginActivity::class.java)) }, Constants.NAV_DRAWER_TIMEOUT)
                R.id.select -> {
                    mHandler.postDelayed({
                        if (ServerDataManager.serverDataList.size > 1) {
                            materialDialogSwitchServer.show()
                        } else {
                            Toast.makeText(applicationContext, "Nothing to choose from", Toast.LENGTH_SHORT).show()
                        }
                    }, Constants.NAV_DRAWER_TIMEOUT)
                }
                R.id.exit -> mHandler.postDelayed(materialAlertDialogExit::show, Constants.NAV_DRAWER_TIMEOUT)
            }
            false
        }

        binding.bottomNavMain.setOnItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.main_container)
            val fgTransaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.changes -> {
                    if (currentFragment !is ChangesListFragment) {
                        fgTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.quit_to_right)
                        mName = ChangesListFragment::class.java.name
                        supportFragmentManager.findFragmentByTag(mName).let {
                            if (it != null) {
                                fgTransaction
                                        .replace(R.id.main_container, it, mName)
                            } else {
                                fgTransaction
                                        .replace(R.id.main_container, ChangesListFragment(), mName)
                                        .addToBackStack(mName)
                            }
                        }
                        fgTransaction.commit()
                    }
                    true
                }
                R.id.stared -> {
                    if (currentFragment !is StarredListFragment) {
                        if (currentFragment is ChangesListFragment) {
                            fgTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left)
                        } else if (currentFragment is ProfileFragment) {
                            fgTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.quit_to_right)
                        }
                        mName = StarredListFragment::class.java.name
                        supportFragmentManager.findFragmentByTag(mName).let {
                            if (it != null) {
                                fgTransaction
                                        .replace(R.id.main_container, it, mName)
                            } else {
                                fgTransaction
                                        .replace(R.id.main_container, StarredListFragment(), mName)
                                        .addToBackStack(mName)
                            }
                        }
                        fgTransaction.commit()
                    }
                    true
                }
                R.id.profile -> {
                    if (currentFragment !is ProfileFragment) {
                        fgTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left)
                        mName = ProfileFragment::class.java.name
                        supportFragmentManager.findFragmentByTag(mName).let {
                            if (it != null) {
                                fgTransaction
                                        .replace(R.id.main_container, it, mName)
                            } else {
                                fgTransaction
                                        .replace(R.id.main_container, ProfileFragment(), mName)
                                        .addToBackStack(mName)
                            }
                        }
                        fgTransaction.commit()
                    }
                    true
                }
                else -> false
            }
        }

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun reloadActivity() {
        finish()
        startActivity(intent)
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