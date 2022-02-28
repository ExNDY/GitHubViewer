package app.thirtyninth.githubviewer.data.network.interceptors

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/*
class AuthAuthenticator @Inject constructor(private val tokenProvider:TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token =  tokenProvider.token

        if (token != null){
            requestBuilder.addHeader("Authorization", token)
        }


        return chain.proceed(requestBuilder.build())
    }
}*/

class AuthAuthenticator @Inject constructor(private val tokenProvider: TokenProvider) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request {
        val request = response.request.newBuilder()

        val token = tokenProvider.token

        if (token != null) {
            request.header("Authorization", token)
        }

        return request.build()
    }
}

interface TokenProvider {
    val token: String?
}