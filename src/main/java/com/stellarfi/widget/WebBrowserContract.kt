package com.stellarfi.widget


interface WebBrowserContract {
    interface Delegate  {
        fun onCloseWebBrowser()
        fun shouldStopLoadingAndClose(url: String): Boolean
    }

    interface View {
        var delegate: Delegate?
    }
}
