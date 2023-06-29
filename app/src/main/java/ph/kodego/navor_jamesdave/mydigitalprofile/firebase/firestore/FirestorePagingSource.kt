package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject

abstract class FirestorePagingSource<Model: Any>(
    private val getList: suspend (DocumentSnapshot?, Int) -> List<DocumentSnapshot>
): PagingSource<DocumentSnapshot, Model>() {
    private val prevKeys: ArrayList<DocumentSnapshot?> = ArrayList()
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Model>): DocumentSnapshot? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
                ?: state.closestPageToPosition(it)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Model> {
        val position = params.key
        return try {
            val documents = getDocuments(position, params.loadSize)
            val models = documents.toModels()
            val prevKey = prevKeys.lastOrNull()
            prevKeys.add(position)
            val nextKey = if (documents.isEmpty()){
                null
            }else{
                documents.last()
            }
            LoadResult.Page(models, prevKey, nextKey)
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    protected open suspend fun getDocuments(
        offset: DocumentSnapshot?,
        limit: Int
    ): List<DocumentSnapshot>{
        return getList(offset, limit)
    }

    private suspend fun List<DocumentSnapshot>.toModels(): List<Model> = map { it.toObject() }

    protected abstract suspend fun DocumentSnapshot.toObject(): Model
}