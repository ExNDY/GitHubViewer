package app.thirtyninth.githubviewer.extentions

import android.content.Context
import coil.ImageLoader
import coil.memory.MemoryCache
import io.noties.markwon.image.coil.CoilImagesPlugin

fun getCoilPlugin(context: Context): CoilImagesPlugin {
    val imageLoader = ImageLoader.Builder(context)
        .apply {
            memoryCache { MemoryCache.Builder(context).maxSizePercent(0.5).build() }
            crossfade(true)
        }.build()

    return CoilImagesPlugin.create(context, imageLoader)
}