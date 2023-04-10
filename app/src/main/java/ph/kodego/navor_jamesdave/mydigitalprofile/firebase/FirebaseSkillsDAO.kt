package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

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

interface FirebaseSkillsDAO {
    suspend fun addSkill(skill: Skill): Boolean
    suspend fun getSkill(skillID: String): Skill
    suspend fun getSkills(subCategoryID: String): ArrayList<Skill>
    suspend fun updateSkill(skill: Skill, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteSkill(skill: Skill): Boolean
}

class FirebaseSkillsMainCategoryDAOImpl(): FirebaseSkillsMainCategoryDAO{
    private val fireStore = FirebaseFirestore.getInstance()
    private val collection = FirebaseCollections.SkillsMainCategory
    private val reference = fireStore.collection(collection)

    override suspend fun addMainCategory(mainCategory: SkillMainCategory): Boolean {
//        TODO("Not yet implemented")
        val document = reference.document()
        mainCategory.mainCategoryID = document.id
        val task = document.set(mainCategory, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("AddMainCategory", "Successful")
            true
        }else{
            Log.e("AddMainCategory", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getMainCategory(mainCategoryID: String): SkillMainCategory {
        TODO("Not yet implemented")
    }

    override suspend fun getMainCategories(profileID: String): ArrayList<SkillMainCategory> {
//        TODO("Not yet implemented")
        val mainCategories: ArrayList<SkillMainCategory> = ArrayList()
        val task = reference
            .whereEqualTo("profileID", profileID)
            .get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("MainCategories", task.result.documents.toString())
            task.result.documents.forEach { snapshot ->
                Log.i("Main Data", snapshot.data.toString())
                val mainCategory = snapshot.toObject(SkillMainCategory::class.java)!!
                Log.d("MainCategory", mainCategory.toString())
                mainCategories.add(mainCategory)
            }
        }else{
            Log.e("Get MainCategories", task.exception?.message.toString())
        }
        return mainCategories
    }

    override suspend fun updateMainCategory(mainCategory: SkillMainCategory, fields: HashMap<String, Any?>): Boolean {
        val task = reference
            .document(mainCategory.mainCategoryID)
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteMainCategory(mainCategory: SkillMainCategory): Boolean {
        val task = reference
            .document(mainCategory.mainCategoryID)
            .delete()
        task.await()
        return task.isSuccessful
    }
}

class FirebaseSkillsSubCategoryDAOImpl(mainCategory: SkillMainCategory): FirebaseSkillsSubCategoryDAO{
    private val fireStore = FirebaseFirestore.getInstance()
    private val collection = FirebaseCollections.SkillsSubCategory
    private val reference = fireStore.collection(FirebaseCollections.SkillsMainCategory).document(mainCategory.mainCategoryID).collection(collection)

    override suspend fun addSubCategory(subCategory: SkillSubCategory): Boolean {
//        TODO("Not yet implemented")
        val document = reference
            .document()
        subCategory.subCategoryID = document.id
        val task = document.set(subCategory, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("AddSubCategory", "Successful")
            true
        }else{
            Log.e("AddSubCategory", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getSubCategory(subCategoryID: String): SkillSubCategory {
        TODO("Not yet implemented")
    }

    override suspend fun getSubCategories(mainCategoryID: String): ArrayList<SkillSubCategory> {
//        TODO("Not yet implemented")
        val subCategories: ArrayList<SkillSubCategory> = ArrayList()
        val task = reference.get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("SubCategories", task.result.documents.toString())
            task.result.documents.forEach { snapshot ->
                Log.i("Sub Data", snapshot.data.toString())
                val subCategory = snapshot.toObject(SkillSubCategory::class.java)!!
                Log.d("SubCategory", subCategory.toString())
                subCategories.add(subCategory)
            }
        }else{
            Log.e("Get SubCategories", task.exception?.message.toString())
        }
        return subCategories
    }

    override suspend fun updateSubCategory(
        subCategory: SkillSubCategory,
        fields: HashMap<String, Any?>
    ): Boolean {
//        TODO("Not yet implemented")
        val task = reference
            .document(subCategory.subCategoryID)
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteSubCategory(subCategory: SkillSubCategory): Boolean {
//        TODO("Not yet implemented")
        val task = reference
            .document(subCategory.subCategoryID)
            .delete()
        task.await()
        return task.isSuccessful
    }

}

class FirebaseSkillsDAOImpl(subCategory: SkillSubCategory): FirebaseSkillsDAO{
    private val fireStore = FirebaseFirestore.getInstance()
    private val collection = FirebaseCollections.Skills
    private val reference = fireStore.collection(FirebaseCollections.SkillsMainCategory)
        .document(subCategory.mainCategoryID)
        .collection(collection)
        .document(subCategory.subCategoryID)
        .collection(collection)

    override suspend fun addSkill(skill: Skill): Boolean {
//        TODO("Not yet implemented")
        val document = reference
            .document()
        skill.skillID = document.id
        val task = document.set(skill, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("AddSkill", "Successful")
            true
        }else{
            Log.e("AddSkill", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getSkill(skillID: String): Skill {
        TODO("Not yet implemented")
    }

    override suspend fun getSkills(subCategoryID: String): ArrayList<Skill> {
//        TODO("Not yet implemented")
        val skills: ArrayList<Skill> = ArrayList()
        val task = reference.get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("Skills", task.result.documents.toString())
            task.result.documents.forEach { snapshot ->
                Log.i("Skill Data", snapshot.data.toString())
                val skill = snapshot.toObject(Skill::class.java)!!
                Log.d("Skill", skill.toString())
                skills.add(skill)
            }
        }else{
            Log.e("Get Skills", task.exception?.message.toString())
        }
        return skills
    }

    override suspend fun updateSkill(skill: Skill, fields: HashMap<String, Any?>): Boolean {
//        TODO("Not yet implemented")
        val task = reference
            .document(skill.skillID)
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteSkill(skill: Skill): Boolean {
//        TODO("Not yet implemented")
        val task = reference
            .document(skill.skillID)
            .delete()
        task.await()
        return task.isSuccessful
    }
}