package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentEducationBinding

fun FragmentEducationBinding.loadData() {
    listEducation.visibility = View.GONE
    loadingData.visibility = View.VISIBLE
}
fun FragmentEducationBinding.showData() {
    listEducation.visibility = View.VISIBLE
    loadingData.visibility = View.GONE
}