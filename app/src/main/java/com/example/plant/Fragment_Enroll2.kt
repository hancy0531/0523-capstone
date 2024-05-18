package com.example.plant

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Fragment_Enroll2 : Fragment() {
    private lateinit var calender: CalendarView
    private lateinit var date: TextView
    private lateinit var point: EditText
    private lateinit var location: EditText
    private var choiceDate: String = ""
    //var plantDataListener: DataListener? = null
    private lateinit var viewModel: PlantEnrollViewModel
    //private lateinit var bundle: Bundle
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_enroll2, container, false)

        calender = view.findViewById(R.id.calendarView2)
        date = view.findViewById(R.id.detailDate)
        point = view.findViewById(R.id.plantDtl)
        location = view.findViewById(R.id.plantLocation)
        viewModel = ViewModelProvider(requireActivity()).get(PlantEnrollViewModel::class.java)
        //plantDataListener = (activity as? DataListener)
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy / MM / dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        date.text = formattedDate
        calender.setOnDateChangeListener { view, year, month, dayOfMonth ->
            date.visibility = View.VISIBLE
            choiceDate = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            date.text = choiceDate

        }
        return view

    }

    override fun onPause() {
        super.onPause()
        val date = date.text.toString()
        val point = point.text.toString()
        val location = location.text.toString()
        viewModel.pdate = date
        viewModel.ppoint = point
        viewModel.plocation = location
        //plantDataListener?.onPlantInfoReceived(date, point, location)
    }

}

