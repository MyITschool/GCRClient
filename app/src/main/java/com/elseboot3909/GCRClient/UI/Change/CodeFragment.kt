package com.elseboot3909.GCRClient.UI.Change

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elseboot3909.GCRClient.databinding.FragmentCodeBinding

class CodeFragment : Fragment() {

    private lateinit var binding: FragmentCodeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

}