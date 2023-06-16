package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule

fun ItemProfileBinding.bind(profile: Profile){
    profileUserName.text = profile.fullName()
    profession.text = profile.profession
    GlideModule().loadProfilePhoto(profilePicture, profile.image)
}