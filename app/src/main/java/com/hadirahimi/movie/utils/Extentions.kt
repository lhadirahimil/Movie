package com.hadirahimi.movie.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.init(
    layoutManager : RecyclerView.LayoutManager , adapter : RecyclerView.Adapter<*>
)
{
    this.layoutManager = layoutManager
    this.adapter = adapter
    this.setHasFixedSize(true)
}

fun View.show(shown : Boolean , container : View)
{
    when (shown)
    {
        true ->
        {
            this.visibility = View.VISIBLE
            container.visibility = View.GONE
        }
        false ->
        {
            this.visibility = View.GONE
            container.visibility = View.VISIBLE
        }
    }
}

fun View.visible(shown : Boolean)
{
    when (shown)
    {
        true -> this.visibility = View.VISIBLE
        false -> this.visibility = View.GONE
    }
}