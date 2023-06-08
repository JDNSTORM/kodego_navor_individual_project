package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.storage

import android.net.Uri

interface FirebaseStorageDAO {
    suspend fun uploadDocument(uri: Uri): Uri?

}