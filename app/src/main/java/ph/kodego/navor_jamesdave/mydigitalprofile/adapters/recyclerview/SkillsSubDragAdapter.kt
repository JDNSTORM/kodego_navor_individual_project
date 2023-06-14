package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillsSubDragBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub
import java.util.Collections

class SkillsSubDragAdapter(): ItemsAdapter<SkillsSub>() {
    val touchHelper = ItemTouchHelper(object: ProfileItemsTouchCallback(){
        override fun updateItem(oldPosition: Int, newPosition: Int) {
            val newItems = ArrayList(items)
            Collections.swap(newItems, oldPosition, newPosition)
            items = newItems
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillsSubDragBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillsSubDragBinding
        val subSkill = items[position]
        bind(binding, subSkill)
        binding.handle.apply {
            setOnClickListener { }
            setOnTouchListener { v, event ->
                when(event.action){
                    KeyEvent.ACTION_DOWN -> {
                        touchHelper.startDrag(holder)
                        true
                    }
                    else -> {
                        v.performClick()
                        false
                    }
                }
            }
        }
    }

    private fun bind(binding: ItemSkillsSubDragBinding, subSkill: SkillsSub) {
        binding.subtitle.text = subSkill.subtitle
    }

    fun skills(): List<SkillsSub> = items
}