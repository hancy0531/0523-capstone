package com.example.plant

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator3
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

class Fragment_Garden : Fragment() {
    private lateinit var viewPagerTip:ViewPager2
    private lateinit var gridView:GridView
    private lateinit var indicator3: CircleIndicator3
    private lateinit var userNameTextView: TextView
    private lateinit var option: Button
    private var userEmail: String? = null
    private var userName: String? = null
    private var pList: ArrayList<PlantListItem> = ArrayList()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__garden, container, false)
        arguments?.let {
            userEmail = it.getString("userEmail")
            userName = it.getString("userName")
        }
        GlobalScope.launch(Dispatchers.IO) {
            userEmail?.let { getPlantinfo(it) }
        }
        viewPagerTip = view.findViewById(R.id.viewPagerTip)
        gridView = view.findViewById(R.id.gridView)
        indicator3 = view.findViewById(R.id.tip_indicator)
        userNameTextView = view.findViewById(R.id.userNameText)
        option = view.findViewById(R.id.options)

        option.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu1 -> {
                        pList.sortByDescending { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.getEnrollTime()) }
                        requireActivity().runOnUiThread {
                            gridView.adapter = GridPlantListAdapter(pList)
                        }
                        true
                    }
                    R.id.menu2 -> {
                        pList.sortBy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.getEnrollTime()) }
                        requireActivity().runOnUiThread {
                            gridView.adapter = GridPlantListAdapter(pList)
                        }
                        true
                    }
                    else -> {
                        pList.sortByDescending { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.getEnrollTime()) }
                        requireActivity().runOnUiThread {
                            gridView.adapter = GridPlantListAdapter(pList)
                        }
                        true
                    }
                }
            }
            popupMenu.show()
        }
        //다이얼로그
        /*option.setOnClickListener{
            val items = arrayOf("최신순", "등록순")
            var selectedItem: String? = "최신순"
            val builder = AlertDialog.Builder(requireContext()).apply {
                setTitle("정렬 방식 선택")

                setSingleChoiceItems(items, -1) { dialog, which ->
                    selectedItem = items[which]

                }
                setPositiveButton("OK" ,DialogInterface.OnClickListener{ dialog, which ->
                    when(selectedItem){
                        "최신순" ->pList.sortByDescending { it.getItemId() }

                        "등록순" ->pList.sortBy { it.getItemId() }

                    }
                    requireActivity().runOnUiThread {
                        gridView.adapter = GridPlantListAdapter(pList)
                        // GridView 업데이트 후 추가적인 작업 수행 가능
                    }
                    //Toast.makeText(this.context,selectedItem,Toast.LENGTH_SHORT).show()
                })
                create()
                show()
            }
        }*/

        userNameTextView.text = userName
        //gridView.adapter = GridPlantListAdapter(pList)
        gridView.setOnItemClickListener { parent, view, position, id ->
            val item: PlantListItem = (parent.adapter as GridPlantListAdapter).getItem(position) as PlantListItem
            Toast.makeText(requireContext(), "Selected item ID: ${item.getItemId()}", Toast.LENGTH_SHORT).show()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            val fragment = Fragment_Diary1()
            val bundle = Bundle()
            var plantName = item.getPlantName()
            var plantId = item.getItemId()
            bundle.putString("userEmail", userEmail)
            bundle.putString("plantName", plantName)
            bundle.putInt("plantId", plantId)

            // Fragment에 Bundle을 설정
            fragment.arguments = bundle

            // FragmentTransaction을 사용하여 PlantEnrollFragment로 전환
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null) // 이전 Fragment로 돌아갈 수 있도록 back stack에 추가
            transaction.commit() // 변경 사항을 적용
            //intent.putExtra("userEmail", userEmail)
            //startActivity(intent)
            // Toast.makeText(context, item.getNum().toString(), Toast.LENGTH_SHORT).show()
        }


        viewPagerTip.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewPagerTip.currentItem = position
            }
        })

        viewPagerTip.adapter = ViewPagerAdapter(getTipList())
        indicator3.setViewPager(viewPagerTip)

        return view
    }

    private fun getPlantinfo(uemail: String) {
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
        for (i in 0 until dataArray.length()) {
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
            pList.add(plantItem)
            // 리스트에 추가됨
            //pList.add(0, plantItem)
           // pList = plantList
        }
        pList.sortByDescending { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.getEnrollTime()) }
        requireActivity().runOnUiThread {
            gridView.adapter = GridPlantListAdapter(pList)
            // GridView 업데이트 후 추가적인 작업 수행 가능
        }
        Log.d("MyTag", "Data retrieved successfully!")
    }

    private fun handleFailure() {
        // 실패한 경우
        pList.clear()
        val defaultItem = PlantListItem()
        defaultItem.setImageUrl("http://10.0.2.2/uploads/default3.png")
        pList.add(defaultItem)
        //Toast.makeText(context, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
    }

    private fun getTipList(): ArrayList<String> {
        val fullTipList = arrayListOf(
            "채광이 잘드는 장소를 찾은 다음에 무엇을 키울지 결정하는 것이 좋습니다.",
            "지나치게 물을 많이 주지 말고 흙이 건조할 때 물을 주는 것이 좋습니다.",
            "너무 강한 직사광선은 식물에게 해롭습니다.",
            "식물의 잘 자라는 온도는 18~23도로 사람과 같습니다. 열대식물과 같이 습한 환경에서 잘 자라는 식물은 분무기로 2~3일에 한 번씩 물을 뿌려줍시다."
        )
        val selectedTips = arrayListOf<String>()
        while (selectedTips.size < 3) {
            val randomIndex = (0 until fullTipList.size).random()
            val tip = fullTipList[randomIndex]
            if (!selectedTips.contains(tip)) {
                selectedTips.add(tip)
            }
        }
        return selectedTips
    }
    /*
    companion object {
        fun getPlantList(): ArrayList<PlantListItem> {
            val plantList = ArrayList<PlantListItem>()

            val plant1 = PlantListItem()
            plant1.setName("스킨답서스")
            plant1.setImgSrc(R.drawable.img_7) // Assuming you have a rose_image in your resources
            plant1.setNum(1111111)
            plantList.add(plant1)

            val plant2 = PlantListItem()
            plant2.setName("테이블야자")
            plant2.setImgSrc(R.drawable.img_8) // Assuming you have a sunflower_image in your resources
            plant2.setNum(2222222)
            plantList.add(plant2)

            val plant3 = PlantListItem()
            plant3.setName("허브")
            plant3.setImgSrc(R.drawable.img_6) // Assuming you have a rose_image in your resources
            plant3.setNum(3333333)
            plantList.add(plant3)

            val plant4 = PlantListItem()
            plant4.setName("아이비")
            plant4.setImgSrc(R.drawable.img_5) // Assuming you have a rose_image in your resources
            plant4.setNum(4444444)
            plantList.add(plant4)

            return plantList
        }

    }*/


}