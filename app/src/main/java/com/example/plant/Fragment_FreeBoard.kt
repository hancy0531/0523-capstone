package com.example.plant

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import org.json.JSONException

class Fragment_FreeBoard : Fragment() {
    private lateinit var WriteFreePostFragment: Fragment_WriteFreePost
    private lateinit var floatingActionMenu: FloatingActionMenu
    private lateinit var fab1: FloatingActionButton
    private lateinit var fab2: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var requestQueue: RequestQueue
//    private lateinit var writepostbtn: Button
//    private lateinit var my_post: TextView
    private var userEmail: String? = null
    private var board_type: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_free_board, container, false)
        // arguments에서 userEmail 데이터 받기
        userEmail = arguments?.getString("userEmail")
        board_type = arguments?.getInt("board_type")

        // board_type에 따라 boardname 설정
        val boardNameTextView: TextView = view.findViewById(R.id.boardname)
        when (board_type) {
            1 -> boardNameTextView.text = "자유게시판"
            2 -> boardNameTextView.text = "자랑게시판"
            3 -> boardNameTextView.text = "분양/나눔 게시판"
            4 -> boardNameTextView.text = "용품정보 게시판"
            5 -> boardNameTextView.text = "식물정보 게시판"
            6 -> boardNameTextView.text = "질문게시판"
        }

//        val bundle = Bundle().apply {
//            putString("userEmail", userEmail)
//            board_type?.let { putInt("board_type", it) }
//            putString("task","write")
//        }
//        WriteFreePostFragment = Fragment_WriteFreePost().apply {
//            arguments = bundle
//        }
//        val writepostbtn = view.findViewById<FloatingActionButton>(R.id.write_post)
//        writepostbtn.setOnClickListener {
//            replaceFragment(WriteFreePostFragment)
//        }

        //FloatingActionMenu
        floatingActionMenu = view.findViewById(R.id.floatingActionMenu)
        fab1 = view.findViewById(R.id.fab1)
        fab2 = view.findViewById(R.id.fab2)

        // Set click listeners for each FAB
        fab1.setOnClickListener {
            //내 게시물만 보기
            getMyPosts()
            floatingActionMenu.close(true)
        }


        val bundle = Bundle().apply {
            putString("userEmail", userEmail)
            board_type?.let { putInt("board_type", it) }
            putString("task","write")
        }
        WriteFreePostFragment = Fragment_WriteFreePost().apply {
            arguments = bundle
        }
        fab2.setOnClickListener {
            //새 게시물 작성하기
            replaceFragment(WriteFreePostFragment)
        }



//        writepostbtn = view.findViewById(R.id.writepost)
//        writepostbtn.setOnClickListener {
//            // 클릭 이벤트 처리, 해당하는 프래그먼트로 변경
//            replaceFragment(WriteFreePostFragment)
//        }

        //리싸이클러뷰
        recyclerView = view.findViewById(R.id.recyclerView)
        //postAdapter = PostAdapter(getSamplePostList())
        //recyclerView.adapter = postAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Volley 요청 큐 초기화
        requestQueue = Volley.newRequestQueue(requireContext())
        // 게시물 데이터를 가져오는 함수 호출
        getPostData()

        //val post_searchView: androidx.appcompat.widget.SearchView = view.findViewById(R.id.post_search_view)
        val post_searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.post_search_view)
        post_searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                // 입력된 검색어를 사용하여 게시물 리스트를 필터링하고 어댑터를 업데이트합니다.
                //postAdapter.filter.filter(newText)
                (recyclerView.adapter as PostAdapter).filter.filter(newText)
                return true
            }
        })

