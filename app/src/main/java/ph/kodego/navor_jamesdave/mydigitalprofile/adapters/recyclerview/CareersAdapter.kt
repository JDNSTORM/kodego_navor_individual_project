package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career.CareerEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Career
import java.util.Collections

class CareersAdapter(): ItemsAdapter<Career>() {
    private var drag: Boolean = false
    private var editDialog: CareerEditDialog? = null
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCareerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemCareerBinding
        val career = items[position]
        binding.bind(career)

        editDialog?.apply {
            binding.root.setOnClickListener { edit(career, items) }
        }
        if (this::touchHelper.isInitialized) {
            binding.handle.apply {
                setOnClickListener {}
                setOnTouchListener { _, event ->
                    when (event.action) {
                        KeyEvent.ACTION_DOWN -> {
                            touchHelper.startDrag(holder)
                            true
                        }
                        else -> {
                            performClick()
                            false
                        }
                    }
                }
            }
        }
    }

    private fun ItemCareerBinding.bind(career: Career){
        position.text = career.position
        companyName.text = career.companyName
        handle.isVisible = drag
        employmentPeriod.isVisible = !drag
        employmentPeriod.text = career.employmentPeriod()
        companyAddress.apply {
            text = career.address.streetAddress
            isVisible = text.isNotEmpty() && !drag
        }
        companyWebsite.apply {
            text = career.website
            isVisible = text.isNotEmpty() && !drag
        }
        companyTelephone.apply {
            text = career.contactNumber.telephone()
            isVisible = text.isNotEmpty() && !drag
        }
        jobDescription.apply {
            text = career.jobDescription
            isVisible = text.isNotEmpty() && !drag
        }
    }

    fun enableEditing(dialog: CareerEditDialog){
        editDialog = dialog
    }

    fun toggleDrag(): Boolean{
        drag = !drag
        notifyItemRangeChanged(0, itemCount)
        return drag
    }

    fun clearToggle(){ if (drag) toggleDrag() }

    fun activateTouchHelper(): ItemTouchHelper{
        touchHelper = ItemTouchHelper(object: ProfileItemsTouchCallback(){
            override fun updateItem(oldPosition: Int, newPosition: Int) {
                val newItems = ArrayList(items)
                Collections.swap(newItems, oldPosition, newPosition)
                items = newItems
            }
        })
        return touchHelper
    }

    fun careers(): List<Career> = items
}