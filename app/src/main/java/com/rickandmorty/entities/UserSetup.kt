package com.rickandmorty.entities

data class UserSetup(
    val isFirstTime: Boolean,
    val isLogged: Boolean,
    val isNightMode: Boolean
)