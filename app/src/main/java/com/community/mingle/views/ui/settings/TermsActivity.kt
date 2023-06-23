package com.community.mingle.views.ui.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.community.mingle.R
import com.community.mingle.databinding.ActivityTermsBinding
import com.community.mingle.utils.UserPostType.PRIVACY_TERMS
import com.community.mingle.utils.UserPostType.SERVICE_TERMS
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.views.ui.LoadingDialog
import com.community.mingle.views.ui.member.TermsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsActivity : BaseActivity<ActivityTermsBinding>(R.layout.activity_terms)  {

    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var option: String
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processIntent()
        initToolbar()
        initView()
        initViewModel()
    }

    private fun processIntent() {
        option = intent.getStringExtra("option").toString()
    }

    private fun initView() {
        loadingDialog = LoadingDialog(this@TermsActivity)
        binding.regulationNameTv.text = option
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.regulationToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        if (option == SERVICE_TERMS) {
            viewModel.getServiceTerms()
        } else if (option == PRIVACY_TERMS) {
            viewModel.getPrivacyTerms()
        }

        viewModel.isTermSuccess.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.termsDetail.text = viewModel.terms
                    loadingDialog.dismiss()
                }
            }
        }
        viewModel.loading.observe(binding.lifecycleOwner!!) {
            loadingDialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return false
    }


}