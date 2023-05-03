package ph.kodego.navor_jamesdave.mydigitalprofile.dao

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account

@Dao
interface AccountsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account): Int

    @Delete
    suspend fun delete(account: Account): Int

    @Query("DELETE FROM `accounts-table`")
    suspend fun clean()

    @Query("SELECT * FROM `accounts-table` WHERE uID = :uID")
    suspend fun getAccount(uID: String): Account?
}

@Database(entities = [Account::class], version = 0)
abstract class AccountsDatabase: RoomDatabase(){
    abstract fun accountDAO(): AccountsDAO

    companion object{
        @Volatile
        private var INSTANCE: AccountsDatabase? = null

        fun getInstance(context: Context): AccountsDatabase{
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AccountsDatabase::class.java,
                        "accounts-table"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}