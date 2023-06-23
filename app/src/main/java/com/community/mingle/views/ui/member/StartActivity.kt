package com.community.mingle.views.ui.member

import android.content.Intent
import android.os.Bundle
import com.community.mingle.R
import com.community.mingle.databinding.ActivityStartBinding
import com.community.mingle.utils.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : BaseActivity<ActivityStartBinding>(R.layout.activity_start) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.loginTv.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}