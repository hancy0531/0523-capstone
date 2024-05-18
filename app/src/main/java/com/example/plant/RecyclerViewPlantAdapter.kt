import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plant.PlantListItem
import com.example.plant.R

class RecyclerViewPlantAdapter(private val mList: ArrayList<PlantListItem>) :
    RecyclerView.Adapter<RecyclerViewPlantAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.imageView2)
        var name: TextView = itemView.findViewById(R.id.itemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.recyclerview_home_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: PlantListItem = mList[position]
       // holder.image.setImageResource(item.getImgSrc())
        holder.name.text = item.getPlantName()
        Glide.with(holder.itemView.context)
            .load(item.getImageUrl())
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}