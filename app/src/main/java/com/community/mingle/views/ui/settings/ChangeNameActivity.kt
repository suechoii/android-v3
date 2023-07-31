package com.community.mingle.views.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivityChangeNameBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNameActivity : BaseActivity<ActivityChangeNameBinding>(R.layout.activity_change_name) {

    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
        initViewModel()
    }

    private fun initView() {
        binding.schoolName.text = MingleApplication.pref.univName

        binding.saveBtn.setOnClickListener {
            viewModel.validateNickname()

            viewModel.isNicknameVerified.observe(binding.lifecycleOwner!!) {
                binding.existIdTv.text = it
                if (it.isNullOrEmpty()) {
                    toast("닉네임 변경이 완료되었습니다.")
                    finish()
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.nicknameToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        binding.nameEdit.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.nameOrange.visibility = View.GONE
                binding.nameGrey.visibility = View.VISIBLE


            }
            else {
                binding.nameOrange.visibility = View.VISIBLE
                binding.nameGrey.visibility = View.GONE
            }
        }

        binding.nameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var count: Int = binding.nameEdit.text.length
                binding.countTv.text = count.toString() + "/10"
                if (binding.nameEdit.text.isNotEmpty()) {
                    binding.saveBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.saveBtn.setTextColor(ContextCompat.getColor(this@ChangeNameActivity, R.color.black))
                    binding.saveBtn.isEnabled = true
                }
                else {
                    binding.saveBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.saveBtn.setTextColor(ContextCompat.getColor(this@ChangeNameActivity, R.color.gray_04))
                    binding.saveBtn.isEnabled = false
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return false
    }


}