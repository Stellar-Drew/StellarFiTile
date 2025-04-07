package com.stellarfi.widget

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
import org.json.JSONObject

class StellarfiCreditScoreHistory : Fragment() {

    private var token: String? = null

    private var isSandbox: Boolean = false
    private var optionParameters: Map<String, Any>? = null
    private lateinit var webView: WebView

    companion object {
        private const val ARG_TOKEN = "arg_token"
        private const val ARG_IS_SANDBOX = "arg_is_sandbox"
        private const val ARG_OPTION_PARAMETERS = "arg_option_parameters"


        fun newInstance(
            token: String? = null,
            isSandbox: Boolean = false,
            optionParameters: Map<String, Any>? = null
        ): StellarfiCreditScoreHistory {
            val fragment = StellarfiCreditScoreHistory()
            val args = Bundle().apply {
                putString(ARG_TOKEN, token)
                putBoolean(ARG_IS_SANDBOX, isSandbox)
                optionParameters?.let {
                    val json = JSONObject(it).toString()
                    putString(ARG_OPTION_PARAMETERS, json)
                }
            }
            fragment.arguments = args
            return fragment
        }
    }

    var handleEvents: ((JSONObject) -> Unit)? = null
        set(value) {
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
            isSandbox = it.getBoolean(ARG_IS_SANDBOX, false)
            it.getString(ARG_OPTION_PARAMETERS)?.let { jsonString ->
                try {
                    val json = JSONObject(jsonString)
                    val map = mutableMapOf<String, Any>()
                    json.keys().forEach { key ->
                        map[key] = json.get(key)
                    }
                    optionParameters = map
                } catch (e: Exception) {
                    Log.e("StellarfiCreditScoreHistory", "Error parsing optionParameters JSON", e)
                    optionParameters = null
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stellarfi_credit_score_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("StellarfiCreditScoreHistory", "onViewCreated called")

        webView = view.findViewById<WebView>(R.id.webView)
        if (webView == null) {
            Log.e("StellarfiCreditScoreHistory", "webView is null! Check your layout file.")
            return
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("StellarfiCreditScoreHistory", "WebView page finished loading: $url")
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("StellarfiCreditScoreHistory", "WebView error: $errorCode, $description, $failingUrl")
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.loadWithOverviewMode = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        WebView.setWebContentsDebuggingEnabled(true)

        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false

        webView.addJavascriptInterface(WebAppInterface(this), "Android") // Pass fragment to interface

        Log.d("StellarfiCreditScoreHistory", "Generating HTML content...")
        val htmlContent = generateHtmlContent()
        Log.d("StellarfiCreditScoreHistory", "HTML content: $htmlContent")

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        Log.d("StellarfiCreditScoreHistory", "loadDataWithBaseURL called")
    }


    class WebAppInterface(private val fragment: StellarfiCreditScoreHistory) { // Use fragment
        private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        @JavascriptInterface
        fun onData(callbackId: String) {
            Log.d("On data", "On data called")
            GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                try {
                    Log.d("On data", "Before the GET")
                    val response = NetworkUtils.simpleGetRequest("https://nxqjufdmmgm4a434g2kks2jjem0adtkd.lambda-url.us-east-2.on.aws/")
                    if (response != null) {
                        Log.d("On data", response)
                        fragment.webView.post { // Use fragment.webView
                            fragment.webView.evaluateJavascript("window.handleResponse('$callbackId', '$response')", null)
                        }
                    } else {
                        Log.d("On data", "Response is null")
                        fragment.webView.post {
                            fragment.webView.evaluateJavascript("window.handleResponse('$callbackId', null)", null)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    fragment.webView.post {
                        fragment.webView.evaluateJavascript("window.handleResponse('$callbackId', null)", null)
                    }
                }
            }
        }


        @JavascriptInterface
        fun log(message: String) {
            Log.d("WebViewLog", message)
        }

        @JavascriptInterface
        fun onStellarfiCallback(data: String) {
            Log.d("StellarFiCallback", "Received callback data: $data")
            try {
                val jsonObject = JSONObject(data)
                fragment.handleEvents?.invoke(jsonObject)  // Use fragment's callback
            } catch (e: Exception) {
                Log.e("StellarfiCreditScoreHistory", "Error parsing callback data as JSON: $data", e)
            }
        }
    }

    private fun generateHtmlContent(): String {
        val optionParamsInitialization = optionParameters?.entries?.joinToString("\n") { (key, value) ->
            val valueString = when (value) {
                is String -> JSONObject.quote(value)
                is Number -> value.toString()
                is Boolean -> value.toString()
                else -> JSONObject.quote(value.toString())
            }
            "creditHistoryComponent.$key = $valueString;"
        } ?: ""

        return """
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>StellarFi Connect JS only web component example</title>
            <script
                type="module"
                src="https://components-staging.connect.stellarfi.com/staging/latest/credit-score-history/index.esm.js"
            ></script>

            <script type="module">
                window.handleResponse = function(callbackId, response) {
                    if (response) {
                        console.log("handleResponse:", response);
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
                              console.log("fetchToken response:", response);
                              const parsedResponse = JSON.parse(response);
                              const token = parsedResponse.session.token;
                              const data = {token:token, ttlSeconds: parsedResponse.session.ttl_seconds}
                              resolve(data);
                            } else {
                                reject("Response is null");
                            }
                        };
                        Android.onData(callbackId);
                    });
                }

                // Create the credit-score-history component
                const creditHistoryComponent = document.createElement('stellarfi-credit-score-history');

                creditHistoryComponent.isSandbox = $isSandbox;
                $optionParamsInitialization
                
                ${if (token.isNullOrEmpty()) "creditHistoryComponent.fetchToken = fetchToken;" else "creditHistoryComponent.token = \"$token\";"}
                
                // Append the component to the document body
                document.body.appendChild(creditHistoryComponent);

              window.addEventListener('stellar-component-event', (event) => {
                console.log('Captured:', event);
                console.log("stellarfiCallback event:", event.detail); // Log the event
                Android.onStellarfiCallback(JSON.stringify(event.detail));
              })

            </script>
        </head>
        <body>
        </body>
    </html>
    """.trimIndent()
    }
}