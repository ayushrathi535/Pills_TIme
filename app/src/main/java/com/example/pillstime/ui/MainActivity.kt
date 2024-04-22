package com.example.pillstime.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.pillstime.R
import com.example.pillstime.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var currentFragment:Fragment ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }



        loadFragment(HomeFragment())
       setupBottomNavigation()
        Log.e("activity--->", "mainActivity")
    }


    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menu ->
            val selectedFragment = when (menu.itemId) {
                R.id.home -> HomeFragment()
                R.id.my_medicine -> MedicineFragment()
                R.id.setting -> SettingFragment()
                else -> return@setOnItemSelectedListener false
            }

            if (selectedFragment::class == currentFragment?.javaClass) {
                return@setOnItemSelectedListener true
            }

            loadFragment(selectedFragment)
            true
        }
    }


    fun getBottomNavigationView(): BottomNavigationView {
        return binding.bottomNavigationView
    }

    private fun loadFragment(fragment: Fragment) {

        if (currentFragment?.javaClass != fragment.javaClass) {
            currentFragment = fragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            transaction.replace(R.id.fragContainer, fragment)
         //   transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
