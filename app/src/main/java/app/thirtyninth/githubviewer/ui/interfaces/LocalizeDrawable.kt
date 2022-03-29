package app.thirtyninth.githubviewer.ui.interfaces

import androidx.annotation.DrawableRes

sealed interface LocalizeDrawable {
    data class Resource(@DrawableRes val resId: Int) : LocalizeDrawable

    fun getDrawableId(): Int {
        if (this is Resource){
            this.resId
        }

        return 0
    }
}