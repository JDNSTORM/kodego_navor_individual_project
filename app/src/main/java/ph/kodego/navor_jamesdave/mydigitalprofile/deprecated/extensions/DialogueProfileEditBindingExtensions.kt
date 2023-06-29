package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.extensions

import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface

fun DialogProfileEditBinding.bind(profile: Profile, summary: ProfessionalSummary){
    profession.setText(profile.profession)
    profileSummary.setText(summary.profileSummary)
    editButtons.updateInterface()
}