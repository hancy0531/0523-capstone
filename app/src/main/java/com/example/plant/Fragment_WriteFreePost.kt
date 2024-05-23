package com.example.plant

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class Fragment_WriteFreePost : Fragment() {
    //private lateinit var FreeBoardFragment: Fragment_FreeBoard
    private lateinit var titleText: EditText
    private lateinit var writerText: TextView
    private lateinit var imageView: ImageView
    private lateinit var camera: ImageButton
    private lateinit var contentText: EditText
    private lateinit var postbtn: Button
    private var userEmail: String? = null
    private var board_type: Int? = null
    private var post_title: String? = null
    private var post_content: String? = null
    private var post_num: Int? = null
    private var task: String? = null

    // 카메라 및 갤러리 권한 요청 코드
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한이 부여되었을 때의 처리
            openCamera()
        } else {
            // 권한이 거부되었을 때의 처리
            Toast.makeText(requireContext(), "카메라 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한이 부여되었을 때의 처리
            openGallery()
        } else {
            // 권한이 거부되었을 때의 처리
            Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }


    //카메라 촬영 기능
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
//            imageBitmap?.let {
//                imageView.setImageBitmap(it)
//            }

        }
    }

    //이미지에서 불러오기 기능
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write_free_post, container, false)

        arguments?.let {
            userEmail = it.getString("userEmail")
            board_type = it.getInt("board_type")
            post_title = it.getString("post_title")
            post_content = it.getString("post_content")
            post_num = it.getInt("post_num")
            task = it.getString("task")
        }
        writerText = view.findViewById(R.id.writerText)
        //writerText.text = userEmail
        writerText.text = userEmail ?: "cannot find user"

        //게시물 작성 시에..
        if(task=="write") {
            //게시물 작성 기능 수행
            titleText = view.findViewById(R.id.titleText)
            contentText = view.findViewById(R.id.contentText)
            imageView = view.findViewById(R.id.imageView)
            camera = view.findViewById(R.id.camera)

            //게시물 작성 시 사진 등록 기능
            camera.setOnClickListener {
                // 다이얼로그 생성
                val options = arrayOf("카메라 촬영", "갤러리에서 가져오기")
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("사진 불러오기")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            // 카메라 촬영 기능 실행
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

//                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                            cameraLauncher.launch(cameraIntent)
                        }
                        1 -> {
                            // 갤러리에서 이미지 가져오기 기능 실행
                            //이미지 권한확인, sdk 33이상이면 READ_MEDIA_IMAGES, 이하면 READ_EXTERNAL_STORAGE
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            else
                                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

//                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                            galleryLauncher.launch(galleryIntent)
                        }
                    }
                }
                // 다이얼로그 표시
                builder.show()
            }

            //작성 버튼 누르면 데이터 저장
            postbtn = view.findViewById(R.id.postbtn)
            postbtn.setOnClickListener {
                val writer = writerText.text.toString()
                val title = titleText.text.toString()
                val content = contentText.text.toString()

                if (title.isNotEmpty() || content.isNotEmpty()) {
//                    val currentDate = Calendar.getInstance().time
//                    val dateFormat = SimpleDateFormat("yyyy / MM / dd", Locale.getDefault())
//                    val formattedDate = dateFormat.format(currentDate)
//                    val postDate = formattedDate

                    val imageDrawable = imageView.drawable as? BitmapDrawable
                    val imageBitmap = imageDrawable?.bitmap
//                    imageBitmap?.let {
//                        insertPost(it, writer, title, content, postDate)
//                    }
                    if (imageBitmap != null) {
                        insertPost(imageBitmap, writer, title, content)
                    } else {
                        insertPost2(writer, title, content)
                    }
                    // 데이터베이스에 데이터 삽입
                    //insertPost(writer, title, content, postDate)
                }

            }
        }
        else {
            //게시물 수정 기능 수행
            view.findViewById<TextView>(R.id.boardtask).text = "게시물 수정"
            titleText = view.findViewById(R.id.titleText)
            contentText = view.findViewById(R.id.contentText)
            titleText.setText(post_title)
            contentText.setText(post_content)
            postbtn = view.findViewById(R.id.postbtn)
            postbtn.text = "수정"

            camera = view.findViewById(R.id.camera)

            //이미지 불러오기
            imageView = view.findViewById(R.id.imageView)
            post_num?.let { showImageById(it) }

            //사진 수정할 수 있도록 하기
            camera.setOnClickListener {
                // 다이얼로그 생성
                val options = arrayOf("카메라 촬영", "갤러리에서 가져오기")
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("사진 불러오기")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            // 카메라 촬영 기능 실행
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                            cameraLauncher.launch(cameraIntent)
                        }
                        1 -> {
                            // 갤러리에서 이미지 가져오기 기능 실행
                            //이미지 권한확인, sdk 33이상이면 READ_MEDIA_IMAGES, 이하면 READ_EXTERNAL_STORAGE
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            else
                                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                            galleryLauncher.launch(galleryIntent)
                        }
                    }
                }
                // 다이얼로그 표시
                builder.show()
            }

            //수정 버튼 누르면 데이터 수정
            postbtn.setOnClickListener {
                val writer = writerText.text.toString()
                val title = titleText.text.toString()
                val content = contentText.text.toString()

                if (title.isNotEmpty() || content.isNotEmpty()) {
//                    val currentDate = Calendar.getInstance().time
//                    val dateFormat = SimpleDateFormat("yyyy / MM / dd", Locale.getDefault())
//                    val formattedDate = dateFormat.format(currentDate)
//                    val postDate = formattedDate

                    //이미지 데이터
                    val imageDrawable = imageView.drawable as? BitmapDrawable
                    val imageBitmap = imageDrawable?.bitmap
                    if (imageBitmap != null) {
                        editPost(imageBitmap, writer, title, content)
                    }
                    // 데이터베이스에 데이터 삽입
                    //editPost(writer, title, content, postDate)
                }
            }
        }

        return view
    }

    // 카메라 열기
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)

    }

    // 갤러리 열기
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }


    //retrofit으로 post 저장
    private fun insertPost(imageBitmap: Bitmap, writer: String, title: String, content: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2/") // 서버의 기본 URL
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            // Bitmap을 ByteArray로 변환
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // ByteArray를 RequestBody로 변환
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)

            // RequestBody를 MultipartBody.Part로 변환
            val body = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

            // 문자열을 RequestBody로 변환
            val boardtypeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), board_type.toString())
            val writerBody = RequestBody.create("text/plain".toMediaTypeOrNull(), writer)
            val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
            val contentBody = RequestBody.create("text/plain".toMediaTypeOrNull(), content)
            //val dateBody = RequestBody.create(MediaType.parse("text/plain"), date)

            // apiService.insertpost 호출을 suspend 함수로 변경
            try {
                val response = apiService.insertpost(body, boardtypeBody, titleBody, contentBody, writerBody)

                // Response 객체에서 성공 여부 확인
                if (response.isSuccessful) {
                    Log.d("Upload", "post uploaded successfully")
                    // 업로드 성공 시 처리
                    requireActivity().runOnUiThread {
                        replaceFragment(Fragment_FreeBoard())
                    }
                } else {
                    Log.e("Upload", "post upload failed")
                    // 업로드 실패 시 처리
                }
            } catch (e: Exception) {
                Log.e("Upload", "post upload error: ${e.message}")
                // 업로드 오류 시 처리
            }


        }
    }


    //이미지 없이 게시물 저장
    private fun insertPost2(writer: String, title: String, content: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2/insertpost2.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = URLEncoder.encode("board_type", "UTF-8") + "=" + URLEncoder.encode(board_type.toString(), "UTF-8") +
                        "&" + URLEncoder.encode("post_title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8") +
                        "&" + URLEncoder.encode("post_content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8") +
                        "&" + URLEncoder.encode("post_writer", "UTF-8") + "=" + URLEncoder.encode(writer, "UTF-8")
                        //"&" + URLEncoder.encode("post_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 데이터 삽입 성공
                    replaceFragment(Fragment_FreeBoard())

                } else {
                    // 데이터 삽입 실패
                    Toast.makeText(requireContext(), "insert post failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    //retrofit으로 이미지 수정
    private fun editPost(imageBitmap: Bitmap, writer: String, title: String, content: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2/") // 서버의 기본 URL
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            // Bitmap을 ByteArray로 변환
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // ByteArray를 RequestBody로 변환
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)

            // RequestBody를 MultipartBody.Part로 변환
            val body = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

            // 문자열을 RequestBody로 변환
            val numBody = RequestBody.create("text/plain".toMediaTypeOrNull(), post_num.toString())
            val boardtypeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), board_type.toString())
            val writerBody = RequestBody.create("text/plain".toMediaTypeOrNull(), writer)
            val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
            val contentBody = RequestBody.create("text/plain".toMediaTypeOrNull(), content)
            //val dateBody = RequestBody.create(MediaType.parse("text/plain"), date)

            // apiService.editpost 호출을 suspend 함수로 변경
            try {
                val response = apiService.editpost(body, numBody, boardtypeBody, titleBody, contentBody, writerBody)

                // Response 객체에서 성공 여부 확인
                if (response.isSuccessful) {
                    Log.d("Edit", "post edit successfully")
                    // 업로드 성공 시 처리
                    requireActivity().runOnUiThread {
                        replaceFragment(Fragment_FreeBoard())
                    }
                } else {
                    Log.e("Edit", "post edit failed")
                    // 업로드 실패 시 처리
                }
            } catch (e: Exception) {
                Log.e("Edit", "post edit error: ${e.message}")
                // 업로드 오류 시 처리
            }
        }
    }

    //이미지 불러오기
    private fun showImageById(id: Int) {
        // Retrofit을 사용하여 서버에 요청
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/") // 서버의 기본 URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 서버에 이미지 보기 요청
        apiService.getImageById(id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // 서버로부터 이미지 데이터를 받아와서 이미지뷰에 표시
                    val inputStream = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    //post_image.setImageBitmap(bitmap)

                    if (bitmap != null) {
                        // 이미지가 null이 아닌 경우 ImageView에 이미지 설정
                        imageView.setImageBitmap(bitmap)
                    }

//                    requireActivity().runOnUiThread {
//                        imageView.setImageBitmap(bitmap)
//                    }
                } else {
                    // 오류 처리
                    Log.e("ImageLoad", "Failed to load image")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 오류 처리
                Log.e("ImageLoad", "Error loading image", t)
            }
        })
    }


