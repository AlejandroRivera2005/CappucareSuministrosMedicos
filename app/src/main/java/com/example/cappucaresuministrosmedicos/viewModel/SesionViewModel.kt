package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SesionViewModel @Inject constructor(
    sesion: SesionUsuario
) : ViewModel() {


    val esEncargado: StateFlow<Boolean> = sesion.perfilActual
        .map { it == PerfilUsuario.ENCARGADO }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}