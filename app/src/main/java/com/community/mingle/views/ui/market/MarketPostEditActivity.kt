package com.community.mingle.views.ui.market

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.mingle.R
import com.community.mingle.databinding.ActivityPostWriteMarketBinding
import com.community.mingle.utils.DialogUtils
import com.community.mingle.utils.ImageUtils
import com.community.mingle.utils.ImageUtils.bitmapFromUrl
import com.community.mingle.utils.KeyboardUtils.hideKeyboard
import com.community.mingle.utils.RecyclerViewUtils
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.views.adapter.MarketPostWriteImageAdapter
import com.community.mingle.views.adapter.PostWriteImageAdapter
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
import java.net.URL
import kotlin.properties.Delegates

@AndroidEntryPoint
class MarketPostEditActivity : BaseActivity<ActivityPostWriteMarketBinding>(R.layout.activity_post_write_market) {

    private val viewModel: MarketPostViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private var itemId by Delegates.notNull<Int>()
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var place: String
    private lateinit var price: String
    private lateinit var chatUrl: String
    private var urlStrings: ArrayList<String> = ArrayList<String>()
    private var uriList: ArrayList<Uri> = ArrayList() // 이미지에 대한 Uri 리스트
    private var fileNameList: ArrayList<String> = ArrayList<String>()  // 미디어 파일명 리스트 초기화
    private var deletedUrls: ArrayList<String> = ArrayList()
    private var postTitleFilled: Boolean = true
    private var postPriceFilled: Boolean = true
    private var postContentFilled: Boolean = true

    //    private var bitmapList: List<Bitmap> = emptyList()
    private lateinit var imageAdapter: MarketPostWriteImageAdapter
    private val galleryActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                // 이미지 다중 선택시
                if (it.data?.clipData != null) {
                    val clipData = it.data?.clipData
                    //val count = it.data!!.clipData!!.itemCount
                    if (clipData!!.itemCount > 10 || (imageAdapter.itemCount + clipData.itemCount) > 10) {
                        DialogUtils.showCustomOneTextDialog(
                            this,
                            "선택 가능 사진 최대 개수는 10장입니다.",
                            "확인"
                        )
                    } else {
                        for (i in 0 until clipData.itemCount) {
                            val uri =
                                clipData.getItemAt(i).uri // 해당 인덱스의 선택한 이미지의 uri를 가져오기
                            fileNameList.add(
                                ImageUtils.getFileNameFromURI(
                                    uri!!,
                                    contentResolver
                                )!!
                            )
                            uriList.add(uri)
                            viewModel.addImageFromUri(uri)
                        }

                        if (binding.writeImageRv.adapter!!.itemCount == 0) {
                            binding.writeImageRv.visibility = View.VISIBLE
                        }

                        binding.postPhotoCountTv.text = imageAdapter.itemCount.toString() + "/10"
                    }
                }
                // 이미지 단일 선택시
                else if (it.data?.data != null) {
                    if (binding.writeImageRv.adapter!!.itemCount == 0) {
                        binding.writeImageRv.visibility = View.VISIBLE
                    }
                    fileNameList.add(
                        ImageUtils.getFileNameFromURI(
                            it.data?.data!!,
                            contentResolver
                        )!!
                    )
                    uriList.add(it.data?.data!!)
                    viewModel.addImageFromUri(it.data?.data!!)
                    binding.postPhotoCountTv.text = imageAdapter.itemCount.toString() + "/10"
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageAdapter = MarketPostWriteImageAdapter()

        processIntent()
        initRV()
        setBitmapImageListListener()
        initSellOrShareRadioGroup()
        setMarketCurrenciesListener()
        setSellOrShareChangedListener()
        initViewModel()
        initView()
    }

