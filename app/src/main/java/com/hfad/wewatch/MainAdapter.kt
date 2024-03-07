package com.hfad.wewatch

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.hfad.wewatch.model.Movie
import com.hfad.wewatch.network.RetrofitClient
import kotlinx.coroutines.runBlocking
import java.util.HashSet

class MainAdapter(internal var movieList: List<Movie>, internal var context: Context) : RecyclerView.Adapter<MainAdapter.MoviesHolder>() {
    // HashMap to keep track of which items were selected for deletion
    val selectedMovies = HashSet<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_movie_main, parent, false)
        return MoviesHolder(v)
    }

    override fun onBindViewHolder(holder: MoviesHolder, position: Int) {
        holder.titleTextView.text = movieList[position].title
        holder.releaseDateTextView.text = movieList[position].releaseDate
        if (movieList[position].posterPath.equals("")) {
            holder.movieImageView.setImageDrawable(context.getDrawable(R.drawable.ic_local_movies_gray))
        } else {
            Picasso.get().load(RetrofitClient.TMDB_IMAGEURL + movieList[position].posterPath).into(holder.movieImageView)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MoviesHolder(v: View) : RecyclerView.ViewHolder(v) {

        internal var titleTextView: TextView
        internal var releaseDateTextView: TextView
        internal var movieImageView: ImageView
        internal var checkBox: CheckBox

        init {
            titleTextView = v.findViewById(R.id.title_textview)
            releaseDateTextView = v.findViewById(R.id.release_date_textview)
            movieImageView = v.findViewById(R.id.movie_imageview)
            checkBox = v.findViewById(R.id.checkbox)
            checkBox.setOnClickListener {
                val adapterPosition = adapterPosition
                if (!selectedMovies.contains(movieList[adapterPosition])) {
                    checkBox.isChecked = true
                    selectedMovies.add(movieList[adapterPosition])
                } else {
                    checkBox.isChecked = false
                    selectedMovies.remove(movieList[adapterPosition]) // Исправлено на remove
                }
            }
        }

    }
}

