package com.rickandmorty.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rickandmorty.R
import com.rickandmorty.activities.dataStore
import com.rickandmorty.adapters.CharacterAdapter
import com.rickandmorty.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.rickandmorty.entities.Character
import com.rickandmorty.services.RickAndMortyAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentFavoriteBinding? = null
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
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch(Dispatchers.IO){
            getFavorites().collect{
                favorites ->
                if(favorites.isNotEmpty()){
                    if(favorites.split(",").size>1){
                        var call= getRetrofit().create(RickAndMortyAPIService:: class.java).
                            getCharactersById("$ENDPOINT$favorites/")
                        var response = fromJson(call.body().toString())

                        withContext(Dispatchers.Main){
                            if (response != null) {
                                initRecyclerView(response)
                            }
                        }
                    }
                    else{
                        var call= getRetrofit().create(RickAndMortyAPIService:: class.java).
                            getCharacterById("$ENDPOINT$favorites/")
                        var response = call.body()

                        withContext(Dispatchers.Main){
                            if (response != null) {
                                initRecyclerView(listOf(response))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView(characters: List<Character>) {
        binding.fragmentFavoriteRecyclerView.layoutManager= GridLayoutManager(requireActivity(), 2)
        binding.fragmentFavoriteRecyclerView.adapter= CharacterAdapter(characters){character ->
            onItemSelected(character) }
    }

    private fun onItemSelected(character: Character){
        var action = FavoriteFragmentDirections.
            actionFavoriteFragmentToCharacterFragment(character.id.toString())
        binding.root.findNavController().navigate(action)
    }

    private fun fromJson(json: String): List<Character>? {
        val typeToken = object : TypeToken<ArrayList<Character>>() {}.type
        return Gson().fromJson(json, typeToken)
    }

    private fun getFavorites()=  requireActivity().dataStore.data.map { preferences ->
        preferences[stringPreferencesKey("favorites")].orEmpty()
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
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}