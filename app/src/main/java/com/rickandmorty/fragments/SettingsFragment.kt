package com.rickandmorty.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.rickandmorty.activities.dataStore
import com.rickandmorty.databinding.FragmentSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            getIsNightMode().collect{
                binding.fragmentSettingsNighyMode.isChecked = it
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.fragmentSettingsNighyMode.setOnClickListener {
            changeDayNight()
        }

        binding.fragmentSettingsEmptyFavoriteList.setOnClickListener {
            emptyFavoriteList()
        }
    }

    private fun changeDayNight() {
        lifecycleScope.launch(Dispatchers.IO){
            getIsNightMode().collect{
                saveIsNightMode(!it)
            }
        }
    }

    private fun emptyFavoriteList() {
        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Empty favorite list")
        builder.setMessage("Are you sure that you want to empty your favorite list?")
        builder.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
            lifecycleScope.launch(Dispatchers.IO){
                requireActivity().dataStore.edit {
                        preferences->
                    preferences[stringPreferencesKey("favorites")]= ""
                }
            }
        }
        builder.setNegativeButton("No"){dialogInterface: DialogInterface, i: Int ->}
        builder.show()
    }

    private fun getIsNightMode()= requireActivity().dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey("isNightMode")]?:false
    }

    private fun saveIsNightMode(isNightMode: Boolean){
        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                requireActivity().dataStore.edit {
                        preferences->
                    preferences[booleanPreferencesKey("isNightMode")]=isNightMode
                    updateMode()
                }
            }
        }
    }

    private fun updateMode() {
        if (binding.fragmentSettingsNighyMode.isChecked){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}