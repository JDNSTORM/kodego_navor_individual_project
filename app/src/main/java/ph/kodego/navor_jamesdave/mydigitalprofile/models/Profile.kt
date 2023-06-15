package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ph.kodego.navor_jamesdave.mydigitalprofile.dao_models.DaoProfile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub

/**
 * Data Flow
 *  Home = ProfileDatas
 *      -> ProfileActivity = ProfileData
 *          -> Fragments = Profile
 *
 */
data class Profile(
    var profession: String = ""
): Account(), Parcelable{
    var profileID: String = ""

    fun setProfile(profile: Profile){
        profession = profile.profession
        profileID = profile.profileID
        setAccount(profile)
    }

    fun migrateProfile(
        summary: ProfessionalSummary,
        careers: ArrayList<Career>,
        skills: ArrayList<SkillMainCategory>,
        educations: ArrayList<Education>
    ): ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile{
        val profile =  ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile(
            "",uID,profession, false, summary.profileSummary
        )
        val migratedCareers = with(careers){
            val list: ArrayList<ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Career> = ArrayList()
            forEach {
                val newCareer = ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Career(
                        it.companyName,
                        it.position,
                        it.employmentStart,
                        it.employmentEnd,
                        it.jobDescription,
                        it.contactInformation?.emailAddress?.email ?: ""
                    )
                it.contactInformation?.address?.let {  address ->
                    newCareer.address = Address(
                        address.streetAddress,
                        address.subdivision,
                        address.cityOrMunicipality,
                        address.province,
                        address.zipCode,
                        address.country
                    )
                }
                it.contactInformation?.contactNumber?.let { contactNumber ->
                    newCareer.contactNumber = ContactNumber(
                        contactNumber.areaCode,
                        contactNumber.contact
                    )
                }

                list.add(newCareer)
            }
            list
        }
        profile.careers.addAll(migratedCareers)
        val migratedSkills = with(skills){
            val mainSkills: ArrayList<SkillsMain> = ArrayList()
            forEach {
                val mainSkill = SkillsMain(it.categoryMain)
                val subSkills = with(it.subCategories){
                    val subSkills: ArrayList<SkillsSub> = ArrayList()
                    forEach {
                        val subSkill = SkillsSub(it.categorySub)
                        it.skills.forEach {
                            subSkill.skills.add(it.skill)
                        }
                        subSkills.add(subSkill)
                    }

                    subSkills
                }
                mainSkill.subCategories.addAll(subSkills)
                mainSkills.add(mainSkill)
            }
            mainSkills
        }
        profile.skills.addAll(migratedSkills)
        val migratedEducations = with(educations){
            val educations: ArrayList<ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education> = ArrayList()
            forEach {
                val education = ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education(
                    it.schoolName,
                    it.degree,
                    it.fieldOfStudy,
                    it.dateEnrolled,
                    it.dateGraduated,
                )
                it.contactInformation?.let {
                    it.address?.let {
                        education.address = Address(
                            it.streetAddress,
                            it.subdivision,
                            it.cityOrMunicipality,
                            it.province,
                            it.zipCode,
                            it.country
                        )
                    }
                    it.emailAddress?.let {
                        education.emailAddress = it.email
                    }
                    it.website?.let {
                        education.website = it.website
                    }
                    it.contactNumber?.let {
                        education.contactNumber.areaCode = it.areaCode
                        education.contactNumber.contact = it.contact
                    }
                }

                educations.add(education)
            }

            educations
        }
        profile.educations.addAll(migratedEducations)

        return profile
    }

    override fun setParcel(parcel: Parcel) {
        super.setParcel(parcel)
        profession = parcel.readString()!!
        profileID = parcel.readString()!!
    }
    constructor(parcel: Parcel) : this() {
        this.setParcel(parcel)
    }

    constructor(profileID: String, profession: String = ""): this(profession){
        this.profileID = profileID
    }
    fun exportDaoProfile(): DaoProfile{
        return DaoProfile(uID, profileID, profession)
    }
    fun importDaoProfile(daoProfile: DaoProfile){
        uID = daoProfile.uid
        profileID = daoProfile.profileID
        profession = daoProfile.profession
    }
    constructor(daoProfile: DaoProfile): this(daoProfile.profession){
        uID = daoProfile.uid
        profileID = daoProfile.profileID
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(profession)
        parcel.writeString(profileID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}

@Parcelize
data class ProfessionalSummary(
    val profileID: String = ""
): Parcelable{
    var id: String = ""
    var profileSummary: String = ""

    fun setSummary(professionalSummary: ProfessionalSummary){
        id = professionalSummary.id
        profileSummary = professionalSummary.profileSummary
    }
}

data class ProfileCareer(
    val profile: Profile?,
    val careers: ArrayList<Career>
)

data class UserSkills(
    val profile: Profile,
    val skills: ArrayList<SkillMainCategory> = ArrayList()
)