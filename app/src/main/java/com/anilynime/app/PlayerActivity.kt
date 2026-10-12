package com.anilynime.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anilynime.app.databinding.ActivityPlayerBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var slug: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slug = intent.getStringExtra("SLUG") ?: ""
        title = intent.getStringExtra("TITLE") ?: ""

        binding.tvPlayerTitle.text = title
        setupWebView()
        loadEpisodeData(slug)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.allowFileAccess = true

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.playerLoader.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.playerLoader.visibility = View.GONE
                }
            }
        }
    }

    private fun loadEpisodeData(currentSlug: String) {
        binding.playerLoader.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val data = ApiClient.instance.getEpisode(slug = currentSlug)
                binding.tvPlayerTitle.text = data.title

                val embeds = data.embeds ?: emptyList()
                binding.chipGroupServers.removeAllViews()

                if (embeds.isNotEmpty()) {
                    // Load the first priority embed
                    val initialUrl = embeds[0].streamUrl ?: embeds[0].iframe
                    binding.webView.loadUrl(initialUrl)

                    embeds.forEachIndexed { index, embed ->
                        val chip = Chip(this@PlayerActivity).apply {
                            text = embed.server
                            isCheckable = true
                            isChecked = (index == 0)
                            setOnClickListener {
                                val url = embed.streamUrl ?: embed.iframe
                                binding.webView.loadUrl(url)
                            }
                        }
                        binding.chipGroupServers.addView(chip)
                    }
                }

                // Prev/Next Navigation
                if (!data.navigation?.prev.isNullOrEmpty()) {
                    binding.btnPrev.visibility = View.VISIBLE
                    binding.btnPrev.setOnClickListener {
                        loadEpisodeData(data.navigation!!.prev!!)
                    }
                } else {
                    binding.btnPrev.visibility = View.GONE
                }

                if (!data.navigation?.next.isNullOrEmpty()) {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnNext.setOnClickListener {
                        loadEpisodeData(data.navigation!!.next!!)
                    }
                } else {
                    binding.btnNext.visibility = View.GONE
                }

            } catch (e: Exception) {
                Toast.makeText(this@PlayerActivity, "Gagal memutar: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.playerLoader.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }
}
