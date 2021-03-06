@file:Suppress("FunctionName")

package com.pawegio.homebudget.login

import com.pawegio.homebudget.*
import com.pawegio.homebudget.util.ToastNotifier
import io.reactivex.Observable
import kotlinx.coroutines.rx2.collect

suspend fun LoginLogic(
    actions: Observable<LoginAction>,
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    toastNotifier: ToastNotifier,
    initPicker: suspend () -> Unit,
    initMain: suspend () -> Unit,
    navigator: Navigator
) {
    if (api.isSignedIn) proceed(repository, navigator, initMain, initPicker)
    actions.collect {
        try {
            api.signIn()
        } catch (e: HomeBudgetApiException) {
            toastNotifier.notify(R.string.sign_in_error)
        }
        if (api.isSignedIn) proceed(repository, navigator, initMain, initPicker)
    }
}

private suspend fun proceed(
    repository: HomeBudgetRepository,
    navigator: Navigator,
    initMainFlow: suspend () -> Unit,
    initPickerFlow: suspend () -> Unit
) {
    if (repository.spreadsheetId != null) {
        navigator.navigate(NavGraph.Action.toMain)
        initMainFlow()
    } else {
        navigator.navigate(NavGraph.Action.toPicker)
        initPickerFlow()
    }
}

sealed class LoginAction {
    object SelectSignIn : LoginAction()
}
