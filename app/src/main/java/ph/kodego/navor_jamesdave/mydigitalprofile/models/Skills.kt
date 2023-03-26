package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
open class SkillMainCategory(
    var categoryMainID: String = "",
    val profileID: String = "",
    var categoryMain: String = ""
): Parcelable{
    val subCategories: ArrayList<SkillSubCategory> = ArrayList()

//    companion object : Parceler<SkillMainCategory> {
//        override fun SkillMainCategory.write(parcel: Parcel, flags: Int) {
//            // Custom write implementation
//        }
//
//        override fun create(parcel: Parcel): SkillMainCategory {
//            // Custom read implementation
//        }
//    }
    constructor( //TODO: Remove after trials
        categoryMainID: String = "",
        profileID: String = "",
        categoryMain: String = "",
        subCategories: ArrayList<SkillSubCategory> = ArrayList()
    ): this(categoryMainID, profileID, categoryMain){
        this.subCategories.addAll(subCategories)
    }
}

@Parcelize
open class SkillSubCategory(
    var categorySubID: String = "",
    var categorySub: String = "",
): SkillMainCategory(), Parcelable{
    val skills: ArrayList<Skill> = ArrayList()

    constructor( //TODO: Remove after trials
        categorySubID: String = "",
        categorySub: String = "",
        skills: ArrayList<Skill> = ArrayList()
    ): this(categorySubID, categorySub){
        this.skills.addAll(skills)
    }
    constructor(categoryMainID: String): this(){
        this.categoryMainID = categoryMainID
    }
}

@Parcelize
data class Skill(
    var skillID: String = "",
    var skill: String = ""
): SkillSubCategory(), Parcelable
