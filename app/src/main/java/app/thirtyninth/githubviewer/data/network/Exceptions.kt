package app.thirtyninth.githubviewer.data.network

class Exceptions {
    companion object{
        // FIXME вместо текстов надо вводить свои типы исключений, чтобы можно было легко проверять
        //  по классу что за ошибка у нас
        const val SERVER_ERROR: String = "server_error"
        const val AUTHENTICATOR_ERROR:String = "Status: 401 Unauthorized"
    }
}