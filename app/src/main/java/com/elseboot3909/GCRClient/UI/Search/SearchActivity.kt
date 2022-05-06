package com.elseboot3909.GCRClient.UI.Search

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.elseboot3909.GCRClient.Utils.Constants
import com.elseboot3909.GCRClient.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        val bundle = intent.extras
        binding.searchBar.setText(bundle?.getString("search_string"))
        binding.searchBar.requestFocus()
        binding.searchBar.setSelection(binding.searchBar.text.length)
        binding.searchBar.setOnKeyListener { _, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                val retIntent = Intent()
                retIntent.putExtra("search_string", binding.searchBar.text.toString().trim())
                this.setResult(Constants.SEARCH_ACQUIRED, retIntent)
                finish()
                true
            } else {
                false
            }
        }

        setContentView(binding.root)
    }
}