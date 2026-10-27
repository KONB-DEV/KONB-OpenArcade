package off.kys.openarcade.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import off.kys.openarcade.core.data.local.dao.GameDao
import off.kys.openarcade.core.domain.model.GameEntry

@Database(
    entities = [GameEntry::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
