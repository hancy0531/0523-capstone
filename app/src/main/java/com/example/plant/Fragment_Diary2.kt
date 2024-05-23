package com.example.plant

//import ApiService
//import ImageUploadResponse
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.Manifest
import android.content.ContentValues
import android.os.Build
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat

const val GALLERY_REQUEST_CODE = 12
const val REQ_CAMERA = 11
const val ImageUrl = "http://10.0.2.2/uploads/default3.png"

class Fragment_Diary2 : Fragment() {
    private lateinit var image: ImageView
    private lateinit var date: TextView
    private lateinit var beforeBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var gallery: ImageView
    private lateinit var camera: ImageView
    private var userEmail: String = ""
    private var plantName: String = ""
    private var plantId: Int = 0
    private var dDate: String = ""
    private var selectedImageUri: Uri? = null
    private lateinit var diaryTitle: EditText
    private lateinit var diaryContent: EditText
    //private val ImageUrl = "http://10.0.2.2/uploads/default.png"

    //retrofit 객체 생성
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary2, container, false)

        //val choiceDate = requireActivity().intent.getStringExtra("date")
        image = view.findViewById(R.id.imageView)
        date = view.findViewById(R.id.date2)
        beforeBtn = view.findViewById(R.id.beforBtn)
        saveBtn = view.findViewById(R.id.saveBtn)
        diaryTitle = view.findViewById(R.id.enterTitle)
        diaryContent = view.findViewById(R.id.enterContent)
        gallery = view.findViewById(R.id.gallery)
        camera = view.findViewById(R.id.camera)

        arguments?.let {
            userEmail = it.getString("userEmail").toString()
            plantName = it.getString("plantName").toString()
            plantId = it.getInt("plantId")
            dDate = it.getString("diaryDate").toString()
        }
        date.text = dDate
        beforeBtn.setOnClickListener {
            replaceFragment(Fragment_Diary1())
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
        saveBtn.setOnClickListener {
            val title = diaryTitle.text.toString()
            val content = diaryContent.text.toString()
            if(title.isNotEmpty()){
                if(content.isNotEmpty()){
                    //선택된 이미지가 있으면
                    if (selectedImageUri != null) {
                        val file = File(absolutelyPath(selectedImageUri, requireContext())) // Assuming selectedImageUri is a file URI
                        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                        Log.d("test", file.name) // 파일이름 출력
                        sendImage(body)

                    } else {
                        // No image selected, proceed without uploading image
                        diaryEnroll(plantId, plantName, userEmail, dDate, title, content, ImageUrl)
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
        }

        return view
    }
    //갤러리 열기
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
        val resolver = requireContext().contentResolver
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
                        diaryEnroll(plantId, plantName, userEmail, dDate, diaryTitle.text.toString(), diaryContent.text.toString(), imageUrl)
                    } ?: run {
                        // 이미지 URL이 없는 경우, 실패 메시지 표시
                        Toast.makeText(requireContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 서버 응답이 실패한 경우, 실패 메시지 표시
                    Toast.makeText(requireContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                // 네트워크 오류 또는 예외 발생 시 실패 메시지 표시
                Toast.makeText(requireContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                Log.e("sendImage", "이미지 업로드 실패", t)
            }
        })
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
    //이미지및 URL및 입력받은 데이터 diary테이블에 저장
    private fun diaryEnroll(plantid: Int, pname: String, uemail: String, ddate: String, dtitle: String, dcontent: String, imageurl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // PHP 스크립트의 URL
                val url = URL("http://10.0.2.2/enrolldiary.php")

                // HttpURLConnection 열기
                val connection = url.openConnection() as HttpURLConnection

                // POST 요청 설정
                connection.requestMethod = "POST"
                connection.doOutput = true

                // POST 데이터 작성
                val postData = URLEncoder.encode("plantid", "UTF-8") + "=" + URLEncoder.encode(plantid.toString(), "UTF-8") + "&" +
                        URLEncoder.encode("pname", "UTF-8") + "=" + URLEncoder.encode(pname, "UTF-8") + "&" +
                        URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8") + "&" +
                        URLEncoder.encode("ddate", "UTF-8") + "=" + URLEncoder.encode(ddate, "UTF-8") + "&" +
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
    private fun replaceFragment(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragment = fragment
        val bundle = Bundle()

        bundle.putString("userEmail", userEmail)
        bundle.putString("plantName", plantName)
        bundle.putInt("plantId", plantId)
        bundle.putString("diaryDate", dDate)

        // Fragment에 Bundle을 설정
        fragment.arguments = bundle

        // FragmentTransaction을 사용하여 PlantEnrollFragment로 전환
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // 이전 Fragment로 돌아갈 수 있도록 back stack에 추가
        transaction.commit() // 변경 사항을 적용
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