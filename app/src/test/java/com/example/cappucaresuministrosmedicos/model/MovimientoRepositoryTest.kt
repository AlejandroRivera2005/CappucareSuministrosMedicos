package com.example.cappucaresuministrosmedicos.model

import androidx.room.Room
import com.example.cappucaresuministrosmedicos.model.local.AppDatabase
import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MovimientoRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: MovimientoRepository

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = MovimientoRepository(db, db.movimientoDao(), db.productoDao())
    }

    @After fun tearDown() {
        db.close()
    }

    private fun producto(stock: Int = 10) = ProductoEntity(
        id = 1, nombre = "Guantes", descripcion = "", categoria = "Protección",
        stockDisponible = stock, unidadMedida = "cajas", stockMinimo = 3,
        lugarAlmacenamiento = "A1"
    )

    @Test fun ingresoIncrementaStockYRegistraMovimiento() = runBlocking {
        db.productoDao().insertar(producto())
        val result = repository.registrar(
            1, TipoMovimiento.INGRESO, 5, MotivoMovimiento.RECEPCION, "Encargado"
        )
        assertTrue(result.isSuccess)
        assertEquals(15, result.getOrThrow().stockDisponible)
        val history = repository.observarPorProducto(1).first()
        assertEquals(1, history.size)
        assertEquals(TipoMovimiento.INGRESO, history.single().tipo)
        assertEquals(5, history.single().cantidad)
        assertEquals("Encargado", history.single().responsable)
    }

    @Test fun salidaDescuentaStockYRegistraMovimiento() = runBlocking {
        db.productoDao().insertar(producto(10))
        val result = repository.registrar(
            1, TipoMovimiento.SALIDA, 4, MotivoMovimiento.VENTA, "Operador"
        )
        assertTrue(result.isSuccess)
        assertEquals(6, result.getOrThrow().stockDisponible)
        assertEquals(1, repository.observarPorProducto(1).first().size)
    }

    @Test fun salidaMayorAlStockFallaSinModificarDatos() = runBlocking {
        db.productoDao().insertar(producto(3))
        val result = repository.registrar(
            1, TipoMovimiento.SALIDA, 4, MotivoMovimiento.MERMA, "Operador"
        )
        assertTrue(result.isFailure)
        assertEquals("No hay stock suficiente (disponible: 3)", result.exceptionOrNull()?.message)
        assertEquals(3, db.productoDao().obtenerPorId(1)?.stockDisponible)
        assertTrue(repository.observarPorProducto(1).first().isEmpty())
    }

    @Test fun productoInexistenteDevuelveFallo() = runBlocking {
        val result = repository.registrar(
            999, TipoMovimiento.INGRESO, 1, MotivoMovimiento.AJUSTE, "Operador"
        )
        assertTrue(result.isFailure)
        assertEquals("El producto no existe", result.exceptionOrNull()?.message)
    }

    @Test fun consultasDeleganAFlujosDelDao() = runBlocking {
        db.productoDao().insertar(producto())
        repository.registrar(1, TipoMovimiento.INGRESO, 2, MotivoMovimiento.RECEPCION, "A")
        repository.registrar(1, TipoMovimiento.SALIDA, 1, MotivoMovimiento.VENTA, "B")
        assertEquals(2, repository.observarRecientes().first().size)
        assertEquals(2, repository.observarRecientes(2).first().size)
        assertEquals(2, repository.contarDesde(0L).first())
        assertEquals(0, repository.contarDesde(Long.MAX_VALUE).first())
    }
}
