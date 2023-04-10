package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

interface FirebaseSkillsDAO {
    suspend fun addSkill(skill: Skill): Boolean
    suspend fun getSkill(skillID: String): Skill
    suspend fun getSkills(subCategoryID: String): ArrayList<Skill>
    suspend fun updateSkill(skill: Skill, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteSkill(skill: Skill): Boolean
}

interface FirebaseSkillsMainCategoryDAO {
    suspend fun addMainCategory(mainCategory: SkillMainCategory): Boolean
    suspend fun getMainCategory(mainCategoryID: String): SkillMainCategory
    suspend fun getMainCategories(profileID: String): ArrayList<SkillMainCategory>
    suspend fun updateMainCategory(mainCategory: SkillMainCategory, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteMainCategory(mainCategory: SkillMainCategory): Boolean
}

interface FirebaseSkillsSubCategoryDAO {
    suspend fun addSubCategory(subCategory: SkillSubCategory): Boolean
    suspend fun getSubCategory(subCategoryID: String): SkillSubCategory
    suspend fun getSubCategories(mainCategoryID: String): ArrayList<SkillSubCategory>
    suspend fun updateSubCategory(subCategory: SkillSubCategory, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteSubCategory(subCategory: SkillSubCategory): Boolean
}