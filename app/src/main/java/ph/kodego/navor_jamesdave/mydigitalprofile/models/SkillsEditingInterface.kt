package ph.kodego.navor_jamesdave.mydigitalprofile.models

import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills.SkillMainEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills.SkillSubEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutSkillEventsBinding

data class SkillsEditingInterface(
    val editBinding: LayoutSkillEventsBinding,
    val mainSkillEditDialog: SkillMainEditDialog<*>,
    val subSkillEditDialog: SkillSubEditDialog<*>
)
