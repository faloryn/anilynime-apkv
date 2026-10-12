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
    private var isSearchMode = false
    private var lastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupTabs()
        setupSearch()
        setupPagination()

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
                    isSearchMode = false
                    updatePaginationUI()
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
                    isSearchMode = true
                    lastQuery = query
                    currentPage = 1
                    updatePaginationUI()
                    performSearch(query)
                } else {
                    isSearchMode = false
                    currentPage = 1
                    updatePaginationUI()
                    loadAnimeData()
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupPagination() {
        binding.btnPrevPage.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePaginationUI()
                if (isSearchMode) {
                    performSearch(lastQuery)
                } else {
                    loadAnimeData()
                }
                binding.rvAnime.scrollToPosition(0)
            }
        }

        binding.btnNextPage.setOnClickListener {
            currentPage++
            updatePaginationUI()
            if (isSearchMode) {
                performSearch(lastQuery)
            } else {
                loadAnimeData()
            }
            binding.rvAnime.scrollToPosition(0)
        }

        updatePaginationUI()
    }

    private fun updatePaginationUI() {
        binding.tvCurrentPage.text = "Halaman $currentPage"
        binding.btnPrevPage.isEnabled = currentPage > 1
        binding.btnPrevPage.alpha = if (currentPage > 1) 1.0f else 0.4f
    }

    private fun performSearch(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = false
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getAnimeList(
                    action = "search",
                    query = query,
                    page = currentPage
                )
                val items = response.items ?: emptyList()
                adapter.submitList(items)
                if (items.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Tidak ada hasil untuk '$query'", Toast.LENGTH_SHORT).show()
                }
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
                val response = ApiClient.instance.getAnimeList(
                    action = currentTab,
                    page = currentPage
                )
                val items = response.items ?: emptyList()
                adapter.submitList(items)
                if (items.isEmpty() && currentPage > 1) {
                    Toast.makeText(this@MainActivity, "Sudah mencapai halaman terakhir", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Gagal memuat anime: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
