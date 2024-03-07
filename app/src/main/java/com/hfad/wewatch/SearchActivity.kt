package com.hfad.wewatch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.wewatch.model.*
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {

    private val TAG = "SearchActivity"
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var noMoviesTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var query =  ""

    private var dataSource = RemoteDataSource()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)
        searchResultsRecyclerView = findViewById(R.id.search_results_recyclerview)
        noMoviesTextView = findViewById(R.id.no_movies_textview)
        progressBar = findViewById(R.id.progress_bar)

        val i = intent
        query = i.getStringExtra(SEARCH_QUERY) ?: "" // Обновите значение переменной query здесь
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        progressBar.visibility = View.VISIBLE
        job = coroutineScope.launch {
            getSearchResults()
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    private fun setupViews() {
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private suspend fun getSearchResults() {
        try {
            val tmdbResponse = dataSource.searchResultsObservable(query) // Assuming dataSource has a method for searching movies
            displayResult(tmdbResponse)
        } catch (e: Exception) {
            displayError("Error fetching Movie Data")
        } finally {
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun displayResult(tmdbResponse: TmdbResponse) {
        progressBar.visibility = View.INVISIBLE

        if (tmdbResponse.totalResults == null || tmdbResponse.totalResults == 0) {
            searchResultsRecyclerView.visibility = View.INVISIBLE
            noMoviesTextView.visibility = View.VISIBLE
        } else {
            adapter = SearchAdapter(tmdbResponse.results ?: emptyList(), this@SearchActivity, itemListener)
            searchResultsRecyclerView.adapter = adapter

            searchResultsRecyclerView.visibility = View.VISIBLE
            noMoviesTextView.visibility = View.INVISIBLE
        }
    }

    private fun showToast(string: String) {
        Toast.makeText(this@SearchActivity, string, Toast.LENGTH_LONG).show()
    }

    private fun displayError(string: String) {
        showToast(string)
    }

    companion object {
        const val SEARCH_QUERY = "searchQuery"
        const val EXTRA_TITLE = "SearchActivity.TITLE_REPLY"
        const val EXTRA_RELEASE_DATE = "SearchActivity.RELEASE_DATE_REPLY"
        const val EXTRA_POSTER_PATH = "SearchActivity.POSTER_PATH_REPLY"
    }

    internal var itemListener: SearchAdapter.RecyclerItemListener = object :
        SearchAdapter.RecyclerItemListener {
        override fun onItemClick(view: View, position: Int) {
            val movie = adapter.getItemAtPosition(position)

            val replyIntent = Intent()
            replyIntent.putExtra(EXTRA_TITLE, movie.title)
            replyIntent.putExtra(EXTRA_RELEASE_DATE, movie.getReleaseYearFromDate())
            replyIntent.putExtra(EXTRA_POSTER_PATH, movie.posterPath)
            setResult(Activity.RESULT_OK, replyIntent)

            finish()
        }
    }

}
