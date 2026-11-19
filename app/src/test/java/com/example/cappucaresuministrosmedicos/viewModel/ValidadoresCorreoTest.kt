package com.example.cappucaresuministrosmedicos.viewModel

import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ValidadoresCorreoTest {
    @Test fun correoObligatorioInvalidoYValido() {
        Validadores.correo("") shouldBe "El correo es obligatorio"
        Validadores.correo("no-es-correo") shouldBe "Ingresa un correo válido"
        Validadores.correo(" ana@example.com ") shouldBe null
    }
}
