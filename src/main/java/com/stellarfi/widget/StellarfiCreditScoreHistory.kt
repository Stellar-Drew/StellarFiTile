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
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.semantics.dismiss
import androidx.fragment.app.Fragment
import android.app.AlertDialog.Builder
import androidx.fragment.app.FragmentActivity
import java.io.IOException
import java.io.InputStreamReader

class StellarfiCreditScoreHistory() : Fragment() {
    private var token: String? = null
    private lateinit var activityContext: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActivity) {
            activityContext = context
        } else {
            throw IllegalStateException("Context must be a FragmentActivity")
        }
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
        webView.addJavascriptInterface(WebAppInterface(activityContext), "Android")

        // Check if token is available before proceeding
        if (token != null) {
            val htmlContent = generateHtmlContent(token!!)
            webView.loadData(htmlContent, "text/html", "UTF-8")
        } else {
            Log.e("StellarfiCreditScoreHistory", "Token is null. Cannot load WebView content.")
            // Handle the error appropriately, e.g., show an error message to the user
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    // WebAppInterface class
    class WebAppInterface(private val activityContext: FragmentActivity) {
        /** Show an alert dialog from the web page */
        @JavascriptInterface
        fun showAlert(message: String) {
            // Use runOnUiThread to show the AlertDialog on the main thread
            activityContext.runOnUiThread {
                Builder(activityContext) // Use Builder here
                    .setTitle("Alert from Web")
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            Log.d("WebAppInterface", "showAlert: $message")
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
               <script type="module" src="https://components-stg.stellarfi.com/staging/latest/credit-score-history/index.esm.js"></script>
                <title>Hello World</title>
                <script>
                    function myCustomFunction() {
                        Android.showAlert("wahtever");
                        Android.onData("Execute data passed to native");
                    }
                    myCustomFunction();
                </script>
            </head>
            <body>
            <div id="wrapper">
                <credit-score-history token ="$token"></credit-score-history>
            </div>
            </body>
            <style> 
              #wrapper{ width: 1450px; height: 1450px; margin: 0px auto;}
            </style>
            </html>
        """.trimIndent()
    }

    companion object {
        fun newInstance(token: String): StellarfiCreditScoreHistory {
            val fragment = StellarfiCreditScoreHistory()
            val args = Bundle()
            args.putString("TOKEN", token)
            fragment.arguments = args
            return fragment
        }
    }
}