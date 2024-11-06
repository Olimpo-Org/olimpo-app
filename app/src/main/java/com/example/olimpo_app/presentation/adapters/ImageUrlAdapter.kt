import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olimpo_app.databinding.ItemImageViewBinding

class ImageUrlAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImageUrlAdapter.ImageViewHolder>() {

    // ViewHolder usando View Binding
    class ImageViewHolder(val binding: ItemImageViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.binding.imageView.context)
            .load(imageUrl)
            .into(holder.binding.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}
