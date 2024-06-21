
package com.dutaram.selfquest

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class Slideadapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layouts = intArrayOf(
        R.layout.slide1,
        R.layout.slide2,
        R.layout.slide3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == R.layout.slide3) {
            Slide3ViewHolder(view)
        } else {
            SlideViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Slide3ViewHolder) {
            holder.button.setOnClickListener {
                val intent = Intent(context, SignInClass::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = layouts.size

    override fun getItemViewType(position: Int): Int = layouts[position]

    open inner class SlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    inner class Slide3ViewHolder(view: View) : SlideViewHolder(view) {
        val button: Button = view.findViewById(R.id.start_button)
    }
}
