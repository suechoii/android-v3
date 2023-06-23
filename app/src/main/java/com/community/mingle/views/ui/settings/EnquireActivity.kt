package com.community.mingle.views.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivityChangeNameBinding
import com.community.mingle.databinding.ActivityEnquireBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnquireActivity : BaseActivity<ActivityEnquireBinding>(R.layout.activity_enquire) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
       binding.returnIv.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
       }
    }






}