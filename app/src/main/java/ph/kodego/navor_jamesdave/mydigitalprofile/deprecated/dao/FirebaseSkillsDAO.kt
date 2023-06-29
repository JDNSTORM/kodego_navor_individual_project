package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Skill
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.SkillSubCategory

interface FirebaseSkillsMainCategoryDAO {
    suspend fun addMainCategory(mainCategory: SkillMainCategory): Boolean
    suspend fun getMainCategory(mainCategoryID: String): SkillMainCategory
    suspend fun getMainCategories(): ArrayList<SkillMainCategory>
    suspend fun updateMainCategory(mainCategory: SkillMainCategory, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteMainCategory(mainCategory: SkillMainCategory): Boolean
}

interface FirebaseSkillsSubCategoryDAO {
    suspend fun addSubCategory(subCategory: SkillSubCategory): Boolean
    suspend fun getSubCategory(subCategoryID: String): SkillSubCategory
    suspend fun getSubCategories(): ArrayList<SkillSubCategory>
    suspend fun updateSubCategory(subCategory: SkillSubCategory, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteSubCategory(subCategory: SkillSubCategory): Boolean
}

interface FirebaseSkillsDAO {
    suspend fun addSkill(skill: Skill): Boolean
    suspend fun getSkill(skillID: String): Skill
    suspend fun getSkills(): ArrayList<Skill>
    suspend fun updateSkill(skill: Skill, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteSkill(skill: Skill): Boolean
}

class FirebaseSkillsMainCategoryDAOImpl(private val profileID: String):
    FirebaseSkillsMainCategoryDAO {
    private val fireStore = FirebaseFirestore.getInstance()
    private val collection = FirebaseCollections.SkillsMainCategory
    private val reference = fireStore.collection(collection)

    override suspend fun addMainCategory(mainCategory: SkillMainCategory): Boolean {
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

    override suspend fun getMainCategories(): ArrayList<SkillMainCategory> {
        val mainCategories: ArrayList<SkillMainCategory> = ArrayList()
        val task = reference
            .whereEqualTo("profileID", profileID)
            .get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("MainCategories", task.result.documents.toString())
            task.result.documents.forEach { document ->
                Log.i("Main Data", document.data.toString())
                val mainCategory = document.toObject(SkillMainCategory::class.java)!!
                mainCategory.subCategories.addAll(FirebaseSkillsSubCategoryDAOImpl(mainCategory).getSubCategories())
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

class FirebaseSkillsSubCategoryDAOImpl(mainCategory: SkillMainCategory):
    FirebaseSkillsSubCategoryDAO {
    private val fireStore = FirebaseFirestore.getInstance()
    private val collection = FirebaseCollections.SkillsSubCategory
    private val reference = fireStore.collection(FirebaseCollections.SkillsMainCategory).document(mainCategory.mainCategoryID).collection(collection)

    override suspend fun addSubCategory(subCategory: SkillSubCategory): Boolean {
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

    override suspend fun getSubCategories(): ArrayList<SkillSubCategory> {
        val subCategories: ArrayList<SkillSubCategory> = ArrayList()
        val task = reference.get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("SubCategories", task.result.documents.toString())
            task.result.documents.forEach { document ->
                Log.i("Sub Data", document.data.toString())
                val subCategory = document.toObject(SkillSubCategory::class.java)!!
                subCategory.skills.addAll(FirebaseSkillsDAOImpl(subCategory).getSkills())
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
        val task = reference
            .document(subCategory.subCategoryID)
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteSubCategory(subCategory: SkillSubCategory): Boolean {
        val task = reference
            .document(subCategory.subCategoryID)
            .delete()
        task.await()
        return task.isSuccessful
    }

}

class FirebaseSkillsDAOImpl(subCategory: SkillSubCategory): FirebaseSkillsDAO {
    private val fireStore = FirebaseFirestore.getInstance()
    private val collection = FirebaseCollections.Skills
    private val reference = fireStore.collection(FirebaseCollections.SkillsMainCategory)
        .document(subCategory.mainCategoryID)
        .collection(FirebaseCollections.SkillsSubCategory)
        .document(subCategory.subCategoryID)
        .collection(collection)

    override suspend fun addSkill(skill: Skill): Boolean {
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

    override suspend fun getSkills(): ArrayList<Skill> {
        val skills: ArrayList<Skill> = ArrayList()
        val task = reference.get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("Skills", task.result.documents.toString())
            task.result.documents.forEach { document ->
                Log.i("Skill Data", document.data.toString())
                val skill = document.toObject(Skill::class.java)!!
                Log.d("Skill", skill.toString())
                skills.add(skill)
            }
        }else{
            Log.e("Get Skills", task.exception?.message.toString())
        }
        return skills
    }

    override suspend fun updateSkill(skill: Skill, fields: HashMap<String, Any?>): Boolean {
        val task = reference
            .document(skill.skillID)
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteSkill(skill: Skill): Boolean {
        val task = reference
            .document(skill.skillID)
            .delete()
        task.await()
        return task.isSuccessful
    }
}