//        my_post = view.findViewById(R.id.mypost)
//        my_post.setOnClickListener {
//            getMyPosts()
//        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment) // container는 프래그먼트가 표시될 영역의 ID
        transaction.addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 화면으로 돌아갈 수 있도록 스택에 추가
        transaction.commit()
    }

    private fun getPostData() {
        val url = "http://10.0.2.2/post.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, "$url?board_type=$board_type", null,
            Response.Listener { response ->
                val postList = mutableListOf<Post>()

                for (i in 0 until response.length()) {
                    try {
                        val postObject = response.getJSONObject(i)
                        val post = Post(
                            postObject.getInt("post_num"),
                            postObject.getInt("board_type"),
                            postObject.getString("post_title"),
                            postObject.getString("post_content"),
                            postObject.getString("post_writer"),
                            postObject.getString("post_date"),
                        )
                        postList.add(post)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                // 데이터를 어댑터에 설정
                postAdapter = PostAdapter(postList) { post ->
                    if (userEmail == post.post_writer) {
                        // 현재 사용자와 게시물의 작성자가 일치하면 수정 및 삭제 기능 활성화
                        // 클릭된 항목의 데이터를 전달하여 상세 화면 프래그먼트로 이동
                        val fragment = Fragment_Viewpost().apply {
                            arguments = Bundle().apply {
                                putString("post_num", post.post_num.toString())
                                putString("board_type", post.board_type.toString())
                                putString("post_title", post.post_title)
                                putString("post_content", post.post_content)
                                putString("post_writer", post.post_writer)
                                putString("post_date", post.post_date)
                                //현재 로그인된 사용자
                                putString("userEmail",userEmail)
                                putBoolean("is_equal", true)  // 수정 가능 여부를 전달 (수정 가능)
                            }
                        }
                        replaceFragment(fragment)
                    } else {
                        // 현재 사용자와 게시물의 작성자가 일치하지 않으면 수정 및 삭제 기능 비활성화
                        val fragment = Fragment_Viewpost().apply {
                            arguments = Bundle().apply {
                                putString("post_num", post.post_num.toString())
                                putString("board_type", post.board_type.toString())
                                putString("post_title", post.post_title)
                                putString("post_content", post.post_content)
                                putString("post_writer", post.post_writer)
                                putString("post_date", post.post_date)
                                //현재 로그인된 사용자
                                putString("userEmail",userEmail)
                                putBoolean("is_equal", false)  // 수정 가능 여부를 전달 (수정 불가능)
                            }
                        }
                        replaceFragment(fragment)
                    }
                }
                recyclerView.adapter = postAdapter
                postAdapter.filter.filter("") // 초기에는 모든 데이터를 보여줍니다.
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )

        // 요청을 큐에 추가
        requestQueue.add(jsonArrayRequest)
    }


    //내 게시물 가져오기
    private fun getMyPosts() {
        val url = "http://10.0.2.2/mypost.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, "$url?board_type=$board_type&user_email=$userEmail", null,
            Response.Listener { response ->
                val postList = mutableListOf<Post>()

                for (i in 0 until response.length()) {
                    try {
                        val postObject = response.getJSONObject(i)
                        val post = Post(
                            postObject.getInt("post_num"),
                            postObject.getInt("board_type"),
                            postObject.getString("post_title"),
                            postObject.getString("post_content"),
                            postObject.getString("post_writer"),
                            postObject.getString("post_date"),
                        )
                        postList.add(post)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                if (postList.isEmpty()) {
                    // 게시물이 없는 경우 처리
                    showNoPostsDialog()
                } else {
                    // 게시물이 있는 경우 어댑터에 설정
                    postAdapter = PostAdapter(postList) { post ->
                        // 클릭된 항목의 데이터를 전달하여 상세 화면 프래그먼트로 이동
                        val fragment = Fragment_Viewpost().apply {
                            arguments = Bundle().apply {
                                putString("post_num", post.post_num.toString())
                                putString("board_type", post.board_type.toString())
                                putString("post_title", post.post_title)
                                putString("post_content", post.post_content)
                                putString("post_writer", post.post_writer)
                                putString("post_date", post.post_date)
                                //현재 로그인된 사용자
                                putString("userEmail", userEmail)
                                putBoolean("is_equal", userEmail == post.post_writer) // 수정 가능 여부를 전달
                            }
                        }
                        replaceFragment(fragment)
                    }
                    recyclerView.adapter = postAdapter
                    postAdapter.filter.filter("") // 초기에는 모든 데이터를 보여줍니다.
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )

        // 요청을 큐에 추가
        requestQueue.add(jsonArrayRequest)
    }

    private fun showNoPostsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("게시물 없음")
        builder.setMessage("작성한 게시물이 없습니다!")
        builder.setPositiveButton("확인") { dialog, which ->
            // 확인 버튼을 누르면 아무런 동작 없이 닫힙니다.
        }
        builder.show()
    }


}
