package com.anilynime.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.anilynime.app.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AnimeAdapter
    private var currentTab = "latest"
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupTabs()
        setupSearch()

        binding.swipeRefresh.setOnRefreshListener {
            loadAnimeData()
        }

        loadAnimeData()
    }

    private fun setupRecyclerView() {
        adapter = AnimeAdapter { item ->
            if (item.slug.contains("episode") || item.slug.matches(Regex(".*-\\d+$"))) {
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("SLUG", item.slug)
                    putExtra("TITLE", item.title)
                }
                startActivity(intent)
            } else {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("SLUG", item.slug)
                    putExtra("TITLE", item.title)
                }
                startActivity(intent)
            }
        }

        binding.rvAnime.layoutManager = GridLayoutManager(this, 2)
        binding.rvAnime.adapter = adapter
    }

    private fun setupTabs() {
        val tabs = listOf(
            "Terbaru" to "latest",
            "Donghua" to "donghua",
            "Movie" to "movie"
        )

        tabs.forEach { (label, _) ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(label))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { pos ->
                    currentTab = tabs[pos].second
                    currentPage = 1
                    loadAnimeData()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getAnimeList(action = "search", query = query)
                adapter.submitList(response.items ?: emptyList())
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Gagal memuat: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadAnimeData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = false

        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getAnimeList(action = currentTab, page = currentPage)
                adapter.submitList(response.items ?: emptyList())
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Gagal memuat anime: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
