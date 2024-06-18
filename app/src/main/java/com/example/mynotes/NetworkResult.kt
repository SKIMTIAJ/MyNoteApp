package com.example.mynotes

sealed class NetworkResult<T>(val data:T?=null, val message:String?=null) {
    class Loading<T>():NetworkResult<T>()
    class Error<T>(data: T?=null,message: String?=null):NetworkResult<T>(data,message)
    class Success<T>(data:T?=null):NetworkResult<T>(data)
}