    private fun initRV() {
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
                binding.postPhotoCountTv.text = imageAdapter.itemCount.toString() + "/10"
                if (binding.writeImageRv.adapter!!.itemCount == 0) {
                    binding.writeImageRv.visibility = View.GONE
                }

                if (bitmap in viewModel.imageBitmapList.value) {
                    if (getUrlFromBitmap(bitmap, urlStrings) != null) {
                        getUrlFromBitmap(bitmap, urlStrings)?.let { deletedUrls.add(it) }
                    }
                }
            }
        })
    }

    private fun getUrlFromBitmap(selectedBitmap: Bitmap, urlList: ArrayList<String>): String? {
        val selectedBitmapIndex = viewModel.imageBitmapList.value.indexOf(selectedBitmap)
        return if (selectedBitmapIndex != -1 && selectedBitmapIndex < urlList.size) {
            urlList[selectedBitmapIndex]
        } else {
            null
        }
    }

    private fun setBitmapImageListListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.imageBitmapList.collect { bitmapList ->
                    imageAdapter.setItems(bitmapList)
                }
            }
        }
    }

    private fun processIntent() {
        // 이 부분은 이제 어떤 탭에서 넘어오는지에 따라 정해짐
        itemId = intent.getIntExtra("itemId", -1)
        title = intent.getStringExtra("title").toString()
        content = intent.getStringExtra("content").toString()
        price = intent.getStringExtra("price").toString()
        chatUrl = intent.getStringExtra("chatUrl").toString()
        place = intent.getStringExtra("place").toString()
        urlStrings = intent.getStringArrayListExtra("imageList") as ArrayList<String>
        viewModel.isAnon.value = intent.getBooleanExtra("isAnon", true)
        viewModel.loadImageListFromUrl(urlStrings)
        intent.getStringExtra("currency")?.let {
            binding.priceCurrenciesDropdownItem.setText(it, false)
            viewModel.setCurrency(it)
        }
        binding.postPhotoCountTv.text = urlStrings.size.toString() + "/10"

        viewModel.write_title.value = title
        viewModel.write_content.value = content
        viewModel.writePrice.value = price
        viewModel.write_location.value = place
        viewModel.write_chatUrl.value = chatUrl


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

        viewModel.writePrice.observe(binding.lifecycleOwner!!) {
            if (it.trimmedLength() > 0) {
                postPriceFilled = true
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
    }

    private fun initView() {
        loadingDialog = LoadingDialog(this@MarketPostEditActivity)

        binding.imageSelectBtn.setOnClickListener {
            if (imageAdapter.itemCount >= 10) {
                DialogUtils.showCustomOneTextDialog(this, "선택 가능 사진 최대 개수는 10장입니다.", "확인")
            } else {
                selectGalleryIntent()
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

    private fun initViewModel() {
        binding.viewModel = viewModel

        viewModel.loading.observe(binding.lifecycleOwner!!) {
            loadingDialog.show()
        }

        binding.postSendTv.setOnClickListener {
            hideKeyboard()
            getImageList()
        }

        viewModel.successEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { itemId ->
                if (itemId == -1) {
                    DialogUtils.showCustomOneTextDialog(this, "게시글 수정에 실패했습니다.", "확인")
                    loadingDialog.dismiss()
                } else {
                    val intent = Intent(this@MarketPostEditActivity, MarketPostActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("itemId", itemId)
                    startActivity(intent)
                    loadingDialog.dismiss()
                    finish()
                }
            }
        }
    }

    private fun selectGalleryIntent() {
        // 읽기, 쓰기 권한
        val writePermission =
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

    private fun getImageList() {
        val multipartList = ArrayList<MultipartBody.Part>() // imagesToAdd
        if (uriList.isNotEmpty()) {
            for (i in 0 until uriList.size) {
                // 이미지,영상에 대한 RequestBody 생성 (image/* video/*)
                val imageRequestBody =
                    RequestBody.create(
                        "image/*".toMediaTypeOrNull(),
                        ImageUtils.convertBitmapToByte(this@MarketPostEditActivity, viewModel.imageBitmapList.value[i])
                    )
                // 이미지에 대한 RequestBody 를 바탕으로 Multi form 데이터 리스트 생성
                multipartList.add(
                    MultipartBody.Part.createFormData(
                        "itemImagesToAdd",
                        fileNameList[i],
                        imageRequestBody
                    )
                )
                Log.d("filenameadijdajd", fileNameList[i])
            }
        }
        //val deletedUrls = compareBitmaps(bitmapList as ArrayList<Bitmap>, imageAdapter.getItems() as ArrayList<Bitmap?>)
        Log.d("deletedUrls", deletedUrls.toString())
        viewModel.editPost(itemId, deletedUrls, multipartList)
    }

    private fun initSellOrShareRadioGroup() {
        binding.sellOrShareRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.sell_radio_btn -> {
                    viewModel.setFreeCheckStatus(false)
                }

                R.id.share_radio_btn -> {
                    viewModel.setFreeCheckStatus(true)
                }
            }
        }
        binding.sellOrShareRadioGroup.check(R.id.sell_radio_btn)
    }

    private fun setMarketCurrenciesListener() {
        lifecycleScope.launch {
            viewModel.marketCurrencies
                .collect { currencies ->
                    binding.priceCurrenciesDropdownItem.setAdapter(
                        ArrayAdapter(
                            this@MarketPostEditActivity,
                            R.layout.item_dropdown,
                            currencies
                        )
                    )
                }
        }
    }

    private fun setSellOrShareChangedListener() {
        viewModel.isFree.observe(this@MarketPostEditActivity) { isFree ->
            if (isFree) {
                binding.priceContainer.apply {
                    isEnabled = false
                    setBackgroundColor(ContextCompat.getColor(this@MarketPostEditActivity, R.color.gray_01))
                }
                binding.priceCurrenciesDropdown.isEnabled = false
                binding.priceEt.apply {
                    isEnabled = false
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTextColor(ContextCompat.getColor(this@MarketPostEditActivity, R.color.gray_03))
                }
            } else {
                binding.priceContainer.apply {
                    isEnabled = true
                    setBackgroundColor(ContextCompat.getColor(this@MarketPostEditActivity, R.color.transparent))
                }
                binding.priceCurrenciesDropdown.isEnabled = true
                binding.priceEt.apply {
                    isEnabled = true
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTextColor(Color.BLACK)
                }
            }
        }
    }


}