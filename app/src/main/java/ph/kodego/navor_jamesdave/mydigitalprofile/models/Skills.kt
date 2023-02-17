package ph.kodego.navor_jamesdave.mydigitalprofile.models

data class SkillMainCategory(
    var id: Int = 0,
    var categoryMain: String = "",
    val subCategories: ArrayList<SkillSubCategory> = ArrayList()
)

data class SkillSubCategory(
    var id: Int = 0,
    val categoryMainID: Int,
    var categorySub: String = "",
    val skills: ArrayList<Skill> = ArrayList()
)

data class Skill(
    var id: Int = 0,
    val categorySubID: Int,
    var skill: String
)
