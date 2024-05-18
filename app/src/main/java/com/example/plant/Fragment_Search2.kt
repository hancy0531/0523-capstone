package com.example.plant

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text

class Fragment_Search2 : Fragment(){
    private lateinit var plantNm: TextView
    private lateinit var plntbneNm: TextView
    private lateinit var plntzrNm: TextView
    private lateinit var distbNm: TextView
    private lateinit var fmlCodeNm: TextView
    private lateinit var orgplceInfo: TextView
    private lateinit var adviseInfo: TextView
    private lateinit var grwtveCodeNm: TextView
    private lateinit var managedemanddoCodeNm: TextView
    private lateinit var postngplaceCodeNm: TextView
    private lateinit var managelevelCodeNm: TextView
    private lateinit var soilInfo: TextView
    private lateinit var frtlzrInfo: TextView
    private lateinit var prpgtEraInfo: TextView
    private lateinit var grwhTpCodeNm: TextView
    private lateinit var hdCodeNm: TextView
    private lateinit var lighttdemanddoCodeNm: TextView
    private lateinit var fncltyInfo: TextView
    private lateinit var watercycleSprngCodeNm: TextView
    private lateinit var watercycleSummerCodeNm: TextView
    private lateinit var watercycleAutumnCodeNm: TextView
    private lateinit var watercycleWinterCodeNm: TextView
    private lateinit var backBtn: Button
    private var plantName: String = ""
    private var plntbneName: String = ""
    private var plntzrName: String = ""
    private var distbName: String = ""
    private var fmlCodeName: String = ""
    private var orgplceInformation: String = ""
    private var adviseInformation: String = ""
    private var grwtveCodeName: String = ""
    private var managedemanddoCodeName: String = ""
    private var postngplaceCodeName: String = ""
    private var managelevelCodeName: String = ""
    private var soilInformation: String = ""
    private var frtlzrInformation: String = ""
    private var prpgtEraInformation: String = ""
    private var grwhTpCodeName: String = ""
    private var hdCodeName: String = ""
    private var lighttdemanddoCodeName: String = ""
    private var fncltyInformation: String = ""
    private var watercycleSprngCodeName: String = ""
    private var watercycleSummerCodeName: String = ""
    private var watercycleAutumnCodeName: String = ""
    private var watercycleWinterCodeName: String = ""
    private var userEmail: String = ""
    private var imageUrl: String = ""
    private var userName: String = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__search2, container, false)
        arguments?.let {
            plantName = it.getString("plantNm").toString()
            plntbneName = it.getString("plntbneNm").toString()
            plntzrName = it.getString("plntzrNm").toString()
            fmlCodeName = it.getString("fmlCodeNm").toString()
            distbName = it.getString("distbNm").toString()
            orgplceInformation = it.getString("orgplceInfo").toString()
            adviseInformation = it.getString("adviseInfo").toString()
            grwtveCodeName = it.getString("grwtveCodeNm").toString()
            managedemanddoCodeName = it.getString("managedemanddoCodeNm").toString()
            postngplaceCodeName = it.getString("postngplaceCodeNm").toString()
            managelevelCodeName = it.getString("managelevelCodeNm").toString()
            soilInformation = it.getString("soilInfo").toString()
            frtlzrInformation = it.getString("frtlzrInfo").toString()
            prpgtEraInformation = it.getString("prpgtEraInfo").toString()
            grwhTpCodeName = it.getString("grwhTpCodeNm").toString()
            hdCodeName = it.getString("hdCodeNm").toString()
            lighttdemanddoCodeName = it.getString("lighttdemanddoCodeNm").toString()
            fncltyInformation = it.getString("fncltyInfo").toString()
            watercycleAutumnCodeName = it.getString("watercycleAutumnCodeNm").toString()
            watercycleSummerCodeName = it.getString("watercycleSummerCodeNm").toString()
            watercycleSprngCodeName = it.getString("watercycleSprngCodeNm").toString()
            watercycleWinterCodeName = it.getString("watercycleWinterCodeNm").toString()
            arguments?.let {
                userEmail = it.getString("userEmail").toString()
                imageUrl = it.getString("imageUrl").toString()
                userName = it.getString("userName").toString()
                //userPassword = it.getString("userPassword")
            }
        }
        plantNm = view.findViewById(R.id.plantNm)
        plntbneNm = view.findViewById(R.id.plntbneNm)
        plntzrNm = view.findViewById(R.id.plntzrNm)
        distbNm = view.findViewById(R.id.distbNm)
        fmlCodeNm = view.findViewById(R.id.fmlCodeNm)
        orgplceInfo = view.findViewById(R.id.orgplceInfo)
        adviseInfo = view.findViewById(R.id.adviseInfo)
        grwtveCodeNm = view.findViewById(R.id.grwtveCodeNm)
        managedemanddoCodeNm = view.findViewById(R.id.managedemanddoCodeNm)
        postngplaceCodeNm = view.findViewById(R.id.postngplaceCodeNm)
        managelevelCodeNm = view.findViewById(R.id.managelevelCodeNm)
        soilInfo = view.findViewById(R.id.soilInfo)
        frtlzrInfo = view.findViewById(R.id.frtlzrInfo)
        prpgtEraInfo = view.findViewById(R.id.prpgtEraInfo)
        grwhTpCodeNm = view.findViewById(R.id.grwhTpCodeNm)
        hdCodeNm = view.findViewById(R.id.hdCodeNm)
        lighttdemanddoCodeNm = view.findViewById(R.id.lighttdemanddoCodeNm)
        fncltyInfo = view.findViewById(R.id.fncltyInfo)
        watercycleSprngCodeNm = view.findViewById(R.id.watercycleSprngCodeNm)
        watercycleSummerCodeNm = view.findViewById(R.id.watercycleSummerCodeNm)
        watercycleAutumnCodeNm = view.findViewById(R.id.watercycleAutumnCodeNm)
        watercycleWinterCodeNm = view.findViewById(R.id.watercycleWinterCodeNm)

        backBtn = view.findViewById(R.id.backBtn)



        plantNm.text = plantName
        plntbneNm.text = plntbneName
        plntzrNm.text = plntzrName
        distbNm.text = distbName
        fmlCodeNm.text = fmlCodeName
        orgplceInfo.text = orgplceInformation
        adviseInfo.text = adviseInformation
        grwtveCodeNm.text = grwtveCodeName
        managedemanddoCodeNm.text = managedemanddoCodeName
        postngplaceCodeNm.text = postngplaceCodeName
        managelevelCodeNm.text = managelevelCodeName
        soilInfo.text = soilInformation
        frtlzrInfo.text = frtlzrInformation
        prpgtEraInfo.text = prpgtEraInformation
        grwhTpCodeNm.text = grwhTpCodeName
        hdCodeNm.text = hdCodeName
        lighttdemanddoCodeNm.text = lighttdemanddoCodeName
        fncltyInfo.text = fncltyInformation
        watercycleSprngCodeNm.text = watercycleSprngCodeName
        watercycleSummerCodeNm.text = watercycleSummerCodeName
        watercycleAutumnCodeNm.text = watercycleAutumnCodeName
        watercycleWinterCodeNm.text = watercycleWinterCodeName

        backBtn.setOnClickListener {
            replaceFragment(Fragment_Search())
        }
        return view
    }
    private fun replaceFragment(fragment: Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragment = fragment
        val bundle = Bundle()

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