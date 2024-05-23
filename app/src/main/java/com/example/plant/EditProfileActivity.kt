package com.example.plant

//import ApiService
//import ImageUploadResponse
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView
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

class EditProfileActivity : AppCompatActivity(){

    private lateinit var profileImage: CircleImageView
    private lateinit var profileUserName: EditText
    private lateinit var profileIntroduce: EditText
    private lateinit var cancelBtn: Button
    private lateinit var editBtn: Button
    private lateinit var camera: ImageView
    private lateinit var gallery: ImageView
    private var userImage: String = ""
    private var userEmail: String = ""
    private var userName: String = ""
    private var userIntroduce: String = ""
    private var selectedImageUri: Uri? = null
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2/")
        //.baseUrl("http://localhost:80/")
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
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한이 허용되면 갤러리 열기
            openCamera()
        } else {
            // 권한 거부되면 거절됐다는 메시지 보여주기
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        profileImage = findViewById(R.id.profileImage)
        profileUserName = findViewById(R.id.profileUserName)
        profileIntroduce = findViewById(R.id.introduce)
        cancelBtn = findViewById(R.id.closeProfilePage)
        editBtn = findViewById(R.id.editProfilePage)
        gallery = findViewById(R.id.gallery)
        camera = findViewById(R.id.camera)
        userEmail = intent.getStringExtra("userEmail").toString()
        userName = intent.getStringExtra("userName").toString()
        userImage = intent.getStringExtra("userImage").toString()
        userIntroduce = intent.getStringExtra("userIntroduce").toString()
        Glide.with(this)
            .load(userImage)
            .into(profileImage)
        profileUserName.setText(userName)
        profileIntroduce.setText(userIntroduce)
        cancelBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
        editBtn.setOnClickListener {
            val userName = profileUserName.text.toString()
            val userIntroduce = profileIntroduce.text.toString()
            if(userName.isNotEmpty()){
                if(userIntroduce.isNotEmpty()){
                    //선택된 이미지가 있으면
                    if (selectedImageUri != null) {
                        val file = File(absolutelyPath(selectedImageUri, this)) // Assuming selectedImageUri is a file URI
                        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                        Log.d("test", file.name) // 파일이름 출력
                        uploadImageAndGetUrl(body) { imageUrl ->
                            if (imageUrl != null) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    editProfile(userEmail, userName, imageUrl, userIntroduce)
                                }
                            } else {
                                // 이미지 업로드 실패 처리
                                Toast.makeText(this, "이미지 uri가있으나 서버로부터 url을 받지못함", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // No image selected, proceed without uploading image
                        GlobalScope.launch(Dispatchers.IO) {
                            editProfile(userEmail, userName, userImage, userIntroduce)
                        }
                    }
                }else{
                    AlertDialog.Builder(this)
                        .setTitle("With P")
                        .setMessage("내용을 입력해주세요.")
                        .setPositiveButton("확인") { dialog, _ ->
                            dialog.dismiss() // 다이얼로그 닫기
                        }
                        .show()
                }
            }else{
                AlertDialog.Builder(this)
                    .setTitle("With P")
                    .setMessage("제목을 입력해주세요.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss() // 다이얼로그 닫기
                    }
                    .show()
            }
        }
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
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createImageUri(newFileName(), "image/jpg")?.let {
            selectedImageUri = it
            intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
            startActivityForResult(intent, REQ_CAMERA)
        }
        //intent.type = "image/*"

    }
    private fun createImageUri(filename: String, mimeType: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE,mimeType)
        val resolver = this.contentResolver
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
    private fun newFileName(): String{
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }

    //이미지를 서버로 보내고 서버에서 성공적으로 처리됐다고 이미지의 URL받으면 DB에 이미지 URL저장 plantEnroll
    private fun sendImage(imageBody: MultipartBody.Part) {
        apiService.sendImage(imageBody).enqueue(object : Callback<ImageUploadResponse> {
            override fun onResponse(call: Call<ImageUploadResponse>, response: Response<ImageUploadResponse>) {
                if (response.isSuccessful) {
                    // 이미지 전송에 성공한 경우
                    val imageUploadResponse = response.body()
                    val imageUrl = imageUploadResponse?.imageUrl // 서버로부터 이미지 URL을 받아옴
                    imageUrl?.let {
                        // 이미지 URL이 있을 경우, 해당 URL을 사용하여 일지 등록
                        editProfile(userEmail, userName, imageUrl, userIntroduce)
                    } ?: run {
                        // 이미지 URL이 없는 경우, 실패 메시지 표시
                        Toast.makeText(this@EditProfileActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 서버 응답이 실패한 경우, 실패 메시지 표시
                    Toast.makeText(this@EditProfileActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                // 네트워크 오류 또는 예외 발생 시 실패 메시지 표시
                Toast.makeText(this@EditProfileActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                Log.e("sendImage", "이미지 업로드 실패", t)
            }
        })
    }
    private fun editProfile(uemail: String, uid: String, imageurl: String, introduce: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // PHP 스크립트의 URL
                val url = URL("http://10.0.2.2/edituserinfo.php")

                // HttpURLConnection 열기
                val connection = url.openConnection() as HttpURLConnection

                // POST 요청 설정
                connection.requestMethod = "POST"
                connection.doOutput = true

                // POST 데이터 작성
                val postData = URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8") + "&" +
                        URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8") + "&" +
                        URLEncoder.encode("imageurl", "UTF-8") + "=" + URLEncoder.encode(imageurl, "UTF-8") + "&" +
                        URLEncoder.encode("introduce", "UTF-8") + "=" + URLEncoder.encode(introduce, "UTF-8")
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
                        val intent = Intent(this@EditProfileActivity, ProfileActivity::class.java)
                        intent.putExtra("userEmail", userEmail)
                        intent.putExtra("userName", userName)
                        startActivity(intent)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        AlertDialog.Builder(this@EditProfileActivity)
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
                    Toast.makeText(this@EditProfileActivity, "catch", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY_REQUEST_CODE) {
                // 갤러리에서 선택한 이미지의 URI 가져오기
                selectedImageUri = data?.data

                // 선택한 이미지를 ImageView에 표시
                selectedImageUri?.let { uri ->
                    profileImage.setImageURI(uri)
                }
            }else if(requestCode == REQ_CAMERA){
                // val imageBitmap = data?.extras?.get("data") as Bitmap?
                selectedImageUri?.let { uri ->
                    profileImage.setImageURI(uri)
                }

            }
        }
    }
    //uri로 이미지의 절대경로 얻어오기
    private fun absolutelyPath(path: Uri?, context: Context): String{
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()
        var result = c?.getString(index!!)

        return result!!
    }
}