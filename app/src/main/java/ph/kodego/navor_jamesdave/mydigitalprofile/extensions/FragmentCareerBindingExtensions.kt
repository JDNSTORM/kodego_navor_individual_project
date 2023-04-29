package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentCareerBinding

fun FragmentCareerBinding.loadData(){
    listCareer.visibility = View.GONE
    loadingData.visibility = View.VISIBLE
}
fun FragmentCareerBinding.showData(){
    listCareer.visibility = View.VISIBLE
    loadingData.visibility = View.GONE
}