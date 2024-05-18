package com.example.plant
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class GridPlantListAdapter(var items: ArrayList<PlantListItem>): BaseAdapter(){
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view =
            LayoutInflater.from(parent?.context).inflate(R.layout.gridview_garden_item, parent, false)
        val item = items[position]
        var itemName = view.findViewById<TextView>(R.id.itemName)
        var itemImage = view.findViewById<ImageView>(R.id.imageView3)
        itemName.text = item.getPlantName()
        parent?.context?.let {
            Glide.with(it)
                .load(item.getImageUrl())
                .into(itemImage)
        }
        //itemImage.setImageResource(R.drawable.img_6)

        return view
    }


}