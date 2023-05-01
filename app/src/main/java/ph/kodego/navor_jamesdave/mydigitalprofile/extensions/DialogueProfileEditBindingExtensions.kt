package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

fun DialogueProfileEditBinding.bind(profile: Profile, summary: ProfessionalSummary){
    profession.setText(profile.profession)
    profileSummary.setText(summary.profileSummary)
    editButtons.updateInterface()
}