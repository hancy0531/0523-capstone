package com.example.plant

import ApiService
import ImageUploadResponse
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat

class Fragment_Diary4: Fragment() {
    private lateinit var image: ImageView
    private lateinit var date: TextView
    private lateinit var name: TextView
    private lateinit var content: EditText
    private lateinit var title: EditText
    private lateinit var cancelBtn: Button
    private lateinit var confirmBtn: Button
    private lateinit var gallery: ImageView
    private lateinit var camera: ImageView
    private var dDate: String = ""
    private var plantId: Int = 0
    private var diaryId: Int = 0
    private var userEmail: String = ""
    private var plantName: String = ""
    private var enrollTime: String = ""
    private var diaryTitle: String = ""
    private var diaryContent: String = ""
    private var imageUrl: String = "http://10.0.2.2/uploads/default.png"
    private var selectedImageUri: Uri? = null
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2/")
        .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 추가
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private val apiService = retrofit.create(ApiService::class.java)
    //권한 요청 처리
    private val galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한이 허용되면 갤러리 열기
            openGallery()
        } else {
            // 권한 거부되면 거절됐다는 메시지 보여주기
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한이 허용되면 갤러리 열기
            openCamera()
        } else {
            // 권한 거부되면 거절됐다는 메시지 보여주기
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary4, container, false)
        arguments?.let {
            diaryId = it.getInt("itemId")
            plantId = it.getInt("plantId")
            userEmail = it.getString("userEmail").toString()
            plantName = it.getString("plantName").toString()
            enrollTime = it.getString("enrollTime").toString()
            imageUrl = it.getString("imageUrl").toString()
            diaryTitle = it.getString("diaryTitle").toString()
            diaryContent = it.getString("diaryContent").toString()
            dDate = it.getString("diaryDate").toString()
        }
        image = view.findViewById(R.id.editImageView)
        date = view.findViewById(R.id.detailDate2)
        title =view.findViewById(R.id.editTitle)
        content =view.findViewById(R.id.editContent)
        name = view.findViewById(R.id.detailName2)
        cancelBtn = view.findViewById(R.id.cancelBtn)
        confirmBtn = view.findViewById(R.id.confrimBtn)
        gallery = view.findViewById(R.id.gallery)
        camera = view.findViewById(R.id.camera)

        date.text = enrollTime
        title.setText(diaryTitle)
        content.setText(diaryContent)
        name.text = plantName

        Glide.with(requireContext())
            .load(imageUrl) // 이미지 URL
            .into(image) // 이미지뷰에 로드된 이미지 설정
        gallery.setOnClickListener {
            //이미지 권한확인, sdk 33이상이면 READ_MEDIA_IMAGES, 이하면 READ_EXTERNAL_STORAGE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        camera.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        confirmBtn.setOnClickListener {
            val title = title.text.toString()
            val content = content.text.toString()
            if(title.isNotEmpty()){
                if(content.isNotEmpty()){
                    //선택된 이미지가 있으면
                    if (selectedImageUri != null) {
                        val file = File(absolutelyPath(selectedImageUri, requireContext())) // Assuming selectedImageUri is a file URI
                        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                        Log.d("test", file.name) // 파일이름 출력
                        uploadImageAndGetUrl(body) { imageUrl ->
                            if (imageUrl != null) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    editDiary(diaryId, enrollTime, title, content, imageUrl)
                                }
                            } else {
                                // 이미지 업로드 실패 처리
                                Toast.makeText(requireContext(), "이미지 uri가있으나 서버로부터 url을 받지못함", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // No image selected, proceed without uploading image
                        GlobalScope.launch(Dispatchers.IO) {
                            editDiary(diaryId, enrollTime, title, content, imageUrl)
                        }
                    }
                }else{
                    AlertDialog.Builder(requireContext())
                        .setTitle("With P")
                        .setMessage("내용을 입력해주세요.")
                        .setPositiveButton("확인") { dialog, _ ->
                            dialog.dismiss() // 다이얼로그 닫기
                        }
                        .show()
                }
            }else{
                AlertDialog.Builder(requireContext())
                    .setTitle("With P")
                    .setMessage("제목을 입력해주세요.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss() // 다이얼로그 닫기
                    }
                    .show()
            }
            //replaceFragment(Fragment_Diary3())
        }
        cancelBtn.setOnClickListener {
            Log.d("Fragment_Diary3", "Before button clicked: diaryDate=$dDate, plantId=$plantId, userEmail=$userEmail, plantName=$plantName")
            replaceFragment(Fragment_Diary3())
        }

        return view
    }
    private fun replaceFragment(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragment = fragment
        val bundle = Bundle()

        bundle.putInt("itemId", diaryId)
        bundle.putInt("plantId", plantId)
        bundle.putString("plantName", plantName)
        bundle.putString("userEmail", userEmail)
        bundle.putString("diaryTitle", diaryTitle)
        bundle.putString("diaryContent", diaryContent)
        bundle.putString("imageUrl", imageUrl)
        bundle.putString("diaryDate", dDate)
        bundle.putString("enrollTime", enrollTime)

        // Fragment에 Bundle을 설정
        fragment.arguments = bundle

        // FragmentTransaction을 사용하여 PlantEnrollFragment로 전환
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // 이전 Fragment로 돌아갈 수 있도록 back stack에 추가
        transaction.commit() // 변경 사항을 적용
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE) //onActivityResult()
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createImageUri(newFileName(), "image/jpg")?.let {
            selectedImageUri = it
            intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
            startActivityForResult(intent, REQ_CAMERA)
        }
    }
    private fun createImageUri(filename: String, mimeType: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE,mimeType)
        val resolver = requireContext().contentResolver
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
    private fun newFileName(): String{
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }
    private fun uploadImageAndGetUrl(body: MultipartBody.Part, callback: (String?) -> Unit) {
        apiService.sendImage(body).enqueue(object : Callback<ImageUploadResponse> {
            override fun onResponse(call: Call<ImageUploadResponse>, response: Response<ImageUploadResponse>) {
                if (response.isSuccessful) {
                    val imageUploadResponse = response.body()
                    val imageUrl = imageUploadResponse?.imageUrl
                    imageUrl?.let {
                        Log.d("이미지 업로드 성공", imageUrl)
                        callback(imageUrl) // 이미지 URL을 콜백으로 전달
                    } ?: run {
                        // 이미지 URL이 없는 경우, 실패 콜백 호출
                        callback(null)
                    }
                } else {
                    // 서버 응답이 실패한 경우, 실패 콜백 호출
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                // 네트워크 오류 또는 예외 발생 시 실패 콜백 호출
                Log.e("sendImage", "이미지 업로드 실패", t)
                callback(null)
            }
        })
    }
    private fun editDiary(diaryid: Int, enrolltime: String, dtitle: String, dcontent: String, imageurl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // PHP 스크립트의 URL
                val url = URL("http://10.0.2.2/editdiary.php")

                // HttpURLConnection 열기
                val connection = url.openConnection() as HttpURLConnection

                // POST 요청 설정
                connection.requestMethod = "POST"
                connection.doOutput = true

                // POST 데이터 작성
                val postData = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(diaryid.toString(), "UTF-8") + "&" +
                        URLEncoder.encode("enrolltime", "UTF-8") + "=" + URLEncoder.encode(enrolltime, "UTF-8") + "&" +
                        URLEncoder.encode("dtitle", "UTF-8") + "=" + URLEncoder.encode(dtitle, "UTF-8") + "&" +
                        URLEncoder.encode("dcontent", "UTF-8") + "=" + URLEncoder.encode(dcontent, "UTF-8") + "&" +
                        URLEncoder.encode("imageurl", "UTF-8") + "=" + URLEncoder.encode(imageurl, "UTF-8")
                // 데이터 전송
                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                // 응답 처리
                val inputStream: BufferedReader =
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader(InputStreamReader(connection.inputStream))
                    } else {
                        BufferedReader(InputStreamReader(connection.errorStream))
                    }

                val response = StringBuilder()
                var line: String?
                while (inputStream.readLine().also { line = it } != null) {
                    response.append(line)
                }
                inputStream.close()

                val result = response.toString()
                Log.d("ServerResponse", result)
                // 서버로부터의 응답에 따라 처리
                if (result == "registration successful") {
                    launch(Dispatchers.Main) {
                        //Fragment_Diary1 으로 넘어감
                        replaceFragment(Fragment_Diary1())
                    }
                } else {
                    launch(Dispatchers.Main) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("With P")
                            .setMessage("등록 실패")
                            .setPositiveButton("확인") { dialog, which -> Log.d("MyTag", "positive") }
                            .create()
                            .show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "catch", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun absolutelyPath(path: Uri?, context: Context): String{
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()
        var result = c?.getString(index!!)

        return result!!
    }
    // onActivityResult 메서드를 오버라이드하여 갤러리로부터 선택된 이미지를 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY_REQUEST_CODE) {
                // 갤러리에서 선택한 이미지의 URI 가져오기
                selectedImageUri = data?.data

                // 선택한 이미지를 ImageView에 표시
                selectedImageUri?.let { uri ->
                    image.setImageURI(uri)
                }
            }else if(requestCode == REQ_CAMERA){
                // val imageBitmap = data?.extras?.get("data") as Bitmap?
                selectedImageUri?.let { uri ->
                    image.setImageURI(uri)
                }

            }
        }
    }
}