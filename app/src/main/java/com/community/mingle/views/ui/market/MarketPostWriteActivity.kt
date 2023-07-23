package com.community.mingle.views.ui.market

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.mingle.R
import com.community.mingle.databinding.ActivityPostWriteMarketBinding
import com.community.mingle.utils.DialogUtils
import com.community.mingle.utils.ImageUtils.bitmapResize
import com.community.mingle.utils.ImageUtils.convertBitmapToByte
import com.community.mingle.utils.ImageUtils.getFileNameFromURI
import com.community.mingle.utils.ImageUtils.uriToBitmap
import com.community.mingle.utils.KeyboardUtils.hideKeyboard
import com.community.mingle.utils.RecyclerViewUtils
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.views.adapter.MarketPostWriteImageAdapter
import com.community.mingle.views.ui.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException

@AndroidEntryPoint
class MarketPostWriteActivity : BaseActivity<ActivityPostWriteMarketBinding>(R.layout.activity_post_write_market) {

    private val viewModel: MarketPostViewModel by viewModels()
    private lateinit var imageAdapter: MarketPostWriteImageAdapter
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var boardType: String
    private lateinit var boardName: String
    private var uriPaths: ArrayList<Uri> = ArrayList()
    var imageList: MutableList<MultipartBody.Part>? = null
    private var uriList: ArrayList<Uri> = ArrayList() // 이미지에 대한 Uri 리스트
    private var bitmaplist: ArrayList<Bitmap> = ArrayList()
    private var postTitleFilled: Boolean = false
    private var postContentFilled: Boolean = false
    private var postPriceFilled: Boolean = false
    private var fileNameList: ArrayList<String> = ArrayList<String>()  // 미디어 파일명 리스트 초기화

    /*
       deprecated된 OnActivityResult를 대신하는 콜백 함수로, 갤러리에서 이미지를 선택하면 호출됨.
       resultCode와 data를 가지고 있음. requestCode는 쓰이지 않음.
      */
    private val galleryActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                try {
                    // 이미지 다중 선택시
                    if (it.data?.clipData != null) {
                        var clipData = it.data?.clipData
                        //val count = it.data!!.clipData!!.itemCount
                        if (clipData!!.itemCount > 10 || (imageAdapter.itemCount + clipData.itemCount) > 10) {
                            DialogUtils.showCustomOneTextDialog(
                                this,
                                "선택 가능 사진 최대 개수는 10장입니다.",
                                "확인"
                            )
                        } else {
                            val list = mutableListOf<Bitmap>()
                            for (i in 0 until clipData.itemCount) {
                                var uri =
                                    clipData.getItemAt(i).uri // 해당 인덱스의 선택한 이미지의 uri를 가져오기
                                fileNameList.add(
                                    getFileNameFromURI(
                                        uri!!,
                                        contentResolver
                                    )!!
                                )
                                uriList.add(uri)
                                list.add(uriToBitmap(uri, this))
                                bitmaplist.add(uriToBitmap(uri, this))
                            }

                            if (binding.writeImageRv.adapter!!.itemCount == 0) {
                                binding.writeImageRv.visibility = View.VISIBLE
                            }

                            imageAdapter.addItems(list)
                            binding.postPhotoCountTv.text = imageAdapter.itemCount.toString() + "/10"
                        }
                    }
                    // 이미지 단일 선택시
                    else if (it.data?.data != null) {
                        if (binding.writeImageRv.adapter!!.itemCount == 0) {
                            binding.writeImageRv.visibility = View.VISIBLE
                        }
                        fileNameList.add(
                            getFileNameFromURI(
                                it.data?.data!!,
                                contentResolver
                            )!!
                        )
                        uriList.add(it.data?.data!!)
                        bitmaplist.add(uriToBitmap(it.data?.data!!, this))
                        imageAdapter.addItem(uriToBitmap(it.data?.data!!, this))
                        binding.postPhotoCountTv.text = imageAdapter.itemCount.toString() + "/10"
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initView()
        setMarketCurrenciesListener()
        initRV()
        processIntent()
    }

    private fun processIntent() {
        // 이 부분은 이제 어떤 탭에서 넘어오는지에 따라 정해짐
        boardName = intent.getStringExtra("boardName").toString()
        boardType = intent.getStringExtra("type").toString()
    }

    private fun initView() {
        loadingDialog = LoadingDialog(this@MarketPostWriteActivity)

        binding.imageSelectBtn.setOnClickListener {
            if (imageAdapter.itemCount >= 10) {
                DialogUtils.showCustomOneTextDialog(this, "선택 가능 사진 최대 개수는 10장입니다.", "확인")
            } else {
                selectGalleryIntent()
                //binding.postPhotoCountTv.text = imageAdapter.itemCount.toString()+"/10"
            }
        }

        binding.postReturnIv.setOnClickListener {
            // 게시글 임시 저장할지, 삭제할지
            finish()
        }

        binding.priceCurrenciesDropdownItem.setOnItemClickListener { _, _, position, _ ->
            viewModel.selectCurrencyByPosition(position)
        }
    }

    private fun initRV() {
        imageAdapter = MarketPostWriteImageAdapter()
        val spaceDecoration = RecyclerViewUtils.HorizontalSpaceItemDecoration(20) // 아이템 사이의 거리
        binding.writeImageRv.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(spaceDecoration)
            hasFixedSize()
        }
        // x 버튼 눌러 이미지 리사이클러뷰의 이미지 아이템 삭제
        imageAdapter.setOnPostWriteImageClickListener(object :
            MarketPostWriteImageAdapter.OnPostWriteImageClickListener {
            override fun onClickBtDelete(bitmap: Bitmap, position: Int) {
                imageAdapter.removeItem(position)
                uriList.removeAt(position)
                bitmaplist.removeAt(position)
                if (binding.writeImageRv.adapter!!.itemCount == 0) {
                    binding.writeImageRv.visibility = View.GONE
                }
                binding.postPhotoCountTv.text = imageAdapter.itemCount.toString() + "/10"
            }
        })
    }

