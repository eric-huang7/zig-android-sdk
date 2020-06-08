package com.ziggeo.androidsdk.demo.model.interactor

import com.ziggeo.androidsdk.demo.model.system.message.SystemMessage
import com.ziggeo.androidsdk.demo.model.system.message.SystemMessageNotifier
import com.ziggeo.androidsdk.demo.model.system.message.SystemMessageType
import com.ziggeo.androidsdk.net.models.videos.VideoModel
import com.ziggeo.androidsdk.net.services.videos.IVideosServiceRx
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject


/**
 * Created by Alexander Bedulin on 03-Oct-19.
 * Ziggeo, Inc.
 * alexb@ziggeo.com
 */
class RecordingsInteractor @Inject constructor(
    private val videoService: IVideosServiceRx
) {

    fun getRecordingsList(): Single<List<VideoModel>> {
        return videoService.index(null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun destroy(videoToken: String): Completable {
        return videoService.destroy(videoToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getInfo(videoToken: String): Single<VideoModel> {
        return videoService[videoToken]
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateInfo(token: String, args: HashMap<String, String>): Single<VideoModel> {
        return videoService.update(token, args)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateInfo(videoModel: VideoModel): Single<VideoModel> {
        return videoService.update(videoModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun downloadImage(token: String): Single<InputStream> {
        return videoService.downloadImage(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getImageUrl(token: String): String {
        return videoService.getImageUrl(token)
    }
}