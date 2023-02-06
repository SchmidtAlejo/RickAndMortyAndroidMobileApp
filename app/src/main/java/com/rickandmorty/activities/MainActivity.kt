package com.rickandmorty.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.rickandmorty.R
import com.rickandmorty.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.activityMainNavHostFragmentId) as NavHostFragment

        setupToolbar()
        setupDrawerLayout()
    }


    private fun setupDrawerLayout() {

        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.activityMainDrawerNavigatorId.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.activityMainLayoutId)
    }

    private fun setupToolbar(){
        setSupportActionBar(binding.activityLoginToolbar)

        navHostFragment.navController.addOnDestinationChangedListener{_, destination, arguments ->
            if(destination.id == R.id.characterFragment){
                binding.activityLoginToolbar.visibility= View.GONE
            }
            else{
                binding.activityLoginToolbar.visibility= View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navHostFragment.navController, binding.activityMainLayoutId)
    }
}