package app.thirtyninth.githubviewer.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.DefaultMediaDecoder
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.gif.GifMediaDecoder
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.image.svg.SvgMediaDecoder
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.simple.ext.SimpleExtPlugin

@InstallIn(SingletonComponent::class)
@Module
object MarkwonModule {
    @Provides
    fun providesMarkwon(@ApplicationContext context: Context): Markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create {
            it.addMediaDecoder(GifMediaDecoder.create(false))
            it.addMediaDecoder(SvgMediaDecoder.create())
            it.addMediaDecoder(DefaultMediaDecoder.create())
        })
        .usePlugin(MarkwonInlineParserPlugin.create())
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(HtmlPlugin.create())
        .usePlugin(GlideImagesPlugin.create(context))
        .usePlugin(SimpleExtPlugin.create())
        .build()
}