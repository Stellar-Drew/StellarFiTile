package com.stellarfi.widget

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.util.UUID
import kotlin.coroutines.resume

class StellarfiScoreTile : Fragment() {

    private var token: String? = null
    private lateinit var webView: WebView

    companion object {
        fun newInstance(token: String): StellarfiScoreTile {
            val fragment = StellarfiScoreTile()
            fragment.token = token
            return fragment
        }
    }

    var handleEvents: ((JSONObject) -> Unit)? = null
        set(value) {
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stellarfi_credit_score_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        WebView.setWebContentsDebuggingEnabled(true)

        // Disable scrollbars
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false

        // Add the JavaScript interface
        webView.addJavascriptInterface(WebAppInterface(webView), "Android")

        // Check if token is available before proceeding
        token?.let {
            val htmlContent = generateHtmlContent(it)
            webView.loadData(htmlContent, "text/html", "UTF-8")
        } ?: run {
            Log.e("StellarfiScoreTile", "Token is null. Cannot load WebView content.")
        }
    }

    // WebAppInterface class
    class WebAppInterface(private val webView: WebView) {
        private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace() // Log the full stack trace
            // Handle the error appropriately (e.g., show an error message)
        }

        private val callbacks = mutableMapOf<String, String>()

        @JavascriptInterface
        fun onData(callbackId: String) {
            GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                try {
                    Log.d("On data", "Before the GET")
                    val response = NetworkUtils.simpleGetRequest("https://nxqjufdmmgm4a434g2kks2jjem0adtkd.lambda-url.us-east-2.on.aws/")
                    if (response != null) {
                        Log.d("On data", response)
                        // Call the JavaScript callback with the result
                        webView.post {
                            webView.evaluateJavascript("window.handleResponse('$callbackId', '$response')", null)
                        }
                    } else {
                        Log.d("On data", "Response is null")
                        webView.post {
                            webView.evaluateJavascript("window.handleResponse('$callbackId', null)", null)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    webView.post {
                        webView.evaluateJavascript("window.handleResponse('$callbackId', null)", null)
                    }
                }
            }
        }

        @JavascriptInterface
        fun onTest(issue: String) {
            Log.d("On Error", issue)
        }

        @JavascriptInterface
        fun log(message: String) {
            Log.d("WebViewLog", message)
        }
    }

    private fun generateHtmlContent(token: String): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>StellarFi Connect JS only web component example</title>
                    
                    <script
                        type="module"
                        src="https://components-staging.connect.stellarfi.com/staging/latest/credit-score/index.esm.js"
                    ></script>
                    
                    <script type="module">
                        // Override console.log to send messages to Android
                        (function() {
                            var originalConsoleLog = console.log;
                            console.log = function(message) {
                                originalConsoleLog.apply(console, arguments); // Still log to the browser's console
                                Android.log(message); // Send to Android
                            };
                        })();

                        window.handleResponse = function(callbackId, response) {
                            if (response) {
                                console.log(response);
                                window[callbackId](response);
                            } else {
                                console.error("Error fetching token: Response is null");
                                window[callbackId](null);
                            }
                        }

                        var fetchToken = async function() {
                            return new Promise((resolve, reject) => {
                                const callbackId = 'callback_' + Math.random().toString(36).substring(7);
                                window[callbackId] = function(response) {
                                    delete window[callbackId];
                                    if (response) {
                                      const parsedResponse = JSON.parse(response);
                                      const token = parsedResponse.session.token;
                                      resolve(token);
                                    } else {
                                        reject("Response is null");
                                    }
                                };
                                Android.onData(callbackId);
                            });
                        }

                        document.addEventListener('DOMContentLoaded', async () => {
                            // with vanilla JS, we should append append using DOM API once we have all necessary props defined
                            const creditScoreComponent = document.createElement('credit-score')
                            creditScoreComponent.isSandbox = true
                            const token = await fetchToken()
                            creditScoreComponent.token = token
                            document.body.appendChild(creditScoreComponent)
                        })
                    </script>
                </head>
                <body>
                </body>
            </html>
        """.trimIndent()
    }
}