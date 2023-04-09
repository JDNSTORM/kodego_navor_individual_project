package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class SkillMainCategory(
    val profileID: String = ""
): Parcelable{
    var mainCategoryID: String = ""
    var categoryMain: String = ""
    @get:Exclude
    val subCategories: ArrayList<SkillSubCategory> = ArrayList()

    constructor(mainCategoryID: String, profileID: String, categoryMain: String, subCategories: ArrayList<SkillSubCategory> = ArrayList()): this(profileID){
        this.mainCategoryID = mainCategoryID
        this.categoryMain = categoryMain
        this.subCategories.addAll(subCategories)
    }
}

@Parcelize
data class SkillSubCategory(
    val mainCategoryID: String = ""
): Parcelable{
    var subCategoryID: String = ""
    var categorySub: String = ""
    @get:Exclude
    val skills: ArrayList<Skill> = ArrayList()

    constructor(subCategoryID: String, mainCategoryID: String, categorySub: String, skills: ArrayList<Skill> = ArrayList()): this(mainCategoryID){
        this.subCategoryID = subCategoryID
        this.categorySub = categorySub
        this.skills.addAll(skills)
    }
}

@Parcelize
data class Skill(
    val subCategoryID: String = "",
    var skill: String
): Parcelable{
    var skillID: String = ""

    constructor(skillID: String, subCategoryID: String, skill: String): this(subCategoryID, skill){
        this.skillID = skillID
    }
}
