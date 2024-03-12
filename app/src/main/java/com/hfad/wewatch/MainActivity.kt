package com.hfad.wewatch
//этот вариант нормальный, удаляет, но не обновляет добавленное
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hfad.wewatch.model.LocalDataSource
import com.hfad.wewatch.model.Movie
import com.hfad.wewatch.MainAdapter
import com.hfad.wewatch.network.RetrofitClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList


class MainActivity : AppCompatActivity() {

    private lateinit var moviesRecyclerView: RecyclerView
    private var adapter: MainAdapter? = null
    private lateinit var fab: FloatingActionButton
    private lateinit var noMoviesLayout: LinearLayout

    private lateinit var dataSource: LocalDataSource
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getMyMoviesList()
        setupViews()

    }

    override fun onStart() {
        super.onStart()
        dataSource = LocalDataSource(application)
        getMyMoviesList()

    }
    override fun onResume() {
        super.onResume()
        dataSource = LocalDataSource(application)
        getMyMoviesList()

    }
    override fun onStop() {
        super.onStop()
        Log.e(TAG, "stopped")
    }

    private fun setupViews() {
        moviesRecyclerView = findViewById(R.id.movies_recyclerview)
        moviesRecyclerView.layoutManager = LinearLayoutManager(this)
        fab = findViewById(R.id.fab)
        noMoviesLayout = findViewById(R.id.no_movies_layout)
        supportActionBar?.title = "Movies to Watch"
    }

    private fun getMyMoviesList() {
        coroutineScope.launch {
            try {
                dataSource.allMovies.collect { movieList ->
                    displayMovies(movieList)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: $e")
                displayError("Error fetching movie list")

            }
        }
    }



    private fun displayMovies(movieList: List<Movie>?) {
        if (movieList.isNullOrEmpty()) {
            Log.d(TAG, "No movies to display")
            moviesRecyclerView.visibility = INVISIBLE
            noMoviesLayout.visibility = VISIBLE
        } else {
            adapter = MainAdapter(movieList, this@MainActivity)
            moviesRecyclerView.adapter = adapter

            moviesRecyclerView.visibility = VISIBLE
            noMoviesLayout.visibility = INVISIBLE
        }
    }

    //fab onClick
    fun goToAddMovieActivity(v: View) {
        val myIntent = Intent(this@MainActivity, AddMovieActivity::class.java)
        startActivityForResult(myIntent, ADD_MOVIE_ACTIVITY_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_MOVIE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Обновить список фильмов
            getMyMoviesList()
            showToast("Movie successfully added.")
        }
        else {
            displayError("Movie could not be added.")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteMenuItem) {
            val adapter = this.adapter
            if (adapter != null) {
                for (movie in adapter.selectedMovies) {
                    coroutineScope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                dataSource.delete(movie)
                            }
                            showToast("Movies deleted")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error deleting movies: $e")
                            displayError("Error deleting movies")
                        }
                    }
                }
                if (adapter.selectedMovies.size == 1) {
                    showToast("Movie deleted")
                } else if (adapter.selectedMovies.size > 1) {
                    showToast("Movies deleted")
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun showToast(str: String) {
        Toast.makeText(this@MainActivity, str, Toast.LENGTH_LONG).show()
    }

    fun displayError(e: String) {
        showToast(e)
    }

    companion object {
        const val ADD_MOVIE_ACTIVITY_REQUEST_CODE = 1
    }
}