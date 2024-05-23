package com.example.plant

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

//댓글 리사이클러뷰 어댑터
data class Comment(
    val comment_num: Int,
    val post_num: Int,
    val comment_content: String,
    val comment_writer: String,
    val comment_date: String
)
class CommentAdapter(private val context: Context, private val commentList: MutableList<Comment>, private val currentUser: String?) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.bind(comment)
    }

    override fun getItemCount() = commentList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val writerTextView: TextView = itemView.findViewById(R.id.comment_writer)
        val contentTextView: TextView = itemView.findViewById(R.id.comment_content)
        val dateTextView: TextView = itemView.findViewById(R.id.comment_date)
        val editTextView: TextView = itemView.findViewById(R.id.edit_comment) // 수정 버튼
        val deleteTextView: TextView = itemView.findViewById(R.id.delete_comment) // 삭제 버튼
        val writerImageView: CircleImageView = itemView.findViewById(R.id.user_image) //댓글 작성자 프로필 이미지뷰

        fun bind(comment: Comment) {
            writerTextView.text = comment.comment_writer
            contentTextView.text = comment.comment_content
            dateTextView.text = comment.comment_date

            // 현재 사용자와 댓글 작성자가 동일한 경우 수정 및 삭제 버튼 표시
            if (currentUser == comment.comment_writer) {
                editTextView.visibility = View.VISIBLE
                deleteTextView.visibility = View.VISIBLE
            } else {
                editTextView.visibility = View.GONE
                deleteTextView.visibility = View.GONE
            }

            //댓글 작성자의 이미지 로드
            loadImage(comment.comment_writer, writerImageView)

            // 수정 버튼 클릭 리스너 설정
            editTextView.setOnClickListener {
                // 댓글 수정 기능 호출
                //editComment(comment.comment_num)
                showEditDialog(comment)
            }

            // 삭제 버튼 클릭 리스너 설정
            deleteTextView.setOnClickListener {
                // 댓글 삭제 기능 호출
                //deleteComment(comment)
                //댓글 삭제 기능 호출 전에 확인 다이얼로그 표시
                AlertDialog.Builder(context)
                    .setTitle("댓글 삭제")
                    .setMessage("정말로 이 댓글을 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { dialog, which ->
                        // 사용자가 확인을 클릭하면 삭제 함수 호출
                        deleteComment(comment)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }

        }

        //댓글 작성자 프로필 가져오기
        private fun loadImage(uemail: String, imageView: CircleImageView) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val url = URL("http://10.0.2.2/getprofileimage.php")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.doOutput = true

                    val postData = URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8")

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

                        val jsonResponse = JSONObject(response.toString())
                        if (jsonResponse.has("imageurl")) {
                            val imageUrl = jsonResponse.getString("imageurl")
                            withContext(Dispatchers.Main) {
                                Glide.with(context)
                                    .load(imageUrl)
                                    .into(imageView)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                // 이미지 URL이 없는 경우 처리
                            }
                        }
                    } else {
                        // 요청 실패 처리
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }

    //수정 다이얼로그
    private fun showEditDialog(comment: Comment) {
        val editView = LayoutInflater.from(context).inflate(R.layout.edit_comment_dialog, null)
        val editText = editView.findViewById<EditText>(R.id.edit_comment_content)
        editText.setText(comment.comment_content)

        AlertDialog.Builder(context)
            .setTitle("댓글 수정")
            .setView(editView)
            .setPositiveButton("수정") { _, _ ->
                val newContent = editText.text.toString()
                if (newContent.isNotBlank()) {
                    editComment(comment, newContent)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    //댓글 수정 기능
    private fun editComment(comment: Comment, newContent: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2/editcomment.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = URLEncoder.encode("comment_num", "UTF-8") + "=" + URLEncoder.encode(comment.comment_num.toString(), "UTF-8") +
                        "&" + URLEncoder.encode("comment_content", "UTF-8") + "=" + URLEncoder.encode(newContent, "UTF-8")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 댓글 수정 성공
                    // UI 스레드로 전환하여 RecyclerView 업데이트
                    withContext(Dispatchers.Main) {
                        val position = commentList.indexOf(comment)
                        if (position != -1) {
                            commentList[position] = comment.copy(comment_content = newContent)
                            notifyItemChanged(position)
                        }
                    }
                } else {
                    // 댓글 수정 실패
                    // 에러 처리
                    withContext(Dispatchers.Main) {
                        //Toast.makeText(context, "댓글 수정 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun deleteComment(comment: Comment) {
        // 서버로 댓글 번호를 전송하여 삭제
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2/deletecomment.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = URLEncoder.encode("comment_num", "UTF-8") + "=" + URLEncoder.encode(comment.comment_num.toString(), "UTF-8")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 댓글 삭제 성공
                    // UI 스레드로 전환하여 RecyclerView 업데이트
                    withContext(Dispatchers.Main) {
                        val position = commentList.indexOf(comment)
                        if (position != -1) {
                            commentList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                } else {
                    // 댓글 삭제 실패
                    // 에러 처리
                    withContext(Dispatchers.Main) {
                        //Toast.makeText(itemView.context, "댓글 삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




}