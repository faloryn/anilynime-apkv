package com.anilynime.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anilynime.app.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var slug: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slug = intent.getStringExtra("SLUG") ?: ""
        val title = intent.getStringExtra("TITLE") ?: ""
        binding.tvDetailTitle.text = title

        loadDetail(slug)
    }

    private fun loadDetail(currentSlug: String) {
        binding.detailLoader.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val data = ApiClient.instance.getAnimeDetail(slug = currentSlug)
                binding.tvDetailTitle.text = data.title
                binding.tvSynopsis.text = data.synopsis ?: "Tidak ada sinopsis."

                Glide.with(this@DetailActivity)
                    .load(data.thumbnail)
                    .into(binding.ivDetailPoster)

                val metaText = data.metadata?.entries?.joinToString("\n") { "${it.key}: ${it.value}" } ?: ""
                binding.tvMetadata.text = metaText

                binding.chipGroupEpisodes.removeAllViews()
                data.episodes?.forEach { ep ->
                    val chip = Chip(this@DetailActivity).apply {
                        text = ep.title.ifEmpty { "Episode ${ep.episode}" }
                        setOnClickListener {
                            val intent = Intent(this@DetailActivity, PlayerActivity::class.java).apply {
                                putExtra("SLUG", ep.slug)
                                putExtra("TITLE", ep.title)
                            }
                            startActivity(intent)
                        }
                    }
                    binding.chipGroupEpisodes.addView(chip)
                }

            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Gagal memuat detail: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.detailLoader.visibility = View.GONE
            }
        }
    }
}
