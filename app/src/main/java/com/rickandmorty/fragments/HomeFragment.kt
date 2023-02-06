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
import com.rickandmorty.R
import com.rickandmorty.activities.dataStore
import com.rickandmorty.adapters.CharacterAdapter
import com.rickandmorty.databinding.FragmentHomeBinding
import com.rickandmorty.entities.Character
import com.rickandmorty.responses.GetCharactersByPage
import com.rickandmorty.services.RickAndMortyAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var page= 1

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.fragmentHomePage.text= (page).toString()

        lifecycleScope.launch(Dispatchers.IO){

            updateCall(page)

            getUser().collect{
                withContext(Dispatchers.Main){
                    binding.fragmentHomeNameUser.text= "Hello, $it"
                    binding.fragmentHomePage.visibility= View.VISIBLE
                    if(page!= MAX_PAGE){
                        binding.fragmentHomeNextPage.visibility= View.VISIBLE
                    }
                    if(page!= MIN_PAGE){
                        binding.fragmentHomePreviosPage.visibility= View.VISIBLE
                    }
                }
            }
        }

        binding.fragmentHomeNextPage.setOnClickListener {
            page+=1
            binding.fragmentHomePage.text= page.toString()
            if(page== MAX_PAGE){
                binding.fragmentHomeNextPage.visibility= View.GONE
            }
            binding.fragmentHomePreviosPage.visibility= View.VISIBLE
            binding.fragmentHomeRecyclerView.visibility= View.GONE
            binding.fragmentHomeProgressBar.visibility= View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO){
                updateCall(page)
            }
        }

        binding.fragmentHomePreviosPage.setOnClickListener {
            page-=1
            binding.fragmentHomePage.text= page.toString()
            if(page== MIN_PAGE){
                binding.fragmentHomePreviosPage.visibility= View.GONE
            }
            binding.fragmentHomeNextPage.visibility= View.VISIBLE
            binding.fragmentHomeRecyclerView.visibility= View.GONE
            binding.fragmentHomeProgressBar.visibility= View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO){
                updateCall(page)
            }
        }
    }

    private suspend fun updateCall(_page: Int){
        var call= getRetrofit().create(RickAndMortyAPIService:: class.java).getCharactersByPage("$ENDPOINT$_page")
        var response: GetCharactersByPage? = call.body()
        if (call.isSuccessful){
            if(response!=null && response.results.isNotEmpty()){
                withContext(Dispatchers.Main){
                    binding.fragmentHomeRecyclerView.visibility= View.VISIBLE
                    initRecyclerView(response.results)
                    binding.fragmentHomeProgressBar.visibility= View.GONE
                }
            }
        }
    }

    private fun initRecyclerView(characters: List<Character>) {
        binding.fragmentHomeRecyclerView.layoutManager= GridLayoutManager(requireActivity(), 2)
        binding.fragmentHomeRecyclerView.adapter= CharacterAdapter(characters){character ->
            onItemSelected(character) }
    }

    private fun onItemSelected(character: Character){
        var action = HomeFragmentDirections.actionHomeFragmentToCharacterFragment(character.id.toString())
        binding.root.findNavController().navigate(action)
    }

    private fun getUser()=  requireActivity().dataStore.data.map { preferences ->
        preferences[stringPreferencesKey("user")].orEmpty()
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
        const val ENDPOINT= "character/?page="
        const val MAX_PAGE=42
        const val MIN_PAGE=1
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}