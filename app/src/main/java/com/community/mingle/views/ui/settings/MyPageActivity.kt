package com.community.mingle.views.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivityMypageBinding
import com.community.mingle.service.models.Option
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.RecyclerViewUtils
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.UserPostType
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.views.adapter.OptionListAdapter
import com.community.mingle.views.ui.member.StartActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMypageBinding>(R.layout.activity_mypage) {
    private val viewModel: MyPageViewModel by viewModels()

    private lateinit var optionListAdapter1: OptionListAdapter
    private lateinit var optionListAdapter2: OptionListAdapter
    private lateinit var optionListAdapter3: OptionListAdapter

    private val myOptionList =
        arrayListOf(
            Option(UserPostType.MY_SCRAP_POST), Option(UserPostType.MY_LIKE_POST),
            Option(UserPostType.MY_POST), Option(UserPostType.MY_COMMENT_POST)
        )

    private val marketList =
        arrayListOf(Option(UserPostType.MARKET_SELL), Option(UserPostType.MY_ITEM))

    private val termsList =
        arrayListOf(Option(UserPostType.SERVICE_TERMS), Option(UserPostType.PRIVACY_TERMS), Option(UserPostType.ENQUIRE_MINGLE))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initRV()
        initView()
    }

    override fun onResume() {
        super.onResume()
        binding.nicknameTv.text = "${MingleApplication.pref.nickname} 님"
    }

    private fun initView() {
        binding.viewModel = viewModel

        binding.nicknameTv.text = "${MingleApplication.pref.nickname} 님"
        binding.univnameTv.text = "${MingleApplication.pref.univName} 재학 중인"

        binding.manageAccountTv.setOnClickListener {
            val intent = Intent(this@MyPageActivity, ManageAccountActivity::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.logoutSuccess.observe(binding.lifecycleOwner!!) {
            val intent = Intent(this, StartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
            finish()
            toast("로그아웃되었습니다.")
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.mypageToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initRV() {
        optionListAdapter1 = OptionListAdapter(myOptionList)
        optionListAdapter2 = OptionListAdapter(marketList)
        optionListAdapter3 = OptionListAdapter(termsList)

        binding.rvOption1.apply {
            adapter = optionListAdapter1
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            hasFixedSize()
            isNestedScrollingEnabled = false
        }

        binding.rvOption2.apply {
            adapter = optionListAdapter2
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            hasFixedSize()
            isNestedScrollingEnabled = false
        }

        binding.rvOption3.apply {
            adapter = optionListAdapter3
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            hasFixedSize()
            isNestedScrollingEnabled = false
        }

        optionListAdapter1.setOnOptionClickListener(object :
            OptionListAdapter.OnOptionClickListener {
            override fun onClickOption(item: Option, position: Int) {
                val intent = Intent(this@MyPageActivity, UserPostActivity::class.java)
                intent.putExtra("option", item.title)
                startActivity(intent)
            }
        })

        optionListAdapter2.setOnOptionClickListener(object :
            OptionListAdapter.OnOptionClickListener {
            override fun onClickOption(item: Option, position: Int) {
                if (item==Option(UserPostType.MARKET_SELL)) {
                    val intent = Intent(this@MyPageActivity, MarketUserPostActivity::class.java)
                    intent.putExtra("option", item.title)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this@MyPageActivity, MarketLikedActivity::class.java)
                    startActivity(intent)
                }
            }
        })

        optionListAdapter3.setOnOptionClickListener(object :
            OptionListAdapter.OnOptionClickListener {
            override fun onClickOption(item: Option, position: Int) {
                if(item == Option(UserPostType.ENQUIRE_MINGLE)) {
                    val intent = Intent(this@MyPageActivity, EnquireActivity::class.java)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this@MyPageActivity, TermsActivity::class.java)
                    intent.putExtra("option", item.title)
                    startActivity(intent)
                }
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