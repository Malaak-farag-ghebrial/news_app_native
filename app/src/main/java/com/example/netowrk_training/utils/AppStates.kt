package com.example.netowrk_training.utils

sealed class AppStates<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Loading<T>() : AppStates<T>()
    class Success<T>(data: T) : AppStates<T>(data)
    class Error<T>(message: String?= null,data: T? = null) : AppStates<T>(data,message)




}