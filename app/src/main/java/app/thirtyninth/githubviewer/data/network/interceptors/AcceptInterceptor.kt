package app.thirtyninth.githubviewer.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AcceptInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Accept", "application/vnd.github.v3+json")

        return chain.proceed(requestBuilder.build())
    }
}