package com.community.mingle.views.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.community.mingle.R
import com.community.mingle.databinding.ActivityQuitBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuitActivity : AppCompatActivity() {
    private var mBinding: ActivityQuitBinding? = null
    private val binding get() = mBinding!!
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

    }

    private fun initView() {

        mBinding = ActivityQuitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}