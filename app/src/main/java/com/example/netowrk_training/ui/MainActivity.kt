package com.example.netowrk_training.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.netowrk_training.R
import com.example.netowrk_training.database.ArticleDatabase
import com.example.netowrk_training.repository.NewsRepository
import com.example.netowrk_training.ui.viewmodel.NewsProviderViewModel
import com.example.netowrk_training.ui.viewmodel.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
  //  lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
       // binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsProviderViewModel(application,newsRepository)
        newsViewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)



        val navBarHost = supportFragmentManager.findFragmentById(R.id.news_nav_host_fragment) as NavHostFragment

        val navController = navBarHost.navController

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.nav_bar)


        bottomNavBar.setupWithNavController(navController)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }


    }
}