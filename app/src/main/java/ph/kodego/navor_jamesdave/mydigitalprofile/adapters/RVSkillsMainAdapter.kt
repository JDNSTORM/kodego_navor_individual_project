package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory

/**
 * TODO: Setting ClickListener inside ViewHolder is inefficient because it is redundant but same
 * TODO: Nested RecyclerViews are not affected by ViewHolder.OnClickListener
 */
class RVSkillsMainAdapter(private val skills: ArrayList<SkillMainCategory>): RecyclerView.Adapter<ViewHolder>() {
    private var clickCounter = 0

    interface AdapterActions{
        fun holderClickNotify(position: Int)
    }
    private var adapterActions: AdapterActions? = null
    fun setAdapterActions(adapterActions: AdapterActions){
        this.adapterActions = adapterActions
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
        val skillMainCategory = skills[position]
        val binding = holder.binding as ViewholderSkillsMainBinding

        with(binding){
            root.setOnClickListener {
//                Snackbar.make(binding.root, "ItemView Clicked: ${++clickCounter}, Layout Position: ${holder.layoutPosition}, Adapter Position: ${holder.adapterPosition}", Snackbar.LENGTH_SHORT).show()
                adapterActions?.holderClickNotify(position) ?: Log.e("AdapterError", "adapterActions not set")
            }

            skillMain.setText(skillMainCategory.categoryMain)
            val skillSubAdapter = RVSkillSubAdapter(skillMainCategory.subCategories)
            skillSubAdapter.setAdapterActions(adapterActions!!)

            listSkillSub.layoutManager = LinearLayoutManager(root.context)
            listSkillSub.adapter = skillSubAdapter
        }
    }
}