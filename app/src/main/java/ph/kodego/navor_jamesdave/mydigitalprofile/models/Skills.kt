package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class SkillMainCategory(
    var mainCategoryID: String = "",
    val profileID: String = "",
    var categoryMain: String = ""
): Parcelable{
    @get:Exclude
    val subCategories: ArrayList<SkillSubCategory> = ArrayList()

    constructor(mainCategoryID: String, profileID: String, categoryMain: String, subCategories: ArrayList<SkillSubCategory> = ArrayList()):
        this(mainCategoryID, profileID, categoryMain){
            this.subCategories.addAll(subCategories)
        }
    constructor(profileID: String): this("", profileID, "")
}

@Parcelize
data class SkillSubCategory(
    var subCategoryID: String = "",
    val mainCategoryID: String = "",
    var categorySub: String = ""
): Parcelable{
    @get:Exclude
    val skills: ArrayList<Skill> = ArrayList()

    constructor(subCategoryID: String, mainCategoryID: String, categorySub: String, skills: ArrayList<Skill> = ArrayList()): this(subCategoryID, mainCategoryID, categorySub){
        this.skills.addAll(skills)
    }
    constructor(mainCategoryID: String): this("", mainCategoryID, "")
}

@Parcelize
data class Skill(
    var skillID: String = "",
    val subCategoryID: String = "",
    var skill: String = ""
): Parcelable{

    constructor(subCategoryID: String, skill: String): this("", subCategoryID, skill)
}
