package com.stellarfi.widget

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class StellarfiCreditFactors() : Fragment() {
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = arguments?.getString("TOKEN")
        if (token == null) {
            throw IllegalArgumentException("Token is required")
        }
    }

    fun layoutId(): Int = R.layout.fragment_stellarfi_credit_score_history

    var delegate: WebBrowserContract.Delegate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.settings.domStorageEnabled = true
        webView.settings.displayZoomControls = true
        webView.settings.loadWithOverviewMode = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        WebView.setWebContentsDebuggingEnabled(true)

        // Disable scrollbars
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false

        // Add the JavaScript interface
        webView.addJavascriptInterface(WebAppInterface(requireContext()), "Android")

        // Check if token is available before proceeding
        if (token != null) {
            val htmlContent = generateHtmlContent(token!!)
            webView.loadData(htmlContent, "text/html", "UTF-8")
        } else {
            Log.e("StellarfiCreditAccounts", "Token is null. Cannot load WebView content.")
            // Handle the error appropriately, e.g., show an error message to the user
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    // WebAppInterface class
    class WebAppInterface(private val mContext: Context) {
        /** Show a toast from the web page */
        @JavascriptInterface
        fun showToast(toast: String) {
            Log.d("WebAppInterface", "showToast: $toast")
        }
        @JavascriptInterface
        fun onData(value: String) {
            Log.d("WebAppInterface", "onData: $value")
        }
    }

    private fun generateHtmlContent(token: String): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <style>
                    body {
                        overflow: hidden; /* Hide scrollbars */
                    }
                </style>
                   <script>
                   window.process = { env: {} }; // Basic polyfill for process.env
               </script>
               <script type="module" src="https://components-stg.stellarfi.com/staging/latest/credit-factors/index.esm.js"></script>
                <title>Hello World</title>
                <script>
                    function myCustomFunction() {
                        Android.showToast("Execute show Toast");
                        Android.onData("Execute data passed to native");
                    }
                    myCustomFunction();
                </script>
            </head>
            <body>
            <div id="wrapper">
                <credit-factors token ="$token"></credit-factors>
            </div>
            </body>
            <style> 
              #wrapper{ width: 1450px; height: 1450px; margin: 0px auto;}
            </style>
            
            </html>
        """.trimIndent()
    }

    companion object {
        fun newInstance(token: String): StellarfiCreditFactors {
            val fragment = StellarfiCreditFactors()
            val args = Bundle()
            args.putString("TOKEN", token)
            fragment.arguments = args
            return fragment
        }
    }
}