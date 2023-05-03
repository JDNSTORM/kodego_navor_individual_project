package ph.kodego.navor_jamesdave.mydigitalprofile.dao_models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account

@Entity(tableName = "profile-table") //TODO
@Parcelize
data class DaoProfile(
    @ColumnInfo(name = "uID")
    val uid: String = "",
    @PrimaryKey(false)
    val profileID: String = "",
    var profession: String = ""
): Parcelable