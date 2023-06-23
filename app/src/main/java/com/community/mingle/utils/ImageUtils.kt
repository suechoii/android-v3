package com.community.mingle.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.net.URI
import java.net.URL
import java.util.*

object ImageUtils {

    // Bitmap -> String
    fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // String -> Bitmap
    fun stringToBitmap(encodedString: String): Bitmap {
        val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    }

    // Uri -> Bitmap
    fun uriToBitmap(uri: Uri, context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    fun convertBitmapToByte(context: Context, bitmap: Bitmap, quality: Int = 100): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;

        val display = DisplayMetrics()
        val windowsManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(display)
        val width = display.widthPixels
        val height = display.heightPixels
        var bmpWidth = bitmap.width
        var bmpHeight = bitmap.height
        val widthScale = bmpWidth / width
        val heightScale = bmpHeight / height
        val scale = if (widthScale > heightScale) { widthScale } else { heightScale}

        if (scale >=1 ) {
            bmpWidth /= scale
            bmpHeight /= scale
        }

        val resized = Bitmap.createScaledBitmap(bitmap,bmpWidth,bmpHeight,true)
        resized.compress(
            Bitmap.CompressFormat.JPEG,
            quality,
            byteArrayOutputStream
        ) // compression 및 변환
        return byteArrayOutputStream.toByteArray()
    }

    fun getFileNameFromURI(
        uri: Uri,
        contentResolver: ContentResolver
    ): String? { // 추가적인 모듈화 가능할듯?
        var buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path
        }
        var cursor = contentResolver.query(uri, null, null, null, null)
        var nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) // 파일명 반환
        cursor!!.moveToFirst() // 커서 위치 이동
        return cursor.getString(nameIndex!!)
    }

    private const val MAX_WIDTH = 1280
    private const val MAX_HEIGHT = 960

    fun optimizeBitmap(context: Context, uri: Uri): String? {
        try {
            val storage = context.cacheDir // 임시 파일 경로
            val fileName = String.format("%s.%s", UUID.randomUUID(), "jpg") // 임시 파일 이름

            val tempFile = File(storage, fileName)
            tempFile.createNewFile() // 임시 파일 생성

            // 지정된 이름을 가진 파일에 쓸 파일 출력 스트림을 만든다.
            val fos = FileOutputStream(tempFile)

            decodeBitmapFromUri(uri,context)?.apply {
                compress(Bitmap.CompressFormat.JPEG, 100, fos)
                recycle()
            } ?: throw NullPointerException()

            fos.flush()
            fos.close()

            return tempFile.absolutePath // 임시파일 저장경로 리턴

        } catch (e:Exception) {
            Log.e(ContentValues.TAG, "FileUtil - ${e.message}")
        }

        return null
    }

    // 최적화 bitmap 반환
    private fun decodeBitmapFromUri(uri: Uri, context: Context): Bitmap? {

        // 인자값으로 넘어온 입력 스트림을 나중에 사용하기 위해 저장하는 BufferedInputStream 사용
        val input = BufferedInputStream(context.contentResolver.openInputStream(uri))

        input.mark(input.available()) // 입력 스트림의 특정 위치를 기억

        var bitmap: Bitmap?

        BitmapFactory.Options().run {
            // inJustDecodeBounds를 true로 설정한 상태에서 디코딩한 다음 옵션을 전달
            inJustDecodeBounds = true
            bitmap = BitmapFactory.decodeStream(input, null, this)

            input.reset() // 입력 스트림의 마지막 mark 된 위치로 재설정

//            // inSampleSize 값과 false로 설정한 inJustDecodeBounds를 사용하여 다시 디코딩
            inSampleSize = calculateInSampleSize(this)
            inJustDecodeBounds = false

            bitmap = BitmapFactory.decodeStream(input, null, this)?.apply {
                // 회전된 이미지 되돌리기에서 다시 언급할게용 :)
                rotateImageIfRequired(context,this, uri)
            }
        }

        input.close()

        return bitmap

    }

    // 리샘플링 값 계산 : 타겟 너비와 높이를 기준으로 2의 거듭제곱인 샘플 크기 값을 계산
    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > MAX_HEIGHT || width > MAX_WIDTH) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= MAX_HEIGHT && halfWidth / inSampleSize >= MAX_WIDTH) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(uri) ?: return null

        val exif = if (Build.VERSION.SDK_INT > 23) {
            ExifInterface(input)
        } else {
            ExifInterface(uri.path!!)
        }

        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0,bitmap.width, bitmap.height, matrix, true)
    }

    suspend fun bitmapResize(
        context: Context,
        uriList: ArrayList<Uri>
    ): MutableList<MultipartBody.Part>? {

        val pathHashMap = hashMapOf<Int, String?>()

        CoroutineScope(Dispatchers.IO).launch {
            uriList.forEachIndexed { index, uri ->
                launch {
                    // 지난 시간에  FileUtil에서 return 받은 값
                    val path = optimizeBitmap(context, uri)
                    pathHashMap[index] = path
                }
            }
        }.join() // 작업이 끝날 때까지 기다린다.

        val fileList = arrayListOf<MultipartBody.Part>()

        pathHashMap.forEach {
            if (it.value.isNullOrEmpty()) {
                return null
            }

            val filePart = addImageFileToRequestBody(it.value!!, "multipartFile")
            Log.e("fileList",filePart.toString())
            fileList.add(filePart)
        }

        return fileList
    }

    // 이미지 'FormData' 에 담기
    fun addImageFileToRequestBody(path: String, name: String): MultipartBody.Part {
        val imageFile = File(path)
        // MIME 타입을 따르기 위해 image/jpeg로 변환하여 RequestBody 객체 생성
        val fileRequestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        // RequestBody로 Multipart.Part 객체 생성
        return MultipartBody.Part.createFormData(name, imageFile.name, fileRequestBody)
    }

    suspend fun bitmapFromUrl(
        urlList : ArrayList<String>
    ) : List<Bitmap> {
        val bitmapList = mutableListOf<Bitmap>()
        CoroutineScope(Dispatchers.IO).launch {
            urlList.forEachIndexed { _, url ->
                launch {
                    val bitmap = try {
                        val inputStream = URL(url).openStream()
                        BitmapFactory.decodeStream(inputStream)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        null
                    }
                    if (bitmap != null) {
                        bitmapList.add(bitmap)
                    }
                }
            }
        }.join() // 작업이 끝날 때까지 기다린다.

        return bitmapList
    }

}