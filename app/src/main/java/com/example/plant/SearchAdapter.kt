package com.example.plant

import PlantItem
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable

import android.widget.TextView

class SearchAdapter(private var items: List<PlantItem>): BaseAdapter(), Filterable {

    private var filteredItemList: List<PlantItem> = items

    override fun getCount(): Int {
        return filteredItemList.size
    }

    override fun getItem(position: Int): Any {
        return filteredItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.search_item, parent, false)
        }
        val item = getItem(position) as PlantItem
        var title = view!!.findViewById<TextView>(R.id.searchTitle)
        var content = view.findViewById<TextView>(R.id.searchContent)
        var plantbneNm = view.findViewById<TextView>(R.id.plantbneNm)
        plantbneNm.text = item.plntbneNm//식물학명
        //title.text = item.distbNm
        title.text = item.plantNm
        content.text = item.adviseInfo

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filteredList = ArrayList<PlantItem>()

                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(items)
                } else {
                    val query = constraint.toString().toLowerCase().trim()
                    for (item in items) {
                        if (item.distbNm.toLowerCase().contains(query) ||
                            item.adviseInfo.toLowerCase().contains(query) ||
                            item.plntbneNm.toLowerCase().contains(query) ||
                            item.plantNm.toLowerCase().contains(query)){
                            filteredList.add(item)
                        }
                    }
                }

                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    filteredItemList = results.values as ArrayList<PlantItem>
                    notifyDataSetChanged()
                }
            }
        }
    }
}