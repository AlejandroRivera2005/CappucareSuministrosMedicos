package com.example.cappucaresuministrosmedicos.viewModel

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidadoresTest : StringSpec({
    "nombre exige contenido y al menos tres caracteres" {
        Validadores.nombre("") shouldBe "El nombre es obligatorio"
        Validadores.nombre(" Al ") shouldBe "Debe tener al menos 3 caracteres"
        Validadores.nombre("Ana") shouldBe null
    }

    "nombre rechaza dígitos" {
        Validadores.nombre("Ana2") shouldBe "No puede contener números"
    }

    "contraseña valida longitud, dígitos y letras" {
        Validadores.contrasena("") shouldBe "La contraseña es obligatoria"
        Validadores.contrasena("abc12") shouldBe "Debe tener al menos 8 caracteres"
        Validadores.contrasena("abcdefgh") shouldBe "Debe incluir al menos un número"
        Validadores.contrasena("12345678") shouldBe "Debe incluir al menos una letra"
        Validadores.contrasena("clave123") shouldBe null
    }

    "confirmación exige coincidencia" {
        Validadores.confirmarContrasena("clave123", "") shouldBe "Confirma tu contraseña"
        Validadores.confirmarContrasena("clave123", "clave321") shouldBe "Las contraseñas no coinciden"
        Validadores.confirmarContrasena("clave123", "clave123") shouldBe null
    }

    "código de producto acepta entre 3 y 15 caracteres alfanuméricos o guiones" {
        Validadores.codigoProducto("") shouldBe "El código es obligatorio"
        Validadores.codigoProducto("AB") shouldBe "Usa 3 a 15 letras, números o guiones"
        Validadores.codigoProducto("AB-123") shouldBe null
        Validadores.codigoProducto("AB_123") shouldBe "Usa 3 a 15 letras, números o guiones"
    }

    "entero valida formato y mínimo" {
        Validadores.entero("", "Stock") shouldBe "Stock es obligatorio"
        Validadores.entero("1.5", "Stock") shouldBe "Stock debe ser un número entero"
        Validadores.entero("-1", "Stock") shouldBe "Stock debe ser al menos 0"
        Validadores.entero("3", "Stock") shouldBe null
        Validadores.entero("2", "Stock", minimo = 3) shouldBe "Stock debe ser al menos 3"
    }

    "id de producto debe ser un entero positivo" {
        Validadores.idProducto("") shouldBe "El ID es obligatorio"
        Validadores.idProducto("abc") shouldBe "El ID debe ser un número entero"
        Validadores.idProducto("0") shouldBe "El ID debe ser mayor a 0"
        Validadores.idProducto(" 12 ") shouldBe null
    }

    "texto obligatorio identifica el campo" {
        Validadores.textoObligatorio("", "La categoría") shouldBe "La categoría es obligatorio"
        Validadores.textoObligatorio("  ", "El nombre") shouldBe "El nombre es obligatorio"
        Validadores.textoObligatorio("Guantes", "El nombre") shouldBe null
    }

    "movimiento impide cantidades no positivas y salidas sin stock" {
        Validadores.cantidadMovimiento(0, 10, true) shouldBe "La cantidad debe ser mayor a 0"
        Validadores.cantidadMovimiento(11, 10, true) shouldBe "No hay stock suficiente (disponible: 10)"
        Validadores.cantidadMovimiento(11, 10, false) shouldBe null
        Validadores.cantidadMovimiento(10, 10, true) shouldBe null
    }
})
