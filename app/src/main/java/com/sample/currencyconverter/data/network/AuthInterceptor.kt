package com.sample.currencyconverter.data.network

import com.sample.currencyconverter.config.Config
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class AuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // To add generic query params
        val httpUrl = original.url.newBuilder()
            .addQueryParameter("app_id", Config.APP_ID)
            .build()
        val requestBuilder: Request.Builder?
        requestBuilder = original.newBuilder()
            .url(httpUrl)

        val response = chain.proceed(requestBuilder.build())
        return response
    }
}