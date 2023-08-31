package com.community.mingle.views.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenResumed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.common.IntentConstants
import com.community.mingle.databinding.FragmentHomeBinding
import com.community.mingle.model.post.HomeHotPost
import com.community.mingle.model.post.PostType
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
import com.community.mingle.views.ui.board.HotPostsBoardActivity
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
import kotlinx.coroutines.flow.collectLatest
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
    private lateinit var homeHotPostListAdapter : HomeListAdapter
    private lateinit var currentHomeUnivRecentList: List<HomeResult>
    private lateinit var currentHomeTotalRecentList: List<HomeResult>
    private lateinit var viewPagerAdapter: BannerVPAdapter
    private var unBlindPosition: Int = 0
    private var clickedPosition: Int = 0
    private var bannerList = arrayListOf<Banner>()

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
        initViewModel()
        initRV()
        initView()
    }

//    override fun onResume() {
//        super.onResume()
//        isRunning = true
//
//        if (MingleApplication.pref.isUpdate) {
//            homeViewModel.getHomeList()
//            //initRV()
//        }
//
//    }

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

        binding.hotPostsTitle.setOnClickListener {
            val intent = Intent(context, HotPostsBoardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        binding.viewModel = homeViewModel

        homeViewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    //binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    //binding.layoutProgress.root.visibility = View.GONE
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
            homeUnivRecentListAdapter.differ.submitList(it)
            binding.swipeRefresh.isRefreshing = false
            currentHomeUnivRecentList = it
        }

        homeViewModel.homeTotalRecentList.observe(viewLifecycleOwner) {
            //with(homeTotalRecentListAdapter) { addHomeList(it.toMutableList()) }
            homeTotalRecentListAdapter.differ.submitList(it)
            currentHomeTotalRecentList = it
        }

                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        homeViewModel.homeHotPostList.collectLatest {
                            homeHotPostListAdapter.differ.submitList(it)
                        }
                    }
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
//
//        postViewModel.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
//            event.getContentIfNotHandled()?.let {
//                if (it) {
//                    homeViewModel.getHomeList()
//                }
//            }
//        }
    }

    private fun initRV() {
        homeUnivRecentListAdapter = HomeListAdapter()
        homeTotalRecentListAdapter = HomeListAdapter()
        homeHotPostListAdapter = HomeListAdapter()

        /* 지금 잔디밭 화젯거리 */
        binding.nowUnivRv.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            adapter = homeUnivRecentListAdapter
            hasFixedSize()
        }

        homeUnivRecentListAdapter.setMyItemClickListener(object :
            HomeListAdapter.MyItemClickListener {
            override fun onItemClick(post: HomeResult, pos: Int, isReported: Boolean, reportText: String?) {
                clickedPosition = pos
                startPostActivity(
                    postId = post.postId,
                    boardTypeName = "잔디밭",
                    isReported = isReported,
                    reportText = reportText,
                    categoryType = post.categoryType,
                )
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
            override fun onItemClick(post: HomeResult, pos: Int, isReported: Boolean, reportText: String?) {
                clickedPosition = pos
                startPostActivity(
                    postId = post.postId,
                    boardTypeName = "광장",
                    isReported = isReported,
                    reportText = reportText,
                    categoryType = post.categoryType,
                )
            }

        })

        binding.hotPostsRv.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            adapter = homeHotPostListAdapter
            hasFixedSize()
        }
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

        homeHotPostListAdapter.setMyItemClickListener(object :
            HomeListAdapter.MyItemClickListener {
            override fun onItemClick(post: HomeResult, pos: Int, isReported: Boolean, reportText: String?) {
                clickedPosition = pos
                post.boardType?.let {
                    startPostActivity(
                        postId = post.postId,
                        boardTypeName = it,
                        isReported = post.reported,
                        reportText = post.title,
                        categoryType = post.categoryType,
                    )
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.getHomeList()
            homeViewModel.getUnivRecent()
            homeViewModel.getTotalRecent()
            homeViewModel.loadBestPostList()
        }
    }

//    private fun initHotPostRecyclerView() {
//        binding.hotPostsRv.apply {
//            adapter = homeHotPostListAdapter
//            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
//            hasFixedSize()
//        }

    //        lifecycleScope.launch {
    //            repeatOnLifecycle(Lifecycle.State.STARTED) {
    //                homeViewModel.homeHotPostList.collectLatest {
    //                    homeHotPostListAdapter.submitList(it)
    //                }
    //            }
    //        }`
   // }

    private fun startPostActivity(
        postId: Int,
        boardTypeName: String,
        categoryType: String,
        isReported: Boolean,
        reportText: String?,
    ) {
        val intent = Intent(requireActivity(), PostActivity::class.java)
            .apply {
                putExtra("postId", postId)
                putExtra(IntentConstants.BoardType, boardTypeName)
                putExtra(IntentConstants.CategoryType, categoryType)
                //putExtra("isBlind", isBlind)
                putExtra("isReported", isReported)
                putExtra("reportText", reportText)
            }
        startActivity(intent)
    }


//    private fun onHomeHotPostClick(post: HomeHotPost, position: Int) {
//        clickedPosition = position
//        tempAdapter = homeUnivRecentListAdapter
//        startPostActivity(
//            postId = post.postId,
//            boardTypeName = when (post.postType) {
//                PostType.Total -> "광장"
//                PostType.Univ -> "잔디밭"
//                null -> ""
//            },
//            isReported = post.reported,
//            reportText = post.title,
//            categoryType = post.categoryType,
//        )
//    }

//    private fun onHomeHotPostCancelBlind(hotPost: HomeHotPost, position: Int) {
//        unBlindPosition = position
//        postViewModel.unblindPost(
//            when (hotPost.postType) {
//                null -> return
//                PostType.Total -> "광장"
//                PostType.Univ -> "잔디밭"
//            }, hotPost.postId
//        )
//    }
}