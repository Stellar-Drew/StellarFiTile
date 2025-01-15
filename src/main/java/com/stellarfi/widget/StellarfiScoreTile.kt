package com.stellarfi.widget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.stellarfi.widget.R

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

class StellarfiScoreTile : Fragment() {
    private val token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzdGVsbGFyZmkiLCJzdWIiOiJhY2NfY3VzdF8wbXRha2F6NXVxZm4iLCJqdGkiOiI3NTVjNTI5Mi0xMDM2LTQxOTktYWU5ZC1jZjc1ODMyYThhMDQiLCJpYXQiOjE3MzY4NzExNjIsImF1ZCI6ImFjY19lM25uajI5cHN2b3MiLCJyb2xlIjoiYWNjb3VudF9jdXN0b21lciIsImV4cCI6MTczNjg3MjA2Mn0.lOp9H9vvZyFFSTo8HQC1OR9YJyL2Xmv2sMqU4NYfSgc";
    private lateinit var url: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(layoutId(), container, false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun layoutId(): Int = R.layout.fragment_stellarfi_score_tile

    var delegate: WebBrowserContract.Delegate? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val htmlContent = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <script type="module" src="https://components-stg.stellarfi.com/staging/latest/credit-score/index.esm.js"></script>
    <title>Hello World</title>
</head>
<body>
    <h1>Hello, Dude!</h1>

    <credit-score token ="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzdGVsbGFyZmkiLCJzdWIiOiJhY2NfY3VzdF8wbXRha2F6NXVxZm4iLCJqdGkiOiI3NTVjNTI5Mi0xMDM2LTQxOTktYWU5ZC1jZjc1ODMyYThhMDQiLCJpYXQiOjE3MzY4NzExNjIsImF1ZCI6ImFjY19lM25uajI5cHN2b3MiLCJyb2xlIjoiYWNjb3VudF9jdXN0b21lciIsImV4cCI6MTczNjg3MjA2Mn0.lOp9H9vvZyFFSTo8HQC1OR9YJyL2Xmv2sMqU4NYfSgc"</credit-score>
    <h2>Hello, Dude 2</h1>
</body>
</html>
            """

        webView.loadData(htmlContent, "text/html", "UTF-8")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

class FragmentManager {
    companion object {
        fun showFragment(activity: AppCompatActivity) {
            if (activity.supportFragmentManager.findFragmentById(android.R.id.content) == null) {
                activity.supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, StellarfiScoreTile())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}


