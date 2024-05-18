package com.example.plant

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

class Fragment_Enroll3 : Fragment(), View.OnClickListener {
    private lateinit var temperaturePicker: NumberPicker
    private lateinit var temperatureTextView: TextView
    private lateinit var humidPicker: NumberPicker
    private lateinit var humidTextView: TextView
    private lateinit var sunday: TextView
    private lateinit var monday: TextView
    private lateinit var tuesday: TextView
    private lateinit var wednesday: TextView
    private lateinit var thursday: TextView
    private lateinit var friday: TextView
    private lateinit var saturday: TextView
    private lateinit var timePicker: TimePicker
    private lateinit var wateringSwitch: Switch
    private lateinit var humidSwitch: Switch
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
    private var selectedDays = ArrayList<String>()
    private var wateringSwitchChecked: Int = 0
    private var humidSwitchChecked: Int = 0
    //private lateinit var bundle: Bundle
    // var plantDataListener: DataListener? = null
    private lateinit var viewModel: PlantEnrollViewModel
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_enroll3, container, false)

        //val intent = Intent(requireContext(), PlantEnrollActivity::class.java)
        temperaturePicker = view.findViewById(R.id.temperaturePicker)
        temperatureTextView = view.findViewById(R.id.temperatureTextView)
        humidPicker = view.findViewById(R.id.humidPicker)
        humidTextView = view.findViewById(R.id.humidTextView)
        sunday = view.findViewById(R.id.sun)
        monday = view.findViewById(R.id.mon)
        tuesday = view.findViewById(R.id.tue)
        wednesday = view.findViewById(R.id.wed)
        thursday = view.findViewById(R.id.thur)
        friday = view.findViewById(R.id.fri)
        saturday = view.findViewById(R.id.sat)
        timePicker = view.findViewById(R.id.timePicker)
        wateringSwitch = view.findViewById(R.id.wateringSwitch)
        humidSwitch = view.findViewById(R.id.humidSwitch)

        viewModel = ViewModelProvider(requireActivity()).get(PlantEnrollViewModel::class.java)
        //plantDataListener = (activity as? DataListener)
        selectedHour = timePicker.currentHour
        selectedMinute = timePicker.currentMinute
        temperatureTextView.text = "15°C ~ 25°C"
        humidTextView.text = "40% ~ 60%"
        sunday.setOnClickListener(this)
        monday.setOnClickListener(this)
        tuesday.setOnClickListener(this)
        wednesday.setOnClickListener(this)
        thursday.setOnClickListener(this)
        friday.setOnClickListener(this)
        saturday.setOnClickListener(this)


        // NumberPicker에 범위 및 초기값 설정
        temperaturePicker.minValue = 10
        temperaturePicker.maxValue = 50
        temperaturePicker.value = 10

        humidPicker.minValue = 30
        humidPicker.maxValue = 80
        humidPicker.value = 40
        wateringSwitch.setOnCheckedChangeListener { _, isChecked ->
            wateringSwitchChecked = if (isChecked) 1 else 0
        }

        // humidSwitch의 상태가 변경될 때 해당 변수를 업데이트합니다.
        humidSwitch.setOnCheckedChangeListener { _, isChecked ->
            humidSwitchChecked = if (isChecked) 1 else 0
        }

        // NumberPicker 값 변경 이벤트 처리
        temperaturePicker.setOnValueChangedListener { picker, oldVal, newVal ->
            // 새로운 온도값을 화면에 업데이트
            temperatureTextView.text =
                newVal.toString() + "°C" + " ~ " + (newVal + 15).toString() + "°C"
        }

        humidPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            humidTextView.text = newVal.toString() + "%" + " ~ " + (newVal + 20).toString() + "%"
        }

        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            selectedHour = hourOfDay
            selectedMinute = minute
        }

        return view
    }

    override fun onClick(v: View?) {
        // Check which TextView was clicked
        when (v?.id) {
            R.id.sun -> handleDayClick(sunday, "Sunday")
            R.id.mon -> handleDayClick(monday, "Monday")
            R.id.tue -> handleDayClick(tuesday, "Tuesday")
            R.id.wed -> handleDayClick(wednesday, "Wednesday")
            R.id.thur -> handleDayClick(thursday, "Thursday")
            R.id.fri -> handleDayClick(friday, "Friday")
            R.id.sat -> handleDayClick(saturday, "Saturday")
        }
    }

    private fun handleDayClick(textView: TextView, day: String) {
        if (selectedDays.contains(day)) {
            textView.setBackgroundColor(Color.TRANSPARENT)
            selectedDays.remove(day)
        } else {
            val gradientDrawable =
                ResourcesCompat.getDrawable(resources, R.drawable.button_diary, null)
            textView.background = gradientDrawable
            //textView.setBackgroundColor(R.drawable.button_diary)
            selectedDays.add(day)
        }
        Log.d("Selected Days", selectedDays.toString())
    }

    override fun onPause() {
        super.onPause()
        val selectedHour = selectedHour
        val selectedMinute = selectedMinute
        val selectedStringDays = selectedDays.joinToString(", ")
        val temperature = temperatureTextView.text.toString()
        val humid = humidTextView.text.toString()
        val wateringSwitchChecked = wateringSwitchChecked
        val humidSwitchChecked = humidSwitchChecked
        viewModel.phour = selectedHour
        viewModel.pminute = selectedMinute
        viewModel.pcycle = selectedStringDays
        viewModel.ptemp = temperature
        viewModel.phumid = humid
        viewModel.pwatering_alarm = wateringSwitchChecked
        viewModel.phumid_alarm = humidSwitchChecked
            /*plantDataListener?.onWateringInfoReceived(
                selectedHour,
                selectedMinute,
                selectedStringDays,
                temperature,
                humid
            )*/
    }
}