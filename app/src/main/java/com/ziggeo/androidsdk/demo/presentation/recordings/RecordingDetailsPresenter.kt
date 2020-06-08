package com.ziggeo.androidsdk.demo.presentation.recordings

import com.arellomobile.mvp.InjectViewState
import com.ziggeo.androidsdk.IZiggeo
import com.ziggeo.androidsdk.demo.Screens
import com.ziggeo.androidsdk.demo.model.data.storage.KVStorage
import com.ziggeo.androidsdk.demo.model.data.storage.VIDEO_TOKEN
import com.ziggeo.androidsdk.demo.model.interactor.RecordingsInteractor
import com.ziggeo.androidsdk.demo.model.system.flow.FlowRouter
import com.ziggeo.androidsdk.demo.model.system.message.SystemMessageNotifier
import com.ziggeo.androidsdk.demo.presentation.global.BasePresenter
import com.ziggeo.androidsdk.net.models.videos.VideoModel
import io.reactivex.disposables.Disposable
import javax.inject.Inject


/**
 * Created by Alexander Bedulin on 25-Sep-19.
 * Ziggeo, Inc.
 * alexb@ziggeo.com
 */
@InjectViewState
class RecordingDetailsPresenter @Inject constructor(
    private var recordingsInteractor: RecordingsInteractor,
    private var router: FlowRouter,
    private var kvStorage: KVStorage,
    private var ziggeo: IZiggeo,
    smn: SystemMessageNotifier
) : BasePresenter<RecordingDetailsView>(smn) {

    private lateinit var model: VideoModel
    private lateinit var videoToken: String
    private var disposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        videoToken = kvStorage.get(VIDEO_TOKEN) as String
        viewState.showPreview(recordingsInteractor.getImageUrl(videoToken))
        disposable = recordingsInteractor.getInfo(videoToken)
            .doOnSubscribe {
                viewState.showProgressDialog(true)
            }.doFinally {
                viewState.showProgressDialog(false)
            }.subscribe { model, throwable ->
                model?.let {
                    this.model = model
                    viewState.showRecordingData(model)
                }
                throwable?.let {
                    commonOnError(it)
                }
            }
        viewState.showViewsInViewState()
    }

    fun onPlayClicked() {
        ziggeo.startPlayer(videoToken)
    }

    fun onConfirmNoClicked() {
        viewState.hideConfirmDeleteDialog()
    }

    fun onConfirmYesClicked() {
        viewState.hideConfirmDeleteDialog()
        disposable = recordingsInteractor.destroy(videoToken)
            .doOnError { commonOnError(it) }
            .doOnSubscribe {
                viewState.showProgressDialog(true)
            }.doFinally {
                viewState.showProgressDialog(false)
            }.subscribe {
                router.finishFlow()
            }
    }

    fun onDeleteClicked() {
        viewState.showConfirmDeleteDialog()
    }

    fun onSaveClicked(tokenOrKey: String, title: String, description: String) {
        if (model.token != tokenOrKey) {
            model.key = tokenOrKey
        }
        model.title = title
        model.description = description

        disposable = recordingsInteractor.updateInfo(model)
            .doOnError { commonOnError(it) }
            .doOnSubscribe {
                viewState.showProgressDialog(true)
            }.doFinally {
                viewState.showProgressDialog(false)
            }.subscribe { model ->
                this.model = model
                viewState.showViewsInViewState()
            }
    }

    fun onEditClicked() {
        viewState.showViewsInEditState()
    }

    fun onCloseClicked() {
        viewState.showRecordingData(model)
        viewState.showViewsInViewState()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        router.newRootFlow(Screens.MainFlow)
    }
}