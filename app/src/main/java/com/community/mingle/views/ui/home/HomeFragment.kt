package com.community.mingle.views.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.FragmentHomeBinding
import com.community.mingle.model.HotPost
import com.community.mingle.service.models.Banner
import com.community.mingle.service.models.HomeResult
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.RecyclerViewUtils
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.viewmodel.HomeViewModel
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.views.adapter.BannerVPAdapter
import com.community.mingle.views.adapter.HomeHotPostListAdapter
import com.community.mingle.views.adapter.HomeListAdapter
import com.community.mingle.views.ui.board.PostActivity
import com.community.mingle.views.ui.member.StartActivity
import com.community.mingle.views.ui.notification.NotiActivity
import com.community.mingle.views.ui.search.SearchActivity
import com.community.mingle.views.ui.settings.MyPageActivity
import com.community.mingle.views.ui.univTotal.TotalFragment
import com.community.mingle.views.ui.univTotal.UnivFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private var isRunning = true
    private val homeViewModel: HomeViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var callback: OnBackPressedCallback
    private var backPressedTime: Long = 0
    private lateinit var homeUnivRecentListAdapter: HomeListAdapter
    private lateinit var homeTotalRecentListAdapter: HomeListAdapter
    private val homeHotPostListAdapter: HomeHotPostListAdapter by lazy {
        HomeHotPostListAdapter(
            onItemClick = ::onHomeHotPostClick,
            onCancelBlindClick = { _, position ->
                onHomeHotPostCancelBlind(position)
            }
        )
    }
    private lateinit var currentHomeUnivRecentList: List<HomeResult>
    private lateinit var currentHomeTotalRecentList: List<HomeResult>
    private lateinit var viewPagerAdapter: BannerVPAdapter
    private var unBlindPosition: Int = 0
    private var clickedPosition: Int = 0
    private var bannerList = arrayListOf<Banner>()
    private lateinit var tempAdapter: HomeListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 백 버튼 2초 내 다시 클릭 시 앱 종료
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    activity!!.finish()
                    return
                }
                // 백 버튼 최초 클릭 시
                requireContext().toast("뒤로가기 버튼을 한 번 더 누르면 앱이 종료됩니다.")
                backPressedTime = System.currentTimeMillis()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MingleApplication.pref.isUpdate = false
        initViewModel()
        initRV()
        initView()
    }

    override fun onResume() {
        Log.d("is it onResume", MingleApplication.pref.isUpdate.toString())
        super.onResume()
        isRunning = true

        if (MingleApplication.pref.isUpdate) {
            homeViewModel.getHomeList()
            //initRV()
        }

    }

    private fun initView() {
        // 마이 페이지 및 알림 연결
        binding.mypage.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            startActivity(intent)
        }

        binding.bell.setOnClickListener {
            val intent = Intent(activity, NotiActivity::class.java)
            startActivity(intent)
        }

        binding.search.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        binding.viewModel = homeViewModel

        homeViewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        homeViewModel.refreshExpire.observe(viewLifecycleOwner) {
            if (it) {
                MingleApplication.pref.deleteToken() // 저장된 토큰 삭제
                val intent = Intent(activity, StartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                requireContext().toast("로그아웃되었습니다.")
            }
        }

        homeViewModel.homeUnivRecentList.observe(binding.lifecycleOwner!!) {
            Log.d("called again?", "check")
            homeUnivRecentListAdapter.addHomeList(it)
            binding.swipeRefresh.isRefreshing = false
            currentHomeUnivRecentList = it
        }

        homeViewModel.homeTotalRecentList.observe(viewLifecycleOwner) {
            with(homeTotalRecentListAdapter) { addHomeList(it.toMutableList()) }
            currentHomeTotalRecentList = it
        }

        binding.sliderVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        homeViewModel.banner.observe(viewLifecycleOwner) {
            bannerList = arrayListOf()
            bannerList.addAll(it)
            viewPagerAdapter.submitlist(bannerList)
        }
        /* 홈 화면 배너 */
        binding.sliderVp.apply {
            Log.d("bannerList", bannerList.toString())
            viewPagerAdapter = BannerVPAdapter()
            adapter = viewPagerAdapter
            //페이지당 보이는 인디케이터 움직임은 viewpager를 따라간다
            binding.dotsIndicator.attachTo(binding.sliderVp)
            offscreenPageLimit = 1

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    isRunning = true
                }
            })

            viewPagerAdapter.setMyItemClickListener(object :
                BannerVPAdapter.MyItemClickListener {
                override fun onItemClick(banner: Banner) {
                    if (banner.link != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(banner.link))
                        startActivity(intent)
                    }
                }
            })
        }
        //자동 스크롤은 4초 당 한 번 ’넘어가도록
        lifecycleScope.launch {
            whenResumed {
                while (isRunning) {
                    delay(4000)
                    binding.sliderVp.currentItem = (binding.sliderVp.currentItem + 1) % (bannerList.size)
                }
            }
        }

        postViewModel.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    homeViewModel.getHomeList()
                }
            }
        }
    }

    private fun initRV() {
        homeUnivRecentListAdapter = HomeListAdapter()
        homeTotalRecentListAdapter = HomeListAdapter()
        /* 지금 잔디밭 화젯거리 */
        binding.nowUnivRv.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            adapter = homeUnivRecentListAdapter
            hasFixedSize()
        }
        // TODO: Home에서 포스팅으로 넘어갈때 확인할방법
        homeUnivRecentListAdapter.setMyItemClickListener(object :
            HomeListAdapter.MyItemClickListener {
            override fun onItemClick(post: HomeResult, pos: Int, isBlind: Boolean, isReported: Boolean, reportText: String?) {
                clickedPosition = pos
                tempAdapter = homeUnivRecentListAdapter
                startPostActivity(
                    postId = post.postId,
                    authorNickName = post.nickname,
                    boardTypeName = "잔디밭",
                    isBlind = isBlind,
                    isReported = isReported,
                    reportText = reportText,
                )
            }

            override fun onCancelClick(post: HomeResult, pos: Int) {
                unBlindPosition = pos
                postViewModel.unblindPost("잔디밭", post.postId)
            }
        })
        /* 지금 광장에서는 */
        binding.nowTotalRv.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            adapter = homeTotalRecentListAdapter
            hasFixedSize()
        }

        homeTotalRecentListAdapter.setMyItemClickListener(object :
            HomeListAdapter.MyItemClickListener {
            override fun onItemClick(post: HomeResult, pos: Int, isBlind: Boolean, isReported: Boolean, reportText: String?) {
                clickedPosition = pos
                tempAdapter = homeTotalRecentListAdapter
                startPostActivity(
                    postId = post.postId,
                    authorNickName = post.nickname,
                    boardTypeName = "광장",
                    isBlind = isBlind,
                    isReported = isReported,
                    reportText = reportText,
                )
            }

            override fun onCancelClick(post: HomeResult, pos: Int) {
                unBlindPosition = pos
                postViewModel.unblindPost("잔디밭", post.postId)
            }
        })
        initHotPostRecyclerView()
        val view = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        /* 지금 잔디밭에서는 바로가기 연결 */
        binding.nowUnivTitle.setOnClickListener {
            view.findViewById<BottomNavigationItemView>(R.id.univFragment).performClick()
        }
        /* 지금 광장에서는 바로가기 연결 */
        binding.nowTotalTitle.setOnClickListener {
            view.findViewById<BottomNavigationItemView>(R.id.totalFragment).performClick()
        }
        /* 학생회 바로가기 */
        binding.quickclickStudentUnion.setOnClickListener {
            view.findViewById<BottomNavigationItemView>(R.id.univFragment).performClick()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_frm, UnivFragment().apply {
                    arguments = Bundle().apply {
                        putInt("index", 3)
                    }
                }).commitAllowingStateLoss()
        }
        /* 밍글소식 바로가기 */
        binding.quickclickMingleNews.setOnClickListener {
            view.findViewById<BottomNavigationItemView>(R.id.totalFragment).performClick()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_frm, TotalFragment().apply {
                    arguments = Bundle().apply {
                        putInt("index", 3)
                    }
                }).commitAllowingStateLoss()
        }

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.getHomeList()
        }
    }

    private fun initHotPostRecyclerView() {
        binding.hotPostsRv.apply {
            adapter = homeHotPostListAdapter
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            hasFixedSize()
        }
    }

    private fun startPostActivity(
        postId: Int,
        authorNickName: String,
        boardTypeName: String,
        isBlind: Boolean,
        isReported: Boolean,
        reportText: String?,
    ) {
        val intent = Intent(requireActivity(), PostActivity::class.java)
            .apply {
                putExtra("postId", postId)
                if (authorNickName == "HKUKSA")
                    putExtra("board", "학생회")
                if (authorNickName == "팀 밍글")
                    putExtra("board", "밍글소식")
                putExtra("type", boardTypeName)
                putExtra("isBlind", isBlind)
                putExtra("isReported", isReported)
                putExtra("reportText", reportText)
            }
        startActivity(intent)
    }

    private fun onHomeHotPostClick(post: HotPost, position: Int) {
        clickedPosition = position
        tempAdapter = homeUnivRecentListAdapter
        startPostActivity(
            postId = post.postId,
            authorNickName = post.nickname,
            boardTypeName = "잔디밭",
            isBlind = post.blinded,
            isReported = post.reported,
            reportText = post.title,
        )
    }

    private fun onHomeHotPostCancelBlind(position: Int) {
        unBlindPosition = position
        //        postViewModel.unblindPost("잔디밭", post.postId)
        // TODO: unblind hot post
    }
}