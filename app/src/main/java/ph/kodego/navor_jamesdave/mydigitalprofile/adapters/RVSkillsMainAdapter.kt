package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
// TODO: Setting ClickListener inside ViewHolder is inefficient because it is redundant but same
class RVSkillsMainAdapter(private val skills: ArrayList<SkillMainCategory>): RecyclerView.Adapter<RVSkillsMainAdapter.ViewHolder>() {
    private var clickCounter = 0
    inner class ViewHolder(private val binding: ViewholderSkillsMainBinding): RecyclerView.ViewHolder(binding.root) {
        //TODO: SetOnClickListener when clicking on ViewHolder
        init {
            itemView.setOnClickListener {
                Snackbar.make(itemView, "ItemView Clicked: ${++clickCounter}, Layout Position: $layoutPosition, Adapter Position: $adapterPosition", Snackbar.LENGTH_SHORT).show()
            }
        }
        fun bindViewHolder(skillMain: SkillMainCategory){
            binding.skillMain.setText(skillMain.categoryMain)
            val skillSubAdapter = RVSkillSubAdapter(skillMain.subCategories)
            binding.listSkillSub.layoutManager = LinearLayoutManager(itemView.context)
            binding.listSkillSub.adapter = skillSubAdapter

//            binding.root.setOnClickListener {
//                Toast.makeText(itemView.context, "Main Category Clicked: ${++clickCounter}", Toast.LENGTH_SHORT).show()
//            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillsMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return skills.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(skills[position])
    }
}