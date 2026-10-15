import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cappucaresuministrosmedicos.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY id")
    fun getAllTasks(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getTaskById(id: Long): ProductoEntity?

    @Insert
    suspend fun insert(task: ProductoEntity)

    @Update
    suspend fun update(task: ProductoEntity)

    @Delete
    suspend fun delete(task: ProductoEntity)
}