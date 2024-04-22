package com.example.pillstime.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    // This property will hold the ViewBinding instance
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    // This abstract method must be implemented in subclasses to provide the binding object
    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)
        setupViews()
    }

    // Override onDestroy to nullify the binding reference
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Abstract method for setting up views, to be implemented in subclasses if necessary
    open fun setupViews() {
        // Add common setup code here if needed
    }
}
