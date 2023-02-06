package com.rickandmorty.responses

import com.rickandmorty.entities.Character

data class GetCharactersByPage (
    val results: List<Character>
)