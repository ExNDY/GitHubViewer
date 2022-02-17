package app.thirtyninth.githubviewer.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Authorization","token " + "")
        return chain.proceed(requestBuilder.build())
    }
}