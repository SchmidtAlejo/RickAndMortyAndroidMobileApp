package com.rickandmorty.services

import com.google.gson.JsonArray
import com.rickandmorty.responses.GetCharactersByPage
import com.rickandmorty.entities.Character
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RickAndMortyAPIService {

    @GET
    suspend fun getCharactersByPage(@Url url: String): Response<GetCharactersByPage>

    @GET
    suspend fun getCharacterById(@Url url: String): Response<Character>

    @GET
    suspend fun getCharactersById(@Url url: String): Response<JsonArray>
}