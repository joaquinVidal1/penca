package com.example.penca.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.penca.database.MatchDao
import com.example.penca.network.AuthenticationBody
import com.example.penca.network.NetworkUser
import com.example.penca.network.UserService
import com.example.penca.utils.PreferenceHelper
import com.example.penca.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val matchDao: MatchDao,
    private val preferenceHelper: PreferenceHelper,
    private val userApi: UserService,
) {

    private val _shouldLogOut = SingleLiveEvent<Boolean>()
    val shouldLogOut: LiveData<Boolean> = _shouldLogOut

    val isUserLogged = MutableLiveData(preferenceHelper.getToken() != "null")

    private lateinit var loggedUser: NetworkUser

    fun logOut() {
        CoroutineScope(Dispatchers.IO).launch {
            matchDao.emptyTable()
        }
        preferenceHelper.removeToken()
        _shouldLogOut.postValue(true)
    }

    suspend fun logIn(email: String, password: String) =
        try {
            val networkUser = userApi.logIn(AuthenticationBody(email, password))
            loggedUser = networkUser
            preferenceHelper.insertToken(networkUser.token)
            null
        } catch (e: HttpException) {
            val errorResponse = e.response()!!.errorBody()?.charStream()?.readText()
            Log.e(
                "http exception",
                errorResponse.toString()
            )
            getMessage(errorResponse)
        }

    //TODO habria que parsearlo del json con moshi
    private fun getMessage(errorResponse: String?) =
        errorResponse?.replace("{\"message\":\"", "")?.replace("\"}", "") ?: "error"

    suspend fun register(email: String, password: String) =
        try {
            val networkUser = userApi.register(AuthenticationBody(email, password))
            networkUser.token = "Bearer " + networkUser.token
            loggedUser = networkUser
            matchDao.emptyTable()
            preferenceHelper.insertToken(networkUser.token)
            null
        } catch (e: HttpException) {
            val errorResponse = e.response()!!.errorBody()?.charStream()?.readText()
            Log.e(
                "http exception",
                errorResponse.toString()
            )
            getMessage(errorResponse)
        }
}