package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentHomeBinding

fun FragmentHomeBinding.loadData(){
    listProfiles.visibility = View.GONE
    loadingData.visibility = View.VISIBLE
}

fun FragmentHomeBinding.showData(){
    listProfiles.visibility = View.VISIBLE
    loadingData.visibility = View.GONE
}