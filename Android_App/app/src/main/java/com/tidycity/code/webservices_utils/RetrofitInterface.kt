package com.tidycity.code.webservices_utils

import android.text.TextUtils
import com.tidycity.code.dataclasses_prototypes.Prototypes
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
const val READ_TIMEOUT = "READ_TIMEOUT"
const val WRITE_TIMEOUT = "WRITE_TIMEOUT"

interface RetrofitInterface {

    @Multipart
    @POST("files/")
    fun uploadVideoServer(
        @Header ("Authorization") token: String,
        @Part("username") username: String,
        @Part("videoId") identifier: String,
        @Part videoMetadata: MultipartBody.Part?,
        @Part Frames: List<MultipartBody.Part>,
    ): Call<Prototypes.RfFileResponse>

    @GET("request_token/")
    fun getToken(
        @Query("username") username: String
    ):Call<Prototypes.RfTokenResponse>

    @POST("token_test/")
    fun validateToken(
        @Header ("Authorization") token: String,
    ):Call<Prototypes.RfTokenValidation>

    @POST("auth/register")
    fun createAccount(
        @Body request: Prototypes.CreateAccountParams
    ):Call<Prototypes.RfCreateAccountResponse>

    @POST("auth/authenticate")
    fun signIn(
        @Body request: Prototypes.SignInParams
    ): Call<Prototypes.RfSignResponse>

    companion object {

        private val interceptor = Interceptor { chain ->
            val request = chain.request()

            var connectTimeout = chain.connectTimeoutMillis()
            var readTimeout = chain.readTimeoutMillis()
            var writeTimeout = chain.writeTimeoutMillis()

            val connect = request.header(CONNECT_TIMEOUT)
            val read = request.header(READ_TIMEOUT)
            val write = request.header(WRITE_TIMEOUT)

            if (!TextUtils.isEmpty(connect)) {
                connectTimeout = connect!!.toInt()
            }

            if (!TextUtils.isEmpty(read)) {
                readTimeout = read!!.toInt()
            }

            if (!TextUtils.isEmpty(write)) {
                writeTimeout = write!!.toInt()
            }

            chain.withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(request)
        }

        private var okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .build()

        private var BASE_URL = "https://0ff4-85-139-212-19.ngrok-free.app/api/web-services/"

        operator fun invoke(): RetrofitInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(RetrofitInterface::class.java)
        }
    }
}
