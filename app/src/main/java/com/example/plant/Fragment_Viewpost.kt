package com.example.plant

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Fragment_Viewpost : Fragment() {
    private lateinit var FreeBoardFragment: Fragment_FreeBoard
    private lateinit var listbtn: Button
    private lateinit var editbtn: Button
    private lateinit var deletebtn: Button
    private lateinit var comment_btn: Button
    private lateinit var comment_text: EditText
    private lateinit var post_image: ImageView
    private lateinit var writer_image: CircleImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestQueue: RequestQueue
    private var userEmail: String? = null
    private var post_num: String? = null
    private var board_type: String? = null
    private var post_title: String? = null
    private var post_content: String? = null
    private var post_writer: String? = null
    private var post_date: String? = null
    private var is_equal:Boolean? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_post, container, false)
        arguments?.let {
            post_num = it.getString("post_num")
            board_type = it.getString("board_type")
            post_title = it.getString("post_title")
            post_content = it.getString("post_content")
            post_writer = it.getString("post_writer")
            post_date = it.getString("post_date")
            userEmail = it.getString("userEmail")
            is_equal = it.getBoolean("is_equal")  // 수정 가능 여부 가져오기
        }

        Log.d("post_num: ","$post_num")
        Log.d("board_type: ","$board_type")
        Log.d("post_title: ","$post_title")
        Log.d("post_content: ","$post_content")
        Log.d("post_writer: ","$post_writer")
        Log.d("post_date: ","$post_date")
        Log.d("userEmail: ","$userEmail")
        Log.d("is_equal: ","$is_equal")

        //기존 내용 불러오기
        view.findViewById<TextView>(R.id.viewpost_writer).text = post_writer
        view.findViewById<TextView>(R.id.viewpost_date).text = post_date
        view.findViewById<TextView>(R.id.viewpost_title).text = post_title
        view.findViewById<TextView>(R.id.viewpost_content).text = post_content

        //게시물 작성자 프로필 불러오기
        writer_image = view.findViewById(R.id.writerImage)
        post_writer?.let { getProfileImage(it) }

        //이미지 불러오기
        post_image = view.findViewById(R.id.viewpost_image)
        post_num?.let { showImageById(it.toInt()) }

        // 수정 가능 여부에 따라 버튼 보이기/숨기기 처리
        if (is_equal == true) {
            // 수정 가능한 경우 버튼 보이기
            view.findViewById<Button>(R.id.editButton).visibility = View.VISIBLE
            view.findViewById<Button>(R.id.deleteButton).visibility = View.VISIBLE
        } else {
            // 수정 불가능한 경우 버튼 숨기기
            view.findViewById<Button>(R.id.editButton).visibility = View.GONE
            view.findViewById<Button>(R.id.deleteButton).visibility = View.GONE
        }

        //리싸이클러뷰
        recyclerView = view.findViewById(R.id.comment_recyclerView)
        //postAdapter = PostAdapter(getSamplePostList())
        //recyclerView.adapter = postAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Volley 요청 큐 초기화
        requestQueue = Volley.newRequestQueue(requireContext())
        // 게시물 데이터를 가져오는 함수 호출
        post_num?.let { getComments(it.toInt()) }




        listbtn = view.findViewById(R.id.listbtn)
        listbtn.setOnClickListener {
            // 클릭 이벤트 처리, 해당하는 프래그먼트로 변경
            val fragment = Fragment_FreeBoard().apply {
                arguments = Bundle().apply {
                    //putString("board_type", board_type) // board_type 데이터 전달
                    putString("userEmail", post_writer) // writer 데이터 전달
                    board_type?.let { it1 -> putInt("board_type", it1.toInt()) } //board_type 데이터 전달
                }
            }
            replaceFragment(fragment)
        }

        editbtn = view.findViewById(R.id.editButton)
        editbtn.setOnClickListener {
//            val fragment = Fragment_WriteFreePost().apply {
//                arguments = Bundle().apply {
//                    //putString("board_type", board_type) // board_type 데이터 전달
//                    putString("userEmail", post_writer) // writer 데이터 전달
//                    board_type?.let { it1 -> putInt("board_type", it1.toInt()) } //board_type 데이터 전달
//                    post_num?.let { it1 -> putInt("post_num", it1.toInt()) }
//                    putString("post_title", post_title)
//                    putString("post_content", post_content)
//                    putString("task","edit")
//                }
//            }
            replaceFragment2(Fragment_WriteFreePost())
        }

        deletebtn = view.findViewById(R.id.deleteButton)
        deletebtn.setOnClickListener {
            //post_num?.let { it1 -> deletePost(it1.toInt()) }
            // AlertDialog를 생성하여 사용자에게 삭제 여부를 확인
            AlertDialog.Builder(requireContext())
                .setTitle("With P")
                .setMessage("정말로 이 게시물을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    // 사용자가 확인을 클릭하면 삭제 함수 호출
                    post_num?.let { it1 -> deletePost(it1.toInt()) }
                }
                .setNegativeButton("취소", null)
                .show()

        }

        comment_text = view.findViewById(R.id.comment_text)
        comment_btn = view.findViewById(R.id.comment_send)
        comment_btn.setOnClickListener {
            val commentContent = comment_text.text.toString()
            // 댓글 내용이 비어 있는지 확인
            if (commentContent.isNotEmpty()) {
                // 댓글 추가 함수 호출
                addComment(commentContent)
                // 키보드 닫기
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            } else {
                // 댓글 내용이 비어 있으면 사용자에게 알림
                Toast.makeText(requireContext(), "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

    //게시물 작성자 프로필 이미지 가져오기
    private fun getProfileImage(postWriter: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2/getprofileimage.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(postWriter, "UTF-8")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    inputStream.close()

                    // 응답에서 이미지 URL 추출
                    val jsonResponse = JSONObject(response.toString())
                    if (jsonResponse.has("imageurl")) {
                        val imageUrl = jsonResponse.getString("imageurl")

                        // UI 스레드에서 Glide를 사용하여 이미지 로드
                        withContext(Dispatchers.Main) {
                            Glide.with(this@Fragment_Viewpost)
                                .load(imageUrl)
                                .into(writer_image)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "No image URL found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("ImageUrl", "Failed to get image URL")
                }
            } catch (e: Exception) {
                Log.e("ImageUrl", "Error loading image URL", e)
            }
        }
    }

    //댓글 추가하기
    private fun addComment(commentContent: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2/addcomment.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                // POST 요청에 필요한 데이터 설정
                val postData = URLEncoder.encode("post_num", "UTF-8") + "=" + URLEncoder.encode(post_num, "UTF-8") +
                        "&" + URLEncoder.encode("comment_content", "UTF-8") + "=" + URLEncoder.encode(commentContent, "UTF-8") +
                        "&" + URLEncoder.encode("comment_writer", "UTF-8") + "=" + URLEncoder.encode(userEmail, "UTF-8")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 댓글 추가 성공
                    // 원하는 동작 수행 (예: RecyclerView 업데이트)
                    post_num?.let { getComments(it.toInt()) }
                    // UI 스레드로 전환하여 RecyclerView를 업데이트
                    withContext(Dispatchers.Main) {
                        comment_text.setText("") // 댓글 입력란 초기화
                    }
                } else {
                    // 댓글 추가 실패
                    // 에러 처리
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "댓글 추가 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //댓글 불러오기
    private fun getComments(postNum: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2/getcomments.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                // post_num 파라미터 추가
                val postData = URLEncoder.encode("post_num", "UTF-8") + "=" + URLEncoder.encode(postNum.toString(), "UTF-8")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                bufferedReader.close()

                // 서버에서 받은 JSON 문자열을 JSONArray로 변환
                val jsonArray = JSONArray(response.toString())

                // UI 스레드로 전환하여 RecyclerView에 데이터를 설정
                withContext(Dispatchers.Main) {
                    val commentList = mutableListOf<Comment>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val comment = Comment(
                            jsonObject.getInt("comment_num"),
                            jsonObject.getInt("post_num"),
                            jsonObject.getString("comment_content"),
                            jsonObject.getString("comment_writer"),
                            jsonObject.getString("comment_date")
                        )
                        commentList.add(comment)
                    }
                    val commentAdapter = CommentAdapter(requireContext(),commentList, userEmail)
                    recyclerView.adapter = commentAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                    if (bitmap != null) {
                        // 이미지가 null이 아닌 경우 ImageView에 이미지 설정
                        post_image.visibility = View.VISIBLE // ImageView 보이기
                        post_image.setImageBitmap(bitmap)
                    } else {
                        // 이미지가 null인 경우 ImageView 숨기기
                        post_image.visibility = View.GONE
                    }
                    //post_image.setImageBitmap(bitmap)
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


    //레트로핏 이용한 게시물 삭제
//    private fun deletePost(post_num: Int) {
//        // Retrofit 인터페이스 생성
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://10.0.2.2/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(ApiService::class.java)
//
//        // 서버에 삭제 요청 보내기
//        apiService.deletePost(post_num).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    // 게시물 삭제 성공
//                    //replaceFragment(Fragment_FreeBoard())
//                    requireActivity().runOnUiThread {
//                        replaceFragment(Fragment_FreeBoard())
//                    }
//                } else {
//                    // 게시물 삭제 실패
//                    Toast.makeText(requireContext(), "delete post failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                // 오류 처리
//                t.printStackTrace()
//            }
//        })
//    }



    private fun deletePost(postNum: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2/deletepost.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = URLEncoder.encode("post_num", "UTF-8") + "=" + URLEncoder.encode(postNum.toString(), "UTF-8")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 게시물 삭제 성공
                    replaceFragment(Fragment_FreeBoard())

                } else {
                    // 게시물 삭제 실패
                    Toast.makeText(requireContext(), "delete post failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val bundle = Bundle().apply {
            putString("userEmail", post_writer)
            board_type?.let { putInt("board_type", it.toInt()) }
        }
        fragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment) // container는 프래그먼트가 표시될 영역의 ID
        transaction.addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 화면으로 돌아갈 수 있도록 스택에 추가
        transaction.commit()
    }

    private fun replaceFragment2(fragment: Fragment) {
        val bundle = Bundle().apply {
            putString("userEmail", post_writer) // writer 데이터 전달
            board_type?.let { it1 -> putInt("board_type", it1.toInt()) } //board_type 데이터 전달
            post_num?.let { it1 -> putInt("post_num", it1.toInt()) }
            putString("post_title", post_title)
            putString("post_content", post_content)
            putString("task","edit")
        }
        fragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment) // container는 프래그먼트가 표시될 영역의 ID
        transaction.addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 화면으로 돌아갈 수 있도록 스택에 추가
        transaction.commit()
    }


}