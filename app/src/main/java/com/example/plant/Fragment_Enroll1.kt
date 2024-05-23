package com.example.plant

//import ApiService
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat

class Fragment_Enroll1 : Fragment() {
    private lateinit var name: EditText
    private var userEmail: String? = null
    private lateinit var bundle: Bundle
    private lateinit var image: ImageView
    private lateinit var camera: ImageView
    private lateinit var gallery: ImageView
    private var selectedImageUri: Uri? = null
    //var plantDataListener: DataListener? = null
    private lateinit var viewModel: PlantEnrollViewModel


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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_enroll1, container, false)
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE
        image = view.findViewById(R.id.imageView3)
        name = view.findViewById(R.id.plantName)
        camera = view.findViewById(R.id.camera2)
        gallery = view.findViewById(R.id.gallery2)
        arguments?.let {
            userEmail = it.getString("userEmail")
        }
        bundle = Bundle()
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
        //plantDataListener = (activity as? DataListener)
        viewModel = ViewModelProvider(requireActivity()).get(PlantEnrollViewModel::class.java)
        /*
        userEmail?.let {
            Toast.makeText(requireContext(), "User Email: $userEmail", Toast.LENGTH_SHORT).show()
        }*/
        return view
    }

    override fun onPause() {
        super.onPause()
        //plantDataListener?.onNameReceived(plantName)
        viewModel.pname = name.text.toString()
        viewModel.imageuri = selectedImageUri
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
        val resolver = requireContext().contentResolver
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
    private fun newFileName(): String{
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }
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