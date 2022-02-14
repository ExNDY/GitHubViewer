package app.thirtyninth.githubviewer

import android.content.Context
import app.thirtyninth.githubviewer.preferences.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context):Interceptor {
    //private val pref = UserPreferences()

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
/*
        pref.authenticationToken.let {
            requestBuilder.addHeader("Authorization", "token $it")
        }*/

        return chain.proceed(requestBuilder.build())
    }
}