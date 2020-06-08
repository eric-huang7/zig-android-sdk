package com.ziggeo.androidsdk.demo.presentation.recordings

import com.arellomobile.mvp.InjectViewState
import com.ziggeo.androidsdk.demo.R
import com.ziggeo.androidsdk.demo.Screens
import com.ziggeo.androidsdk.demo.model.data.storage.KVStorage
import com.ziggeo.androidsdk.demo.model.data.storage.VIDEO_TOKEN
import com.ziggeo.androidsdk.demo.model.interactor.RecordingsInteractor
import com.ziggeo.androidsdk.demo.model.system.flow.FlowRouter
import com.ziggeo.androidsdk.demo.model.system.message.SystemMessage
import com.ziggeo.androidsdk.demo.model.system.message.SystemMessageNotifier
import com.ziggeo.androidsdk.demo.presentation.global.BasePresenter
import com.ziggeo.androidsdk.net.exceptions.ResponseException
import com.ziggeo.androidsdk.net.models.videos.VideoModel
import io.reactivex.disposables.Disposable
import javax.inject.Inject


/**
 * Created by Alexander Bedulin on 25-Sep-19.
 * Ziggeo, Inc.
 * alexb@ziggeo.com
 */
@InjectViewState
class RecordingsPresenter @Inject constructor(
    private val recordingsInteractor: RecordingsInteractor,
    private var router: FlowRouter,
    private var kvStorage: KVStorage,
    systemMessageNotifier: SystemMessageNotifier
) : BasePresenter<RecordingsView>(systemMessageNotifier) {

    private var fabActionsExpanded = false
    private var disposable: Disposable? = null

    override fun attachView(view: RecordingsView?) {
        super.attachView(view)
        updateRecordingsList()
    }

    override fun detachView(view: RecordingsView?) {
        super.detachView(view)
        disposable?.dispose()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        router.exit()
    }

    fun onPullToRefresh() {
        updateRecordingsList()
    }

    fun onFabCameraClicked() {
        viewState.startCameraRecorder()
    }

    fun onFabScreenClicked() {
        viewState.startScreenRecorder()
    }

    fun onFabAudioClicked() {
        viewState.startAudioRecorder()
    }

    fun onFabImageClicked() {
        viewState.startImageCapture()
    }

    fun onFabFileClicked() {
        viewState.startFileSelector()
    }

    fun onFabActionsClicked() {
        if (fabActionsExpanded) {
            viewState.startHideAnimationMainFab()
            viewState.hideActionFabs()
        } else {
            viewState.startShowAnimationMainFab()
            viewState.showActionFabs()
        }
        fabActionsExpanded = !fabActionsExpanded
    }

    fun onScrollUp() {
        viewState.showSelectorFab()
    }

    fun onScrollDown() {
        if (fabActionsExpanded) {
            viewState.startHideAnimationMainFab()
            fabActionsExpanded = false
        }
        viewState.hideActionFabs()
        viewState.hideSelectorFab()
    }

    fun onItemClicked(model: VideoModel) {
        kvStorage.put(VIDEO_TOKEN, model.token)
        router.startFlow(Screens.RecordingDetailsFlow)
    }

    private fun updateRecordingsList() {
        disposable = recordingsInteractor.getRecordingsList()
            .doOnSubscribe { viewState.showLoading() }
            .doFinally { viewState.hideLoading() }
            .subscribe({ data ->
                if (data.isEmpty()) {
                    viewState.showNoRecordingsMessage()
                } else {
                    viewState.showRecordingsList(data)
                }
            }, {
                if (it is ResponseException && indexingNotAllowed(it.statusCode)) {
                    systemMessageNotifier.send(SystemMessage(R.string.err_check_indexing))
                } else {
                    commonOnError(it)
                    viewState.showNoRecordingsMessage()
                }
            })
    }

    private fun indexingNotAllowed(code: Int): Boolean {
        val unauthorized = 401
        return code == unauthorized
    }
}