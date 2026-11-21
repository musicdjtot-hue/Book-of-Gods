package com.knigabogov.reader

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewAssetLoader

class MainActivity : ComponentActivity() {
    private lateinit var rootContainer: FrameLayout
    private lateinit var webView: WebView

    private fun applyNativeTheme(themeName: String) {
        val (bgHex, isLightBars) = when (themeName.lowercase()) {
            "night" -> "#090B0E" to false
            "sepia" -> "#EFE6D5" to true
            "paper" -> "#FFFFFF" to true
            else -> "#F7F4EE" to true
        }
        val bgColor = Color.parseColor(bgHex)
        rootContainer.setBackgroundColor(bgColor)
        window.decorView.setBackgroundColor(bgColor)
        val controller = WindowCompat.getInsetsController(window, rootContainer)
        controller.isAppearanceLightStatusBars = isLightBars
        controller.isAppearanceLightNavigationBars = isLightBars
    }

    inner class AndroidThemeBridge {
        @JavascriptInterface
        fun syncTheme(themeName: String) {
            runOnUiThread {
                applyNativeTheme(themeName)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        rootContainer = FrameLayout(this)
        webView = WebView(this)
        rootContainer.addView(
            webView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setContentView(rootContainer)
        applyNativeTheme("parchment")

        ViewCompat.setOnApplyWindowInsetsListener(rootContainer) { view, insets ->
            val sysBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(
                sysBars.left,
                sysBars.top,
                sysBars.right,
                maxOf(sysBars.bottom, ime.bottom)
            )
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(rootContainer)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            allowFileAccess = false
            allowContentAccess = false
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            displayZoomControls = false
        }

        webView.addJavascriptInterface(AndroidThemeBridge(), "AndroidThemeBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        webView.loadUrl("https://appassets.androidplatform.net/assets/www/index.html")
    }
}
