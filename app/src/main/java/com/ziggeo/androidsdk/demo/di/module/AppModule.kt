package com.ziggeo.androidsdk.demo.di.module

import android.content.Context
import com.ziggeo.androidsdk.IZiggeo
import com.ziggeo.androidsdk.Ziggeo
import com.ziggeo.androidsdk.demo.model.data.storage.KVStorage
import com.ziggeo.androidsdk.demo.model.data.storage.KVStorageImpl
import com.ziggeo.androidsdk.demo.model.data.storage.Prefs
import com.ziggeo.androidsdk.demo.model.system.message.SystemMessageNotifier
import com.ziggeo.androidsdk.net.services.streams.IStreamsServiceRx
import com.ziggeo.androidsdk.net.services.videos.IVideosServiceRx
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module


/**
 * Created by Alexander Bedulin on 25-Sep-19.
 * Ziggeo, Inc.
 * alexb@ziggeo.com
 */
class AppModule(context: Context) : Module() {

    init {
        // Global
        bind(Context::class.java).toInstance(context)
        bind(SystemMessageNotifier::class.java).toInstance(SystemMessageNotifier())
        val prefs = Prefs(context)
        bind(Prefs::class.java).toInstance(prefs)
        bind(KVStorage::class.java).toInstance(KVStorageImpl())
        bind(SystemMessageNotifier::class.java).toInstance(SystemMessageNotifier())

        // Navigation
        val cicerone = Cicerone.create()
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)

        // Ziggeo
        val ziggeo = Ziggeo(context)
        bind(IZiggeo::class.java).toInstance(ziggeo)
        bind(IVideosServiceRx::class.java).toInstance(ziggeo.apiRx().videos())
        bind(IStreamsServiceRx::class.java).toInstance(ziggeo.apiRx().streams())
    }
}