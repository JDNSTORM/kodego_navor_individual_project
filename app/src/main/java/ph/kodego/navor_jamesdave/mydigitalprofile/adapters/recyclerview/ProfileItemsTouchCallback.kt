package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView

abstract class ProfileItemsTouchCallback(): ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, UP or DOWN)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val oldPosition = viewHolder.adapterPosition
        val newPosition = target.adapterPosition
        recyclerView.adapter?.notifyItemMoved(oldPosition, newPosition)
        updateItem(oldPosition, newPosition)
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    abstract fun updateItem(oldPosition: Int, newPosition: Int)
}