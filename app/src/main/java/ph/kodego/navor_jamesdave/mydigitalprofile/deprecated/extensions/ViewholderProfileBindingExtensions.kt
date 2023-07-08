package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.extensions

import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadProfile

fun ItemProfileBinding.bind(profile: Profile){
    profileUserName.text = profile.fullName()
    profession.text = profile.profession
    profilePicture.loadProfile(profile.image)
}