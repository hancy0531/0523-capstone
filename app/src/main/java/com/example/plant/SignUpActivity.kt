package com.example.plant

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
//회원가입
import java.io.InputStreamReader
import java.io.OutputStreamWriter
//로그인
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPwd: EditText
    private lateinit var alarmPwd2: TextView
    private lateinit var alarmPwd: TextView
    private lateinit var alarmEmail: TextView
    private lateinit var alarmBirth: TextView
    private lateinit var login:TextView
    private lateinit var signupbtn: Button
    private lateinit var birth: EditText
    private val pattern = android.util.Patterns.EMAIL_ADDRESS

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        id = findViewById(R.id.nickName)
        email = findViewById(R.id.email)
        password = findViewById(R.id.signUpPassword)
        confirmPwd = findViewById(R.id.confirmPassword)
        alarmPwd2 = findViewById(R.id.alarmPwd3)
        alarmPwd = findViewById(R.id.alarmPwd2)
        alarmEmail = findViewById(R.id.alarmEmail)
        login = findViewById(R.id.goToLogin)
        signupbtn = findViewById(R.id.signUpBtn)
        birth = findViewById(R.id.birth)
        alarmBirth = findViewById(R.id.alarmBirth)
        val intent = Intent(this, MainActivity::class.java)

        login.setOnClickListener {
            startActivity(intent)
        }
        signupbtn.setOnClickListener {
            val uid = id.text.toString()
            val upw = password.text.toString()
            val uemail = email.text.toString()
            val confrimPwd = confirmPwd.text.toString()
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy / MM / dd", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)
            val joindate = formattedDate
            val ubirth = birth.text.toString()
            if(uemail.isNotEmpty()&&uid.isNotEmpty()&&upw.isNotEmpty()&&confrimPwd.isNotEmpty()&&ubirth.isNotEmpty()){
            if(pattern.matcher(uemail).matches()){
                if(isValidPassword(upw)){
                    if(confrimPwd == upw){
                    if(isValidBirthDate(ubirth)){
                    //중복이메일 체크
                    GlobalScope.launch(Dispatchers.IO) {
                        checkExistingId(uid, upw, uemail, joindate, ubirth)
                    }
                    }else{
                        AlertDialog.Builder(this)
                            .setTitle("With P")
                            .setMessage("생년월일을 형식(yyyyMMdd)에 맞게 입력해주세요")
                            .setPositiveButton("확인") { dialog, _ ->
                                dialog.dismiss() // 다이얼로그 닫기
                            }
                            .show()
                    }
                    }else{
                        AlertDialog.Builder(this)
                            .setTitle("With P")
                            .setMessage("비밀번호가 불일치합니다.")
                            .setPositiveButton("확인") { dialog, _ ->
                                dialog.dismiss() // 다이얼로그 닫기
                            }
                            .show()
                    }
                }else{
                    AlertDialog.Builder(this)
                        .setTitle("With P")
                        .setMessage("영문, 숫자, 특수문자 조합으로 10자리 이상 입력해주세요. ")
                        .setPositiveButton("확인") { dialog, _ ->
                            dialog.dismiss() // 다이얼로그 닫기
                        }
                        .show()
                }
            }else{
                AlertDialog.Builder(this)
                    .setTitle("With P")
                    .setMessage("이메일 형식이 올바르지 않습니다.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss() // 다이얼로그 닫기
                    }
                    .show()
            }
            }else{
                AlertDialog.Builder(this)
                    .setTitle("With P")
                    .setMessage("빈칸없이 입력해주세요.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss() // 다이얼로그 닫기
                    }
                    .show()
                }
        }
        password.addTextChangedListener {

            var password = password.text.toString()
            if (password.isNotEmpty()) {
                MainActivity.checkValidation(password, alarmPwd)
            }
            else{
                alarmPwd.visibility = View.GONE
            }
        }
        password.addTextChangedListener { newPassword ->
            val passwordText = newPassword.toString()
            val confirmPwdText = confirmPwd.text.toString()
            if (confirmPwdText.isNotEmpty() && passwordText.isNotEmpty()) {
                if (confirmPwdText == passwordText) {
                    alarmPwd2.text = "비밀번호가 일치합니다."
                    alarmPwd2.visibility = View.VISIBLE
                } else {
                    alarmPwd2.text = "비밀번호가 불일치합니다."
                    alarmPwd2.visibility = View.VISIBLE
                }
            } else {
                alarmPwd2.visibility = View.GONE
            }
        }

        confirmPwd.addTextChangedListener { newConfirmPwd ->
            val passwordText = password.text.toString()
            val confirmPwdText = newConfirmPwd.toString()
            if (passwordText.isNotEmpty() && confirmPwdText.isNotEmpty()) {
                if (confirmPwdText == passwordText) {
                    alarmPwd2.text = "비밀번호가 일치합니다."
                    alarmPwd2.visibility = View.VISIBLE
                } else {
                    alarmPwd2.text = "비밀번호가 불일치합니다."
                    alarmPwd2.visibility = View.VISIBLE
                }
            } else {
                alarmPwd2.visibility = View.GONE
            }
        }
        email.addTextChangedListener {
            var email = email.text.toString()
            if (email.isNotEmpty()) {
                if (pattern.matcher(email).matches()) {
                    alarmEmail.visibility = View.GONE
                } else {
                    alarmEmail.text = "이메일 형식이 올바르지 않습니다."
                    alarmEmail.visibility = View.VISIBLE
                }
            } else {
                alarmEmail.visibility = View.GONE
            }
        }
        birth.addTextChangedListener{
            var birth = birth.text.toString()
            if(birth.isNotEmpty()) {
                if(isValidBirthDate(birth)&&birth.length==8) {
                    alarmBirth.visibility = View.GONE
                }else{
                    alarmBirth.visibility = View.VISIBLE
                }
            }else{
                alarmBirth.visibility = View.GONE
                }
            }
        }

    //중복이메일 체크 기능
    private fun checkExistingId(uid:String, upw:String, uemail:String, joindate: String, ubirth: String) {
        try {
            // PHP 스크립트의 URL
            val url = URL("http://10.0.2.2/checkid.php")

            // HttpURLConnection 열기
            val connection = url.openConnection() as HttpURLConnection

            // POST 요청 설정
            connection.requestMethod = "POST"
            connection.doOutput = true

            // 데이터 작성
            val postData = URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8")
            val outputStream = OutputStreamWriter(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()

            // 응답 코드 확인
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 응답 데이터를 읽어옴
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readLine()

                // 이미 존재하는 이메일인지 확인
                if (response == "Already Exist") {
                    runOnUiThread {
                        AlertDialog.Builder(this)
                            .setTitle("With P")
                            .setMessage("이미 존재하는 이메일입니다.")
                            .setPositiveButton("확인"
                            ) { dialog, which -> Log.d("MyTag", "positive") }
                            .create()
                            .show()
                    }
                } else {
                    // 회원가입 처리를 진행
                    signup(uid, upw, uemail, joindate, ubirth)
                }
            } else {
                // 요청이 실패한 경우
                runOnUiThread {
                    Toast.makeText(this, "서버 연결 실패", Toast.LENGTH_SHORT).show()
                }
            }

            // 연결 종료
            connection.disconnect()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "catch", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //회원가입 기능
    private fun signup(uid:String, upw:String, uemail:String, joindate:String, ubirth:String) {
        try {
            val url = URL("http://10.0.2.2/signup.php")

            // HttpURLConnection 열기
            val connection = url.openConnection() as HttpURLConnection

            // POST 요청 설정
            connection.requestMethod = "POST"
            connection.doOutput = true
            val defaultImageUrl ="http://10.0.2.2/uploads/defaultProfile.png"
            val defaultIntroduce = ""
            // 데이터 작성
            var postData = URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8")
            postData += "&" + URLEncoder.encode("upw", "UTF-8") + "=" + URLEncoder.encode(upw, "UTF-8")
            postData += "&" + URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8")
            postData += "&" + URLEncoder.encode("joindate", "UTF-8") + "=" + URLEncoder.encode(joindate, "UTF-8")
            postData += "&" + URLEncoder.encode("ubirth", "UTF-8") + "=" + URLEncoder.encode(ubirth, "UTF-8")
            postData += "&" + URLEncoder.encode("imageurl", "UTF-8") + "=" + URLEncoder.encode(defaultImageUrl, "UTF-8")
            postData += "&" + URLEncoder.encode("introduce", "UTF-8") + "=" + URLEncoder.encode(defaultIntroduce, "UTF-8")
            val outputStream = OutputStreamWriter(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()

            // 응답 코드 확인
            val intent = Intent(this, MainActivity::class.java)
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 성공적으로 요청이 처리됨
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle("With P")
                        .setMessage("회원가입 되었습니다.")
                        .setPositiveButton("확인"
                        ) { dialog, which -> startActivity(intent) }
                        .create()
                        .show()
                }
            } else {
                // 요청이 실패한 경우
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle("With P")
                        .setMessage("회원가입 실패했습니다.")
                        .setPositiveButton("확인"
                        ) { dialog, which -> Log.d("MyTag", "positive") }
                        .create()
                        .show()
                }
            }

            // 연결 종료
            connection.disconnect()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle("With P")
                    .setMessage("회원가입 실패했습니다.")
                    .setPositiveButton("확인"
                    ) { dialog, which -> Log.d("MyTag", "positive") }
                    .create()
                    .show()
            }
        }
    }
}fun isValidPassword(password: String): Boolean {
    val symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])"
    // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
    val alpha = "(?=.*[a-zA-Z])"
    // 정규표현식 컴파일
    val length = 10
    val patternSymbol = Pattern.compile(symbol)
    val patternAlpha = Pattern.compile(alpha)

    val matcherSymbol = patternSymbol.matcher(password)
    val matcherAlpha = patternAlpha.matcher(password)

    val isValid = matcherSymbol.find() && matcherAlpha.find() && password.length >= length
    return isValid
}
@RequiresApi(Build.VERSION_CODES.O)
fun isValidBirthDate(birthDate: String): Boolean {
    if(birthDate.length!=8){
        return false
    }
    // 생년월일 형식 검사 (yyyyMMdd)
    val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.getDefault())
    try {
        // 날짜 파싱 시도
        val parsedDate = LocalDate.parse(birthDate, dateFormat)
        // 현재 날짜와 비교하여 미래 날짜인지 확인 (미래 날짜는 유효하지 않은 생년월일)
        val currentDate = LocalDate.now()
        if (parsedDate.isAfter(currentDate)) {
            return false
        }
    } catch (e: Exception) {
        // 날짜 형식이 잘못된 경우
        return false
    }

    // 생년월일이 유효한 경우
    return true
}