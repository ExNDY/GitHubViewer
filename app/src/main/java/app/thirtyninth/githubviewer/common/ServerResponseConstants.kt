package app.thirtyninth.githubviewer.common

object ServerResponseConstants {
    // FIXME сравнивать по тексту - совсем ненадежный подход. нужно сравнивать по типу исключения,
    //  по коду ответа, но не по тексту
    const val SERVER_NOT_AVAILABLE =
        "Unable to resolve host \"api.github.com\": No address associated with hostname"
}