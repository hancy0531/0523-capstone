package com.example.plant

import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
//로그인
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import org.json.JSONObject
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var password: EditText
    private lateinit var signUp: TextView
    private lateinit var signInBtn: Button
    private lateinit var alarmId: TextView
    private lateinit var alarmPwd: TextView
    private var userName: String = ""
    private var userEmail: String = ""
    private var imageUrl: String = ""
    private val pattern = android.util.Patterns.EMAIL_ADDRESS

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        id = findViewById(R.id.id)
        password = findViewById(R.id.password)
        signUp = findViewById(R.id.signUp)
        signInBtn = findViewById(R.id.signInBtn)
        alarmId = findViewById(R.id.alarmId)
        alarmPwd = findViewById(R.id.alarmPwd)

        val intent = Intent(this, SignUpActivity::class.java)
        val intent2 = Intent(this, HomeActivity::class.java)
        //로그인
        signInBtn.setOnClickListener {
            val uemail = id.text.toString()
            val upw = password.text.toString()
            GlobalScope.launch(Dispatchers.IO) {
                login(uemail, upw)
            }
            //startActivity(intent2)
        }
        //
        id.addTextChangedListener {
            var id = id.text.toString()
            if (id.isNotEmpty()) {
                if (pattern.matcher(id).matches()) {
                    alarmId.visibility = View.GONE
                } else {
                    alarmId.visibility = View.VISIBLE
                }
            } else {
                alarmId.visibility = View.GONE
            }
        }
        password.addTextChangedListener {
            var pwd = password.text.toString()
            if (pwd.isNotEmpty()) {
                checkValidation(pwd, alarmPwd)
            } else {
                alarmPwd.visibility = View.GONE
            }
        }
        signUp.setOnClickListener {
            startActivity(intent)
        }
    }
    //로그인 기능
    private fun login(uemail: String, upw: String) {
        val intent = Intent(this, SignUpActivity::class.java)
        val intent2 = Intent(this, HomeActivity::class.java)

            // PHP 스크립트의 URL
            val url = URL("http://10.0.2.2/login.php")

            // HttpURLConnection 열기
            val connection = url.openConnection() as HttpURLConnection

            // POST 요청 설정
            connection.requestMethod = "POST"
            connection.doOutput = true

            var postData = URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8")
            postData += "&" + URLEncoder.encode("upw", "UTF-8") + "=" + URLEncoder.encode(upw, "UTF-8")
            val outputStream = OutputStreamWriter(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()
            outputStream.close()

            val inputStream: BufferedReader
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = BufferedReader(InputStreamReader(connection.inputStream))
            } else {
                inputStream = BufferedReader(InputStreamReader(connection.errorStream))
            }

            val response = StringBuilder()
            var line: String?
            while (inputStream.readLine().also { line = it } != null) {
                response.append(line)
            }
            inputStream.close()

            val jsonResponse = JSONObject(response.toString())
            val status = jsonResponse.getString("status")
            if (status == "Login successful") {
                val dataObject = jsonResponse.getJSONObject("data")
                runOnUiThread {
                    handleSuccess(dataObject) //userName에 db의 uid 가져와서 저장
                    intent2.putExtra("userName", userName)
                    //메인화면으로 넘어감
                    intent2.putExtra("userEmail", userEmail)
                    intent2.putExtra("imageUrl", imageUrl)
                    startActivity(intent2)
                }
                // 가져온 데이터를 처리

            } else {
                // 실패 처리
                handleFailure()
            }

    }
    private fun handleSuccess(dataObject: JSONObject) {
        //userItem = UserItem()
        userName = dataObject.getString("uid")
        userEmail = dataObject.getString("uemail")
        imageUrl = dataObject.getString("imageurl")

    }

    // 데이터 가져오기가 실패한 경우 처리할 로직
    private fun handleFailure() {

    }
    //
    companion object {
        fun checkValidation(password: String, alarmPwd: TextView){
            // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
            val symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])"
            // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
            val alpha = "(?=.*[a-zA-Z])"
            // 정규표현식 컴파일
            val length = 10
            val patternSymbol = Pattern.compile(symbol)
            val patternAlpha = Pattern.compile(alpha)

            val matcherSymbol = patternSymbol.matcher(password)
            val matcherAlpha = patternAlpha.matcher(password)

            if (matcherSymbol.find() && matcherAlpha.find() && password.length >= length) {
                alarmPwd.visibility = View.GONE
            } else {
                alarmPwd.visibility = View.VISIBLE
            }

        }
    }
}