package com.example.penca.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.penca.database.MatchDao
import com.example.penca.domain.entities.ErrorResponse
import com.example.penca.network.entities.AuthenticationBody
import com.example.penca.network.entities.NetworkUser
import com.example.penca.network.UserService
import com.example.penca.utils.PreferenceHelper
import com.example.penca.utils.SingleLiveEvent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
        // TODO no se si es necesario crear un CoroutineScope, ya existiendo por ejemplo el GlobalScope, aunque hay que usarlo con cuidado.
        // TODO tampoco es nescesario usar Dispatchers.IO para correr una corrutina de Room
        CoroutineScope(Dispatchers.IO).launch {
            matchDao.emptyTable()
        }
        preferenceHelper.removeToken()
        _shouldLogOut.postValue(true)
    }

    suspend fun logIn(email: String, password: String) =
        try {
            // TODO para que sirve el loggedUser?
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

    private fun getMessage(errorResponse: String?): String {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<ErrorResponse> = moshi.adapter(ErrorResponse::class.java)
        return adapter.fromJson(errorResponse)?.message ?: "error"
    }

    suspend fun register(email: String, password: String) =
        try {
            val networkUser = userApi.register(AuthenticationBody(email, password))
            // TODO para que sirve el loggedUser?
            // TODO "Bearer" mejor agregarlo en un header usando un interceptor.
            //  Si usaras el token para otra cosa tendrías que estar manipulando el token para ver si pones o sacas el Bearer
            //  Veo que usas un interceptor, me parece que esto te quedó viejo
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