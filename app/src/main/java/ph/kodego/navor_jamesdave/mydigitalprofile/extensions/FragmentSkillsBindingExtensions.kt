package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentSkillsBinding

fun FragmentSkillsBinding.loadData(){
    listSkills.visibility = View.GONE
    loadingData.visibility = View.VISIBLE
}
fun FragmentSkillsBinding.showData(){
    listSkills.visibility = View.VISIBLE
    loadingData.visibility = View.GONE
}