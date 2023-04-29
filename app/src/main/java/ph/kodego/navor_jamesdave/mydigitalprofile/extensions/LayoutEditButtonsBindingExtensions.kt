package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutEditButtonsBinding

fun LayoutEditButtonsBinding.saveInterface(){
    btnSave.visibility = View.VISIBLE
    btnUpdate.visibility = View.GONE
    btnDelete.visibility = View.GONE
}

fun LayoutEditButtonsBinding.updateInterface(){
    btnSave.visibility = View.GONE
    btnUpdate.visibility = View.VISIBLE
    btnDelete.visibility = View.GONE
}

fun LayoutEditButtonsBinding.editInterface(){
    btnSave.visibility = View.GONE
    btnUpdate.visibility = View.VISIBLE
    btnDelete.visibility = View.VISIBLE
}