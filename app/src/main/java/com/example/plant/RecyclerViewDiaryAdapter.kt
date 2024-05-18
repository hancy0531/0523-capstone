import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.plant.DiaryListItem

import com.example.plant.PlantListItem
import com.example.plant.R
import com.bumptech.glide.Glide
import com.example.plant.Fragment_Diary2
import com.example.plant.Fragment_Diary3

class RecyclerViewDiaryAdapter(private val mList: ArrayList<DiaryListItem>) :
    RecyclerView.Adapter<RecyclerViewDiaryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title: TextView = itemView.findViewById(R.id.diaryTitle)
        var content: TextView = itemView.findViewById(R.id.diaryContent)
        var image: ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.diary_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: DiaryListItem = mList[position]
        // holder.image.setImageResource(item.getImgSrc())
        holder.title.text = item.getDiaryTitle()
        holder.content.text = item.getDiaryContent()
        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.context)
            .load(item.getImageUrl())
            .into(holder.image)
        holder.itemView.setOnClickListener{
            val context: Context = holder.itemView.context

            // 클릭한 아이템의 정보를 번들에 담음
            val fragment = Fragment_Diary3()
            val bundle = Bundle()
            bundle.putInt("itemId", item.getItemId())
            bundle.putInt("plantId", item.getPlantId())
            bundle.putString("plantName", item.getPlantName())
            bundle.putString("userEmail", item.getUserEmail())
            bundle.putString("diaryTitle", item.getDiaryTitle())
            bundle.putString("diaryContent", item.getDiaryContent())
            bundle.putString("imageUrl", item.getImageUrl())
            bundle.putString("diaryDate", item.getDiaryDate())
            bundle.putString("enrollTime", item.getEnrollTime())
            fragment.arguments = bundle

            // 상세보기 프래그먼트로 전환
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}