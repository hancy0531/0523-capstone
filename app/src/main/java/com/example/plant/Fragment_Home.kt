package com.example.plant

import RecyclerViewPlantAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
import java.util.Locale


class Fragment_Home : Fragment() {
    @SuppressLint("MissingInflatedId")
    private lateinit var recyclerView: RecyclerView
    private lateinit var enrollBtn: Button
    private var userEmail: String? = null
    private var imageUrl: String? = null
    private var pList: ArrayList<PlantListItem> = ArrayList()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__home, container, false)
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE
        arguments?.let {
            userEmail = it.getString("userEmail")
            imageUrl = it.getString("imageUrl")
            //userPassword = it.getString("userPassword")
        }
        GlobalScope.launch(Dispatchers.IO) {
            userEmail?.let { getPlantInfo(it) }
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        enrollBtn = view.findViewById(R.id.button)


       // val userEmail = arguments?.getString("userEmail")
       // val userPassword = arguments?.getString("userPassword")
        // + 버튼 누르면 PlantEnrollFragment프레그먼트로 교체
        enrollBtn.setOnClickListener {
            //intent.putExtra("userEmail", uemail)
            //startActivity(intent)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            // PlantEnrollFragment 인스턴스를 생성
            val enrollFragment1 = PlantEnrollFragment()
            val bundle = Bundle()

            // Bundle에 데이터를 담기
            bundle.putString("userEmail", userEmail)
            // Fragment에 Bundle을 설정
            enrollFragment1.arguments = bundle

            transaction.replace(R.id.container, enrollFragment1)
            transaction.addToBackStack(null) // 이전 Fragment로 돌아갈 수 있도록 back stack에 추가합니다.
            transaction.commit() // 변경 사항을 적용합니다.
        }
        /*
        userEmail?.let {
            Toast.makeText(requireContext(), "User Email: $userEmail", Toast.LENGTH_SHORT).show()
        }*/
        //recyclerView.adapter = RecyclerViewPlantAdapter(Fragment_Garden.getPlantList())
        //recyclerView.adapter = RecyclerViewPlantAdapter(pList)
        recyclerView.adapter = RecyclerViewPlantAdapter(pList)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.addItemDecoration(RecyclerViewDecoration(20))
        return view
    }
    private fun getPlantInfo(uemail: String) {
        val url = URL("http://10.0.2.2/getplantinfo.php")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        val postData = URLEncoder.encode("uemail", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8")
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
            handleSuccess(dataArray)
        } else {
            // 실패 처리
            handleFailure()
        }
    }
    // 데이터 가져오기가 성공한 경우 처리할 로직
    private fun handleSuccess(dataArray: JSONArray) {
        pList.clear()
        //recyclerView.adapter?.notifyDataSetChanged()
        //for (i in 0 until dataArray.length()) {
        //최근 등록한것순으로 3개만 나타나도록함
        val startIndex = if (dataArray.length() <= 3) 0 else dataArray.length() - 3
        for (i in (startIndex until dataArray.length()).reversed()) {
            val dataObject = dataArray.getJSONObject(i)
            val plantItem = PlantListItem().apply {
                setItemId(dataObject.getInt("id"))
                setUserEmail(dataObject.getString("uemail"))
                setPlantName(dataObject.getString("pname"))
                setPlantDate(dataObject.getString("pdate"))
                setPlantPoint(dataObject.getString("ppoint"))
                setPlantLocation(dataObject.getString("plocation"))
                setPlantCycle(dataObject.getString("pcycle"))
                setPlantHour(dataObject.getInt("phour"))
                setPlantMinute(dataObject.getInt("pminute"))
                setPlantTemp(dataObject.getString("ptemp"))
                setPlantHumid(dataObject.getString("phumid"))
                setTempAlarm(dataObject.getInt("ptemp_alarm"))
                setHumidAlarm(dataObject.getInt("phumid_alarm"))
                setImageUrl(dataObject.getString("imageurl"))
                setEnrollTime(dataObject.getString("currenttime"))
            }
            //recyclerView.adapter = RecyclerViewPlantAdapter(pList)


            // 리스트에 추가
            pList.add(plantItem)
           // recyclerView.adapter?.notifyDataSetChanged()

        }
        pList.sortByDescending { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.getEnrollTime()) }
        requireActivity().runOnUiThread {
            recyclerView.adapter = RecyclerViewPlantAdapter(pList)
        }
        Log.d("MyTag", "Data retrieved successfully!")
    }

    // 데이터 가져오기가 실패한 경우 처리할 로직
    private fun handleFailure() {
        // 실패한 경우
        pList.clear()
        val defaultItem = PlantListItem()
        defaultItem.setImageUrl("http://10.0.2.2/uploads/default3.png")
        pList.add(defaultItem) // 아무것도 등록 안되어 있을때 표시되는 기본 아이템
        //recyclerView.adapter?.notifyDataSetChanged()
        //Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
    }
}
