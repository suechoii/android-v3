package com.community.mingle.views.ui.board

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.community.mingle.R
import com.community.mingle.databinding.ActivityImageSlideBinding
import com.community.mingle.utils.ImageUtils.uriToBitmap
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.views.adapter.ImageSlideAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

@AndroidEntryPoint
class ImageSlideActivity : BaseActivity<ActivityImageSlideBinding>(R.layout.activity_image_slide) {

    private val viewModel: PostViewModel by viewModels()

    private var postId: Int = 0
    lateinit var boardType: String
    private var startPosition: Int? = 0
    private lateinit var imageSlideAdapter: ImageSlideAdapter

    private var uriList : ArrayList<URL> = ArrayList<URL>()
    private lateinit var currentURL : URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        processIntent()
        initView()
        initToolbar()
        initViewModel()
        initViewPager()

    }

    private fun processIntent() {
        boardType = intent.getStringExtra("type").toString()
        startPosition = intent.getIntExtra("position", 0)
        postId = intent.getIntExtra("postId", 0)
    }

    private fun initView() {

        // 풀스크린 모드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.toolbar
        toolbar.overflowIcon = ResUtils.getDrawable(R.drawable.ic_more)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_image_close)
        }
    }

    private fun initViewModel() {

        viewModel.getPost(boardType,postId, false)

        // 로딩 화면 가시화 여부
        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        viewModel.imageList.observe(binding.lifecycleOwner!!) {
            if (!it.isNullOrEmpty()) {
                uriList.clear()
                uriList.addAll(it)
                currentURL = uriList[0]
                imageSlideAdapter.addPostImageList(it.toMutableList())
                // 뷰페이저 시작 위치 지정
                binding.viewPager.post {
                    binding.viewPager.currentItem = startPosition!!
                }

            }
        }
    }

    private fun initViewPager() {

        imageSlideAdapter = ImageSlideAdapter()

        Log.d("tag_position", startPosition.toString())
        binding.viewPager.apply {
            adapter = imageSlideAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentURL = uriList[position]
                Log.d("log_page", "페이지 넘김")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)

        return true
    }

    private var mImage: Bitmap? = null
    val myHandler = Handler(Looper.getMainLooper())

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.post_image_save -> {

                Log.d(currentURL.toString(),"url")
                lifecycleScope.launch(Dispatchers.IO) {
                    mImage = mLoad(currentURL)
                    if (mImage != null)
                        myHandler.post {
                            mSaveMediaToStorage(mImage)
                        }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(applicationContext , "사진이 저장되었습니다" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun mLoad(url: URL): Bitmap? {
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText( applicationContext, "error" , Toast.LENGTH_SHORT).show()
        }
        return null
    }
}