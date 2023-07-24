package com.community.mingle.views.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivityChangePasswordLoginBinding
import com.community.mingle.databinding.ActivityChangePasswordLogoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private var mBinding1 : ActivityChangePasswordLogoutBinding? = null
    private var mBinding2 : ActivityChangePasswordLoginBinding? = null
    private val binding1 get() = mBinding1!!
    private val binding2 get() = mBinding2!!
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

    }

    private fun initView() {
        // 로그아웃 상태인지 확인
        if (MingleApplication.pref.accessToken == null) {
            mBinding1 = ActivityChangePasswordLogoutBinding.inflate(layoutInflater)
            setContentView(binding1.root)
        } else {
            mBinding2 = ActivityChangePasswordLoginBinding.inflate(layoutInflater)
            setContentView(binding2.root)
        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}