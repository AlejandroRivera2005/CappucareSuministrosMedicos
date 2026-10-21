package com.example.cappucaresuministrosmedicos.viewModel

object Validadores {

    // ---------- Usuario ----------

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        valor.any { it.isDigit() } -> "No puede contener números"
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "El correo es obligatorio"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(valor.trim()).matches() ->
            "Ingresa un correo válido"
        else -> null
    }

    fun contrasena(valor: String): String? = when {
        valor.isBlank() -> "La contraseña es obligatoria"
        valor.length < 8 -> "Debe tener al menos 8 caracteres"
        valor.none { it.isDigit() } -> "Debe incluir al menos un número"
        valor.none { it.isLetter() } -> "Debe incluir al menos una letra"
        else -> null
    }

    fun confirmarContrasena(contrasena: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Confirma tu contraseña"
        contrasena != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }

    // ---------- Producto ----------

    fun codigoProducto(valor: String): String? = when {
        valor.isBlank() -> "El código es obligatorio"
        !valor.matches(Regex("^[A-Za-z0-9-]{3,15}$")) ->
            "Usa 3 a 15 letras, números o guiones"
        else -> null
    }

    fun textoObligatorio(valor: String, campo: String): String? =
        if (valor.isBlank()) "$campo es obligatorio" else null

    fun entero(valor: String, campo: String, minimo: Int = 0): String? {
        val numero = valor.toIntOrNull()
        return when {
            valor.isBlank() -> "$campo es obligatorio"
            numero == null -> "$campo debe ser un número entero"
            numero < minimo -> "$campo debe ser al menos $minimo"
            else -> null
        }
    }

    // ---------- Movimiento ----------

    fun cantidadMovimiento(cantidad: Int, stockActual: Int, esSalida: Boolean): String? = when {
        cantidad <= 0 -> "La cantidad debe ser mayor a 0"
        esSalida && cantidad > stockActual -> "No hay stock suficiente (disponible: $stockActual)"
        else -> null
    }
}