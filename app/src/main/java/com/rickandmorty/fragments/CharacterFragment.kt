package com.rickandmorty.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.rickandmorty.R
import com.rickandmorty.activities.dataStore
import com.rickandmorty.databinding.FragmentCharacterBinding
import com.rickandmorty.services.RickAndMortyAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.rickandmorty.entities.Character
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CharacterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CharacterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentCharacterBinding? = null
    private val binding get() = _binding!!
    lateinit var characterId: String
    lateinit var favoritesString: String

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
        _binding = FragmentCharacterBinding.inflate(inflater, container, false)

        characterId= CharacterFragmentArgs.fromBundle(requireArguments()).characterId

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        val toolbar= binding.fragmentCharacterToolbar
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()

        binding.fragmentCharacterAddFavorites.setOnClickListener {
            addToFavorites()
        }

        binding.fragmentCharacterRemoveFavorites.setOnClickListener {
            removeToFavorites()
        }

        lifecycleScope.launch(Dispatchers.IO){
            getFavorites().collect{
                favorites ->

                favoritesString= favorites

                val list= favorites.split(",")


                var call= getRetrofit().create(RickAndMortyAPIService:: class.java).getCharacterById(
                    "$ENDPOINT/$characterId/"
                )
                var response: Character? = call.body()
                if (call.isSuccessful && response!=null){
                    withContext(Dispatchers.Main){
                        if(list.contains(characterId)){
                            binding.fragmentCharacterRemoveFavorites.visibility= View.VISIBLE
                        }
                        else{
                            binding.fragmentCharacterAddFavorites.visibility= View.VISIBLE
                        }
                        binding.fragmentCharacterToolbar.title=response.name
                        binding.fragmentCharacterStatus.text= response.status
                        binding.fragmentCharacterGender.text= response.gender
                        binding.fragmentCharacterOrigin.text= response.origin.name
                        binding.fragmentCharacterLocation.text= response.location.name
                        binding.fragmentCharacterSpecie.text= response.species
                        Glide
                            .with(binding.root)
                            .load(response.image)
                            .centerCrop()
                            .into(binding.fragmentCharacterImage)
                    }
                }
            }
        }
    }

    private fun removeToFavorites() {
        binding.fragmentCharacterRemoveFavorites.visibility= View.GONE
        binding.fragmentCharacterAddFavorites.visibility= View.VISIBLE
        val favorites= favoritesString.split(",")
        var newFavorites=""
        for (item in favorites){
            if(item!=characterId){
                if(newFavorites.isNotEmpty()){
                    newFavorites+=",$item"
                }
                else{
                    newFavorites=item
                }
            }
        }
        lifecycleScope.launch(Dispatchers.IO){
            saveFavorites(newFavorites)
        }
    }

    private fun addToFavorites() {
        binding.fragmentCharacterAddFavorites.visibility= View.GONE
        binding.fragmentCharacterRemoveFavorites.visibility= View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO){
            if(favoritesString.isNotEmpty()){
                saveFavorites("$favoritesString,$characterId")
            }
            else{
                saveFavorites("$characterId")
            }
        }
    }

    private fun getFavorites()=  requireActivity().dataStore.data.map { preferences ->
        preferences[stringPreferencesKey("favorites")].orEmpty()
    }

    private suspend fun saveFavorites(favorites: String){
        requireActivity().dataStore.edit {
                preferences->
            preferences[stringPreferencesKey("favorites")]= favorites
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getString(R.string.rick_and_morty_api_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
    }

    private fun getClient(): OkHttpClient {

        return OkHttpClient.Builder()
            .build()
    }

    companion object {
        const val ENDPOINT="character/"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CharacterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CharacterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}