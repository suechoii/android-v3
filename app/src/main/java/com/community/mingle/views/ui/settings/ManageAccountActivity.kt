package com.community.mingle.views.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.mingle.R
import com.community.mingle.databinding.ActivityManageAccountBinding
import com.community.mingle.service.models.Option
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.UserPostType
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.views.adapter.OptionListAdapter

class ManageAccountActivity : BaseActivity<ActivityManageAccountBinding>(R.layout.activity_manage_account) {

    private lateinit var settingListAdapter: OptionListAdapter

    private val settingList =
        arrayListOf(
            Option(UserPostType.CHANGE_NICKNAME), Option(UserPostType.CHANGE_PASSWORD), Option(
                UserPostType.LEAVE
            )
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initRV()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.mypageToolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initRV() {
        settingListAdapter = OptionListAdapter(settingList)

        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ResUtils.getDrawable(R.drawable.divider_comment)?.let { divider.setDrawable(it) }
        binding.rvSettings.apply {
            adapter = settingListAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
            hasFixedSize()
            isNestedScrollingEnabled = false
        }

        settingListAdapter.setOnOptionClickListener(object :
            OptionListAdapter.OnOptionClickListener {
            override fun onClickOption(item: Option, position: Int) {
                when (item.title) {
                    UserPostType.CHANGE_NICKNAME -> {
                        val intent = Intent(this@ManageAccountActivity, ChangeNameActivity::class.java)
                        startActivity(intent)
                    }
                    UserPostType.CHANGE_PASSWORD -> {
                        val intent = Intent(this@ManageAccountActivity, ChangePasswordActivity::class.java)
                        startActivity(intent)
                    }
                    UserPostType.LEAVE -> {
                        val intent = Intent(this@ManageAccountActivity, QuitActivity::class.java)
                        startActivity(intent)
                    }

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