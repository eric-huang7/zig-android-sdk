package com.ziggeo.androidsdk.demo.presentation.contactus

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


/**
 * Created by Alexander Bedulin on 25-Sep-19.
 * Ziggeo, Inc.
 * alexb@ziggeo.com
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface ContactUsView : MvpView {

    fun openSupportPage()

    fun sendEmailToZiggeo()

}