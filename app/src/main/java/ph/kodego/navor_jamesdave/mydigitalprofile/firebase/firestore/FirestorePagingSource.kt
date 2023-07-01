package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot

abstract class FirestorePagingSource<Model: Any>(
    private val getList: suspend (DocumentSnapshot?, Int) -> List<DocumentSnapshot>
): PagingSource<DocumentSnapshot, Model>() {
    private val prevKeys: ArrayList<DocumentSnapshot?> = ArrayList() //Not useful when the PagingSource is Invalidated
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
            val nextKey = documents.lastOrNull()
            LoadResult.Page(models, prevKey, nextKey)
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    /**
     * Gets the List of DocumentSnapshot using the Primary Constructor
     */
    protected open suspend fun getDocuments(
        offset: DocumentSnapshot?,
        limit: Int
    ): List<DocumentSnapshot>{
        return getList(offset, limit)
    }

    protected open suspend fun List<DocumentSnapshot>.toModels(): List<Model> = map { it.toObject() }

    protected abstract suspend fun DocumentSnapshot.toObject(): Model
}