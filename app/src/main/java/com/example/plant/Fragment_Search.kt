package com.example.plant

import PlantItem
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class Fragment_Search : Fragment() {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private var userEmail: String = ""
    private var imageUrl: String = ""
    private var userName: String = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__search, container, false)
        arguments?.let {
            userEmail = it.getString("userEmail").toString()
            imageUrl = it.getString("imageUrl").toString()
            userName = it.getString("userName").toString()
            //userPassword = it.getString("userPassword")
        }
        listView = view.findViewById(R.id.searchList)
        searchView = view.findViewById(R.id.searchView)
        listView.adapter = SearchAdapter(loadPlantItemsFromJson(requireContext()))
        initSearchView()

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = listView.adapter.getItem(position) as PlantItem
            replaceFragment(Fragment_Search2(), selectedItem)
        }
        return view
    }
    //216개 항목
    private fun loadPlantItemsFromJson(context: Context): List<PlantItem> {
        val inputStream = context.assets.open("plant_items.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("item")

        val plantItems = mutableListOf<PlantItem>()
        for (i in 0 until jsonArray.length()) {
            val plantObject = jsonArray.getJSONObject(i)
            val plantItem = PlantItem(
                plantObject.optString("plantNm", ""),
                plantObject.optString("adviseInfo", ""),
                plantObject.optString("clCodeNm", ""),
                plantObject.optString("cntntsNo", ""),
                plantObject.optString("distbNm", ""),
                plantObject.optString("dlthtsCodeNm", ""),
                plantObject.optString("dlthtsManageInfo", ""),
                plantObject.optString("eclgyCodeNm", ""),
                plantObject.optString("etcEraInfo", ""),
                plantObject.optString("flclrCodeNm", ""),
                plantObject.optString("flpodmtBigInfo", ""),
                plantObject.optString("flpodmtMddlInfo", ""),
                plantObject.optString("flpodmtSmallInfo", ""),
                plantObject.optString("fmlCodeNm", ""),
                plantObject.optString("fmlNm", ""),
                plantObject.optString("fmldeSeasonCodeNm", ""),
                plantObject.optString("fmldecolrCodeNm", ""),
                plantObject.optString("fncltyInfo", ""),
                plantObject.optString("frtlzrInfo", ""),
                plantObject.optString("growthAraInfo", ""),
                plantObject.optString("growthHgInfo", ""),
                plantObject.optString("grwhTpCode", ""),
                plantObject.optString("grwhTpCodeNm", ""),
                plantObject.optString("grwhstleCodeNm", ""),
                plantObject.optString("grwtveCode", ""),
                plantObject.optString("grwtveCodeNm", ""),
                plantObject.optString("hdCode", ""),
                plantObject.optString("hdCodeNm", ""),
                plantObject.optString("hgBigInfo", ""),
                plantObject.optString("hgMddlInfo", ""),
                plantObject.optString("hgSmallInfo", ""),
                plantObject.optString("ignSeasonCodeNm", ""),
                plantObject.optString("imageEvlLinkCours", ""),
                plantObject.optString("indoorpsncpacompositionCodeNm", ""),
                plantObject.optString("lefStleInfo", ""),
                plantObject.optString("lefcolrCodeNm", ""),
                plantObject.optString("lefmrkCodeNm", ""),
                plantObject.optString("lighttdemanddoCodeNm", ""),
                plantObject.optString("managedemanddoCode", ""),
                plantObject.optString("managedemanddoCodeNm", ""),
                plantObject.optString("managelevelCode", ""),
                plantObject.optString("managelevelCodeNm", ""),
                plantObject.optString("orgplceInfo", ""),
                plantObject.optString("pcBigInfo", ""),
                plantObject.optString("pcMddlInfo", ""),
                plantObject.optString("pcSmallInfo", ""),
                plantObject.optString("plntbneNm", ""),
                plantObject.optString("plntzrNm", ""),
                plantObject.optString("postngplaceCodeNm", ""),
                plantObject.optString("prpgtEraInfo", ""),
                plantObject.optString("prpgtmthCodeNm", ""),
                plantObject.optString("smellCode", ""),
                plantObject.optString("smellCodeNm", ""),
                plantObject.optString("soilInfo", ""),
                plantObject.optString("speclmanageInfo", ""),
                plantObject.optString("toxctyInfo", ""),
                plantObject.optString("volmeBigInfo", ""),
                plantObject.optString("volmeMddlInfo", ""),
                plantObject.optString("volmeSmallInfo", ""),
                plantObject.optString("vrticlBigInfo", ""),
                plantObject.optString("vrticlMddlInfo", ""),
                plantObject.optString("vrticlSmallInfo", ""),
                plantObject.optString("watercycleAutumnCode", ""),
                plantObject.optString("watercycleAutumnCodeNm", ""),
                plantObject.optString("watercycleSprngCode", ""),
                plantObject.optString("watercycleSprngCodeNm", ""),
                plantObject.optString("watercycleSummerCode", ""),
                plantObject.optString("watercycleSummerCodeNm", ""),
                plantObject.optString("watercycleWinterCode", ""),
                plantObject.optString("watercycleWinterCodeNm", ""),
                plantObject.optString("widthBigInfo", ""),
                plantObject.optString("widthMddlInfo", ""),
                plantObject.optString("widthSmallInfo", ""),
                plantObject.optString("winterLwetTpCode", ""),
                plantObject.optString("winterLwetTpCodeNm", "")
            )
            plantItems.add(plantItem)
        }
        return plantItems
    }
    private fun initSearchView() {
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (listView.adapter as SearchAdapter).filter.filter(newText)
                return true
            }
        })
    }
    private fun replaceFragment(fragment: Fragment, selectedItem: PlantItem){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragment = fragment
        val bundle = Bundle()
        bundle.putString("plantNm", selectedItem.plantNm)
        bundle.putString("plntbneNm", selectedItem.plntbneNm)
        bundle.putString("plntzrNm", selectedItem.plntzrNm)
        bundle.putString("distbNm", selectedItem.distbNm)
        bundle.putString("fmlCodeNm", selectedItem.fmlCodeNm)
        bundle.putString("orgplceInfo", selectedItem.orgplceInfo)
        bundle.putString("adviseInfo", selectedItem.adviseInfo)
        bundle.putString("grwtveCodeNm", selectedItem.grwtveCodeNm)
        bundle.putString("managedemanddoCodeNm", selectedItem.managedemanddoCodeNm)
        bundle.putString("postngplaceCodeNm", selectedItem.postngplaceCodeNm)
        bundle.putString("managelevelCodeNm", selectedItem.managelevelCodeNm)
        bundle.putString("soilInfo", selectedItem.soilInfo)
        bundle.putString("frtlzrInfo", selectedItem.frtlzrInfo)
        bundle.putString("prpgtEraInfo", selectedItem.prpgtEraInfo)
        bundle.putString("grwhTpCodeNm", selectedItem.grwhTpCodeNm)
        bundle.putString("hdCodeNm", selectedItem.hdCodeNm)
        bundle.putString("lighttdemanddoCodeNm", selectedItem.lighttdemanddoCodeNm)
        bundle.putString("fncltyInfo", selectedItem.fncltyInfo)
        bundle.putString("watercycleSprngCodeNm",selectedItem.watercycleSprngCodeNm)
        bundle.putString("watercycleSummerCodeNm",selectedItem.watercycleSummerCodeNm)
        bundle.putString("watercycleAutumnCodeNm",selectedItem.watercycleAutumnCodeNm)
        bundle.putString("watercycleWinterCodeNm",selectedItem.watercycleWinterCodeNm)

        bundle.putString("userEmail", userEmail)
        bundle.putString("imageUrl", imageUrl)
        bundle.putString("userName", userName)

        // Fragment에 Bundle을 설정
        fragment.arguments = bundle

        // FragmentTransaction을 사용하여 PlantEnrollFragment로 전환
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // 이전 Fragment로 돌아갈 수 있도록 back stack에 추가
        transaction.commit() // 변경 사항을 적용
    }
}

