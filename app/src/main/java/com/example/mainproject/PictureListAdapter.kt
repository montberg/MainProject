package com.example.mainproject

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PictureListAdapter(imageList: MutableList<Bitmap>, base64list: ByteArray?) : RecyclerView.Adapter<PictureListAdapter.MyViewHolder>() {
    private var ImageList = imageList
    private var Base64list = base64list
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        var deleteButton:ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView:View = LayoutInflater.from(parent.context).inflate(R.layout.image_list, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image: Bitmap = ImageList[position]
        holder.image.setImageBitmap(image)
        holder.deleteButton.setOnClickListener {
            Base64list = null
            ImageList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return ImageList.size
    }
}