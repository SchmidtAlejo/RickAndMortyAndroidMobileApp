package com.rickandmorty.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.rickandmorty.R
import com.rickandmorty.databinding.ActivityLoginBinding
import com.rickandmorty.databinding.ActivityMainBinding
import com.rickandmorty.entities.UserSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.dataStore by preferencesDataStore(name= "USER_DATA")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userSetup: UserSetup
    private var helper= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goTo()

    }

    override fun onStart() {
        super.onStart()

        binding.activityLoginButtonContinue.setOnClickListener {
            login()
        }
    }

    private fun login(){
        if(
            binding.activityLoginUser.text?.isNotEmpty() == true &&
            binding.activityLoginPassword.text?.isNotEmpty() == true){
            lifecycleScope.launch(Dispatchers.IO){
                saveData(binding.activityLoginUser.text.toString())
            }
        }
        else{
            Toast.makeText(this, getString(R.string.data_is_not_complete),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun goTo() {
        lifecycleScope.launch(Dispatchers.IO){
            getUserSetup().collect{ user->
                userSetup= user
                if(user.isLogged){
                    activateDayMode()
                    goToMainActivity()
                }
                else{
                    activateDayMode()
                }
            }
        }
    }

    private fun activateDayMode() {
        lifecycleScope.launch(Dispatchers.IO){
            if(userSetup.isFirstTime){
                dataStore.edit {
                        preferences ->
                    preferences[booleanPreferencesKey("isFirstTime")]= false
                    preferences[booleanPreferencesKey("isNightMode")]= false
                }
                withContext(Dispatchers.Main){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            } else{
                withContext(Dispatchers.Main){
                    if(userSetup.isNightMode){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }

    private fun goToMainActivity(){
        if(!helper){
            helper=true
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private suspend fun saveData(user: String){
        dataStore.edit {
                preferences->
            preferences[stringPreferencesKey("user")]= user
            preferences[booleanPreferencesKey("isLogged")]= true
        }
    }

    private fun getUserSetup()= dataStore.data.map { preferences ->
        UserSetup(
            isLogged = preferences[booleanPreferencesKey("isLogged")]?:false,
            isNightMode = preferences[booleanPreferencesKey("isNightMode")]?:false,
            isFirstTime = preferences[booleanPreferencesKey("isFirstTime")]?:true
        )
    }

    private fun getIsFirstTime()= dataStore.data.map {
            preferences ->
            preferences[booleanPreferencesKey("isFirstTime")]?:true
    }

    private fun getIsNightMode()= dataStore.data.map {
            preferences ->
            preferences[booleanPreferencesKey("isNightMode")]?:false
    }


    private fun getIsLogged()=  dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey("isLogged")]?:false
    }
}