package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillSubBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill

class RVSkillsAdapter(private val skillList: ArrayList<Skill>): RecyclerView.Adapter<ViewHolder>() {
    private var categoryPosition: Int? = null
    private var adapterActions: RVSkillsMainAdapter.AdapterActions? = null

    fun setCategoryPosition(position: Int){
        this.categoryPosition = position
    }
    fun setAdapterActions(adapterActions: RVSkillsMainAdapter.AdapterActions){
        this.adapterActions = adapterActions
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
            adapterActions?.holderClickNotify(categoryPosition!!)
        }
    }
}