package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

data class SkillsMain(
    var title: String = "",
    val subCategories: ArrayList<SkillsSub> = ArrayList()
)