    // 익명버튼 활성화/비활성화 함수
    private fun setAnonymousCheckStatus(isChecked: Boolean) {
        if (isChecked) {
            viewModel.isAnon.value = true
            binding.btnAnonTv.setTextColor(ResUtils.getColor(R.color.orange_02))
            binding.btnAnonTick.setColorFilter(ResUtils.getColor(R.color.orange_02))
        } else {
            viewModel.isAnon.value = false
            binding.btnAnonTv.setTextColor(ResUtils.getColor(R.color.gray_03))
            binding.btnAnonTick.setColorFilter(ResUtils.getColor(R.color.gray_03))
        }
    }

    // 무료나눔 확인 함수
    private fun setFreeCheckStatus(isChecked: Boolean) {
        if (isChecked) {
            viewModel.isFree.value = true
            viewModel.write_price.value = "나눔해주시는 경우 가격을 정할 수 없습니다."
        } else {
            viewModel.isFree.value = false
            viewModel.write_price.value = binding.priceEt.text.toString()
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        setAnonymousCheckStatus(true)
        setFreeCheckStatus(false)

        binding.btnAnonymous.setOnClickListener {
            if (viewModel.isAnon.value == true)
                setAnonymousCheckStatus(false)
            else
                setAnonymousCheckStatus(true)
        }

//        binding.btnFree.setOnClickListener {
//            if (viewModel.isFree.value == true)
//                setFreeCheckStatus(false)
//            else
//                setFreeCheckStatus(true)
//        }

        binding.postSendTv.setOnClickListener {
            hideKeyboard()
            runBlocking {
                getImageList()
            }
        }
        // 게시글 제목 리스너
        viewModel.write_title.observe(binding.lifecycleOwner!!) {
            if (it.trimmedLength() > 0) {
                postTitleFilled = true
                // 게시글 본문도 한글자 이상이면 게시 버튼 컬러 #FF5530
                if (postContentFilled && postPriceFilled) {
                    binding.postSendTv.setTextColor(Color.parseColor("#FF5530"))
                    binding.postSendTv.isEnabled = true
                }
            } else {
                postTitleFilled = false
                binding.postSendTv.setTextColor(Color.parseColor("#959595"))
                binding.postSendTv.isEnabled = false
            }
        }
        // 게시글 본문 리스너
        viewModel.write_content.observe(binding.lifecycleOwner!!) {
            if (it.isNotEmpty()) {
                binding.wordCountTv.text = it.length.toString() + "/1000"
            }
            if (it.trimmedLength() > 0) {
                postContentFilled = true
                // 게시글 본문도 한글자 이상이면 게시 버튼 컬러 #FF5530
                if (postTitleFilled && postPriceFilled) {
                    binding.postSendTv.setTextColor(Color.parseColor("#FF5530"))
                    binding.postSendTv.isEnabled = true
                }
            } else {
                postContentFilled = false
                binding.postSendTv.setTextColor(Color.parseColor("#959595"))
                binding.postSendTv.isEnabled = false
            }
        }

        viewModel.write_price.observe(binding.lifecycleOwner!!) {
            if (it.trimmedLength() > 0) {
                postPriceFilled = true
                // 게시글 본문도 한글자 이상이면 게시 버튼 컬러 #FF5530
                if (postTitleFilled && postContentFilled) {
                    binding.postSendTv.setTextColor(Color.parseColor("#FF5530"))
                    binding.postSendTv.isEnabled = true
                }
            } else {
                postPriceFilled = false
                binding.postSendTv.setTextColor(Color.parseColor("#959595"))
                binding.postSendTv.isEnabled = false
            }
        }

        viewModel.write_location.observe(binding.lifecycleOwner!!) {
            if (it.isNotEmpty()) {
                binding.wordCount2Tv.text = it.length.toString() + "/1000"
            }
        }

        viewModel.alertMsg.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                showDialog(it)
            }
        }

        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) loadingDialog.show()
                else loadingDialog.dismiss()
            }
        }

        viewModel.successEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(this@MarketPostWriteActivity, MarketPostActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("itemId", it)
                startActivity(intent)
                loadingDialog.dismiss()
                finish()
            }
        }
    }

    private suspend fun getImageList() = withContext(Dispatchers.IO) {
        Log.d("uriPaths", uriPaths.toString())
        imageList = bitmapResize(applicationContext, uriPaths)
        var multipartList = ArrayList<MultipartBody.Part>()
        for (i in 0 until uriList.size) {
            if (uriList.size == 0) break // 받아온 미디어가 없으면 반복문 탈출
            // 이미지,영상에 대한 RequestBody 생성 (image/* video/*)
            val imageRequestBody =
                RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    convertBitmapToByte(this@MarketPostWriteActivity, bitmaplist[i])
                )
            // 이미지에 대한 RequestBody 를 바탕으로 Multi form 데이터 리스트 생성
            multipartList.add(
                MultipartBody.Part.createFormData(
                    "multipartFile",
                    fileNameList[i],
                    imageRequestBody
                )
            )
        }

        viewModel.writeMarketPost(multipartList)
    }

    private fun selectGalleryIntent() {
        // 읽기, 쓰기 권한
        var writePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var readPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= 33) {
            readPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            if (readPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    1
                )
            } else {
                var intent = Intent(Intent.ACTION_PICK)
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                intent.type = "image/*"  // 갤러리에서 이미지 선택 가능하도록 허용
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 여러 개를 선택할 수 있도록 다중 옵션 지정
                galleryActivityResult.launch(intent)
            }
        } else {
            if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
                // 권한 없다면 권한 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )

            } else {
                // 권한 있으면 Intent 를 통해 갤러리 Open 요청
                var intent = Intent(Intent.ACTION_PICK)
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                intent.type = "image/*"  // 갤러리에서 이미지 선택 가능하도록 허용
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 여러 개를 선택할 수 있도록 다중 옵션 지정
                galleryActivityResult.launch(intent)
            }
        }
    }

    fun showDialog(message: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        dialog.setNegativeButton("확인", null)
        dialog.show()
    }

    private fun setMarketCurrenciesListener() {
        lifecycleScope.launch {
            viewModel.marketCurrencies
                .collect { currencies ->
                    binding.priceCurrenciesDropdownItem.setAdapter(
                        ArrayAdapter(
                            this@MarketPostWriteActivity,
                            R.layout.item_dropdown,
                            currencies
                        )
                    )
                }
        }
    }

}