//    private fun insertPost(writer: String, title: String, content: String, date: String) {
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val url = URL("http://10.0.2.2/insertpost.php")
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "POST"
//                connection.doOutput = true
//
//                val postData = URLEncoder.encode("board_type", "UTF-8") + "=" + URLEncoder.encode(board_type.toString(), "UTF-8") +
//                        "&" + URLEncoder.encode("post_title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8") +
//                        "&" + URLEncoder.encode("post_content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8") +
//                        "&" + URLEncoder.encode("post_writer", "UTF-8") + "=" + URLEncoder.encode(writer, "UTF-8") +
//                        "&" + URLEncoder.encode("post_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")
//
//                val outputStream = OutputStreamWriter(connection.outputStream)
//                outputStream.write(postData)
//                outputStream.flush()
//                outputStream.close()
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // 데이터 삽입 성공
//                    replaceFragment(Fragment_FreeBoard())
//
//                } else {
//                    // 데이터 삽입 실패
//                    Toast.makeText(requireContext(), "insert post failed", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

//    private fun editPost(writer: String, title: String, content: String, date: String) {
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val url = URL("http://10.0.2.2/editpost.php")
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "POST"
//                connection.doOutput = true
//
//                val postData = URLEncoder.encode("board_type", "UTF-8") + "=" + URLEncoder.encode(board_type.toString(), "UTF-8") +
//                        "&" + URLEncoder.encode("post_title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8") +
//                        "&" + URLEncoder.encode("post_content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8") +
//                        "&" + URLEncoder.encode("post_writer", "UTF-8") + "=" + URLEncoder.encode(writer, "UTF-8") +
//                        "&" + URLEncoder.encode("post_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") +
//                        "&" + URLEncoder.encode("post_num", "UTF-8") + "=" + URLEncoder.encode(post_num.toString(), "UTF-8")
//
//                val outputStream = OutputStreamWriter(connection.outputStream)
//                outputStream.write(postData)
//                outputStream.flush()
//                outputStream.close()
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // 데이터 수정 성공
//                    replaceFragment(Fragment_FreeBoard())
//
//                } else {
//                    // 데이터 수정 실패
//                    Toast.makeText(requireContext(), "edit post failed", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }


    private fun replaceFragment(fragment: Fragment) {
        val bundle = Bundle().apply {
            putString("userEmail", userEmail)
            board_type?.let { putInt("board_type", it) }
        }
        fragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment) // container는 프래그먼트가 표시될 영역의 ID
        transaction.addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 화면으로 돌아갈 수 있도록 스택에 추가
        transaction.commit()
    }


}