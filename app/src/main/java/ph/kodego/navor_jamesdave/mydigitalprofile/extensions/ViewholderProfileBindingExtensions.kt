package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule

fun ViewholderProfileBinding.bind(profile: Profile){
    profileUserName.text = profile.fullName()
    profession.text = profile.profession
    GlideModule().loadProfilePhoto(profilePicture, profile.image)
}