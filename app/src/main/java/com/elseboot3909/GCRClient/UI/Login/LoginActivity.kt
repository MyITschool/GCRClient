package com.elseboot3909.GCRClient.UI.Login

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment

import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.Utils.ServerDataManager
import com.elseboot3909.GCRClient.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_login)
        if (ServerDataManager.serverDataList.isEmpty()) {
            supportFragmentManager.beginTransaction().add(R.id.login_container, HelloLoginFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction().add(R.id.login_container, ServerInputFragment()).commit()
        }

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.login_container)
        if (currentFragment is HelloLoginFragment) {
            moveTaskToBack(true)
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right)
    }

}