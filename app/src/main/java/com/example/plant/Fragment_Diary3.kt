package com.example.plant

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Fragment_Diary3: Fragment() {
    private lateinit var image: ImageView
    private lateinit var date: TextView
    private lateinit var name: TextView
    private lateinit var content: TextView
    private lateinit var title: TextView
    private lateinit var beforBtn: Button
    private lateinit var editBtn: Button
    private lateinit var deleteBtn: Button
    private var dDate: String = ""
    private var plantId: Int = 0
    private var diaryId: Int = 0
    private var userEmail: String = ""
    private var plantName: String = ""
    private var enrollTime: String = ""
    private var diaryTitle: String = ""
    private var diaryContent: String = ""
    private var imageUrl: String = "http://10.0.2.2/uploads/default.png"
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary3, container, false)
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
        image = view.findViewById(R.id.detailImageView)
        date = view.findViewById(R.id.detailDate)
        title =view.findViewById(R.id.detailTitle)
        content =view.findViewById(R.id.detailContent)
        name = view.findViewById(R.id.detailName)
        beforBtn = view.findViewById(R.id.beforBtn2)
        editBtn = view.findViewById(R.id.editBtn)
        deleteBtn = view.findViewById(R.id.deleteBtn)

        date.text = enrollTime
        title.text = diaryTitle
        content.text = diaryContent
        name.text = plantName

        Glide.with(requireContext())
            .load(imageUrl) // 이미지 URL
            .into(image) // 이미지뷰에 로드된 이미지 설정
        deleteBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("With P")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialog, _ ->
                    deleteDiary(diaryId)
                    dialog.dismiss() // 다이얼로그 닫기
                }
                .show()
        }
        beforBtn.setOnClickListener {
            Log.d("Fragment_Diary3", "Before button clicked: diaryDate=$dDate, plantId=$plantId, userEmail=$userEmail, plantName=$plantName")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            val fragment = Fragment_Diary1()
            val bundle = Bundle()

            // Bundle에 데이터를 담기
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
        editBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            val fragment = Fragment_Diary4()
            val bundle = Bundle()

            // Bundle에 데이터를 담기
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
        return view
        }
    private fun deleteDiary(diaryid: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // PHP 스크립트의 URL
                val url = URL("http://10.0.2.2/deletediary.php")

                // HttpURLConnection 열기
                val connection = url.openConnection() as HttpURLConnection

                // POST 요청 설정
                connection.requestMethod = "POST"
                connection.doOutput = true

                // POST 데이터 작성
                val postData = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(diaryid.toString(), "UTF-8")
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
                if (result == "delete successful") {
                    launch(Dispatchers.Main) {
                        replaceFragment(Fragment_Diary1())//Fragment_Diary1 으로 넘어감
                    }
                } else {
                    launch(Dispatchers.Main) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("With P")
                            .setMessage("삭제 실패")
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
}