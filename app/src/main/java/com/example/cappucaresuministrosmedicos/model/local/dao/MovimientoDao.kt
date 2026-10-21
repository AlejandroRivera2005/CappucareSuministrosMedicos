package com.example.cappucaresuministrosmedicos.model.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Insert
    suspend fun insertar(movimiento: MovimientoEntity)

    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY fecha DESC")
    fun observarPorProducto(productoId: Long): Flow<List<MovimientoEntity>>

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC LIMIT :limite")
    fun observarRecientes(limite: Int): Flow<List<MovimientoEntity>>

    @Query("SELECT COUNT(*) FROM movimientos WHERE fecha >= :desde")
    fun contarDesde(desde: Long): Flow<Int>
}