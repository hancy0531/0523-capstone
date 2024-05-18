package com.example.plant

import RecyclerViewDiaryAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Fragment_Diary1 : Fragment() {
    private lateinit var calender: CalendarView
    private lateinit var date: TextView
    private lateinit var nextBtn: Button
    private lateinit var plantNameTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private var choiceDate: String = ""
    private var userEmail: String? = null
    private var plantName: String? = null
    private var plantId: Int = 0
    private var dDate: String? = null
    private var dList: ArrayList<DiaryListItem> = ArrayList()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary1, container, false)
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        recyclerView = view.findViewById(R.id.diaryRecyclerView)
        calender = view.findViewById(R.id.calendarView)
        date = view.findViewById(R.id.date)
        nextBtn = view.findViewById(R.id.nextBtn)
        plantNameTextView = view.findViewById(R.id.plantNameText)
        arguments?.let {
            userEmail = it.getString("userEmail")
            plantName = it.getString("plantName")
            plantId = it.getInt("plantId")
            dDate = it.getString("diaryDate")
        }
        Log.d("Fragment_Diary3", "Before button clicked: diaryDate=$dDate, plantId=$plantId, userEmail=$userEmail, plantName=$plantName")
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy / M / d", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        plantNameTextView.text = plantName
        if(dDate == null){
            date.text = formattedDate
            dDate = formattedDate
        }else{
            date.text = dDate
            val parts = dDate!!.split(" / ")
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Subtract 1 since Calendar months are 0-based
            val day = parts[2].toInt()
            // Convert currentDate to milliseconds
            calender.setDate(getDateInMillis(year, month, day), true, true)
        }

        GlobalScope.launch(Dispatchers.IO) {
            plantId?.let { plantId ->
                dDate?.let { dDate ->
                    getDiaries(plantId, dDate)
                }
            }
        }
        // 현재 날짜를 표시하기 위한 작업

        val homeIntent = Intent(requireContext(), HomeActivity::class.java)

        nextBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            // PlantEnrollFragment 인스턴스를 생성
            val fragment = Fragment_Diary2()
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
        calender.setOnDateChangeListener { _, year, month, dayOfMonth ->
            //date.visibility = View.VISIBLE
            choiceDate = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            date.text = choiceDate
            dDate = choiceDate
            //dList.clear()
            GlobalScope.launch(Dispatchers.IO) {
                plantId?.let { plantId ->
                    choiceDate?.let { choiceDate ->
                        getDiaries(plantId, choiceDate)
                    }
                }
            }

        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewDiaryAdapter(dList)

        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)
        return view
    }
    /*
    override fun onResume() {
        super.onResume()
        // Reload the data when the fragment resumes
        reloadData()
    }
    private fun reloadData() {
        GlobalScope.launch(Dispatchers.IO) {
            plantId?.let { plantId ->
                dDate?.let { diaryDate ->
                    getDiaries(plantId, diaryDate)
                }
            }
        }
    }*/
    private fun getDiaries(plantid: Int, ddate: String) {
        val url = URL("http://10.0.2.2/getdiaries.php")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        val postData = URLEncoder.encode("plantid", "UTF-8") + "=" + URLEncoder.encode(plantid.toString(), "UTF-8")  + "&" +
                URLEncoder.encode("ddate", "UTF-8") + "=" + URLEncoder.encode(ddate, "UTF-8")
        val outputStream = OutputStreamWriter(connection.outputStream)
        outputStream.write(postData)
        outputStream.flush()
        outputStream.close()

        val inputStream = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        var line: String?
        while (inputStream.readLine().also { line = it } != null) {
            response.append(line)
        }
        inputStream.close()

        val jsonResponse = JSONObject(response.toString())
        val status = jsonResponse.getString("status")
        if (status == "success") {
            val dataArray = jsonResponse.getJSONArray("data")
            // 가져온 데이터를 처리
            handleSuccess(dataArray, plantid, ddate)
        } else {
            // 실패 처리
            handleFailure()
        }
    }
    // 데이터 가져오기가 성공한 경우 처리할 로직
    private fun handleSuccess(dataArray: JSONArray, plantid: Int, ddate: String) {
        dList.clear()
        for (i in 0 until dataArray.length()) {
            val dataObject = dataArray.getJSONObject(i)
            val diaryItem = DiaryListItem().apply {
                setItemId(dataObject.getInt("id"))
                setPlantId(dataObject.getInt("plantid"))
                setPlantName(dataObject.getString("pname"))
                setUserEmail(dataObject.getString("uemail"))
                setDiaryDate(dataObject.getString("ddate"))
                setDiaryTitle(dataObject.getString("dtitle"))
                setDiaryContent(dataObject.getString("dcontent"))
                setImageUrl(dataObject.getString("imageurl"))
                setEnrollTime(dataObject.getString("enrolltime"))
            }

            //if (diaryItem.getDiaryDate()==ddate) {
            dList.add(diaryItem)
            //}
            //dList.add(diaryItem)
            // 선택한 날짜와 식물 아이디가 DB에 등록된 날짜와 식물 아이디와 일치하는 경우에만 리스트에 추가

        /*
            if (diaryItem.getDiaryDate() == ddate && diaryItem.getPlantId() == plantid) {
                dList.add(diaryItem)
            }*/
        }

        requireActivity().runOnUiThread {
            recyclerView.adapter = RecyclerViewDiaryAdapter(dList)
            recyclerView.adapter?.notifyDataSetChanged()
        }
        Log.d("MyTag", "Data retrieved successfully!")
    }

    // 데이터 가져오기가 실패한 경우 처리할 로직
    private fun handleFailure() {
        // 실패한 경우
        dList.clear()
        requireActivity().runOnUiThread {
            recyclerView.adapter = RecyclerViewDiaryAdapter(dList)
        }
        //dList.add(DiaryListItem()) // 아무것도 등록 안되어 있을때 표시되는 기본 아이템
        //recyclerView.adapter?.notifyDataSetChanged()
        //Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
    }
    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.timeInMillis
    }
}