package com.example.plant

import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity(){

    private lateinit var Fragment_Board: Fragment_Board
    private lateinit var Fragment_Home: Fragment_Home
    private lateinit var Fragment_Garden: Fragment_Garden
    private lateinit var Fragment_Search: Fragment_Search
    private lateinit var bottomNavigationView:BottomNavigationView
    private lateinit var homeBtn: Button
    private lateinit var profileBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        profileBtn = findViewById(R.id.profileBtn)

        val email = intent.getStringExtra("userEmail")
        val imageUrl = intent.getStringExtra("imageUrl")
        val userName = intent.getStringExtra("userName")

        val bundle = Bundle().apply {
            putString("userEmail", email)
            putString("imageUrl", imageUrl)
            putString("userName", userName)
        }
        Glide.with(this)
            .load(imageUrl)
            .into(profileBtn)
        Fragment_Home = Fragment_Home()
        Fragment_Home.arguments = bundle
        Fragment_Board = Fragment_Board()
        Fragment_Board.arguments = bundle
        Fragment_Garden = Fragment_Garden()
        Fragment_Garden.arguments = bundle
        Fragment_Search = Fragment_Search()
        Fragment_Search.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.container, Fragment_Home).commit();
        //val current_uid = intent.getStringExtra("current_uid")

        homeBtn = findViewById(R.id.home)
        val intent = Intent(this, HomeActivity::class.java)
        homeBtn.setOnClickListener {
            intent.putExtra("userEmail", email)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
        val intent2 = Intent(this, ProfileActivity::class.java)
        profileBtn = findViewById(R.id.profileBtn)
        profileBtn.setOnClickListener {
            intent2.putExtra("userEmail", email)
            intent.putExtra("imageUrl", imageUrl)
            intent2.putExtra("userName", userName)
            startActivity(intent2)
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab1 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, Fragment_Home).commit()
                    true
                }
                R.id.tab2 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, Fragment_Board).commit()
                    true
                }
                R.id.tab3 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, Fragment_Garden).commit()
                    true
                }
                R.id.tab4 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, Fragment_Search).commit()
                    true
                }
                else -> false
            }
        }
    }
}