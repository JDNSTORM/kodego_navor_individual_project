package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill

class RVSkillsAdapter(private val skillList: ArrayList<Skill>): RecyclerView.Adapter<ViewHolder>() {
    private var categoryPosition: Int? = null
    private var adapterEvents: RVSkillsMainAdapter.AdapterEvents? = null

    fun setCategoryPosition(position: Int){
        this.categoryPosition = position
    }
    fun setAdapterActions(adapterEvents: RVSkillsMainAdapter.AdapterEvents){
        this.adapterEvents = adapterEvents
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skill = skillList[position]
        val binding = holder.binding as ViewholderSkillBinding

        binding.skill.setText(skill.skill)
        binding.root.setOnClickListener{
//            Snackbar.make(binding.root, "Skill Clicked", Snackbar.LENGTH_SHORT).show()
            adapterEvents?.holderClickNotify(categoryPosition!!)
        }
    }
}