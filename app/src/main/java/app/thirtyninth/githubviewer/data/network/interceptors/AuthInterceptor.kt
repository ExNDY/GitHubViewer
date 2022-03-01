package app.thirtyninth.githubviewer.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = tokenProvider.token

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "token $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}

interface TokenProvider {
    val token: String?
}