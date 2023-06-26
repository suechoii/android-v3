package com.community.mingle

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.community.mingle.databinding.ActivityMainBinding
import com.community.mingle.databinding.BottomDialogTermsBinding
import com.community.mingle.databinding.HomeTermsBinding
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.views.ui.board.PostActivity
import com.community.mingle.views.ui.home.HomeFragment
import com.community.mingle.views.ui.market.MarketFragment
import com.community.mingle.views.ui.univTotal.TotalFragment
import com.community.mingle.views.ui.univTotal.UnivFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding

    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postId = intent.getIntExtra("postId", -1000)
        val boardType = intent.getStringExtra("type")

        if (postId != -1000 && boardType != null) {
            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra("postId", postId)
            intent.putExtra("type",boardType)
            startActivity(intent)
        }


        val view2 = BottomDialogTermsBinding.inflate(layoutInflater)
        val bool = intent.getBooleanExtra("isLogin",false)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.bottomNavigationView.itemIconTintList = null;

        supportFragmentManager.beginTransaction()
            .replace(R.id.home_frm, HomeFragment())
            .commitAllowingStateLoss()

        mBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.univFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_frm, UnivFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.totalFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_frm, TotalFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.marketFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_frm, MarketFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false}

        if (bool) {
            mBinding.viewModel = viewModel
            val view = HomeTermsBinding.inflate(layoutInflater)
            val dialog = BottomSheetDialog(this,R.style.DialogCustomTheme)
            dialog.behavior.state= BottomSheetBehavior.STATE_EXPANDED
            dialog.setContentView(view.root)
            dialog.show()
            val dialog2 = BottomSheetDialog(this, R.style.DialogCustomTheme)
            dialog2.behavior.state=  BottomSheetBehavior.STATE_EXPANDED
            view.detailTermsTv.setOnClickListener {
                viewModel.getServiceTerms()
                viewModel.isTermSuccess.observe(this) { event ->
                    event.getContentIfNotHandled()?.let {
                        if (it) {
                            view2.termsDetail.text = viewModel.terms
                        }
                    }
                }
                dialog2.setCancelable(false)
                dialog2.setContentView(view2.root)
                dialog2.show()
                view2.closeTermsIv.setOnClickListener {
                    dialog2.dismiss()
                }

            }
            view.allChecked.setOnClickListener {
                dialog.dismiss()
            }
        }

//        // 네비게이션을 담는 호스트
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
//
//        // 네비게이션 컨트롤러
//        val navController = navHostFragment.navController
//
//        // 바텀 네비게이션 뷰와 네비게이션을 묶어주기
//        NavigationUI.setupWithNavController(mBinding.bottomNavigationView, navController)
    }

}