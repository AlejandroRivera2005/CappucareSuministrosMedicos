## Cappucare Suministros médicos

## Grupo 7
Integrantes: Félix Rojas y Alejandro Rivera.

## Sinapsis
Esta aplicación para android fue realizada como encargo para la empresa de Cappucare, tiene el propósito general de facilitar las labores de gestión de bodega para esta misma, con el contexto especial de ser una bodega de insumos médicos.

Nuestra aplicación fue hecha en Android Studio, utiliza componentes de Jetpack Compose y Material3, inyección de dependencias de Hilt y manejo de datos y persistencia local a través de Room.

El patrón arquitectónico de esta aplicación es Model - View - ViewModel (MVVM), dado a que es el estándar para aplicaciones de android.


## Arquitectura de software
Como se mencionó anteriormente, nuestra aplicación sigue el patrón de diseño MVVM, dentro de los directorios del proyecto, se ve reflejado de la siguiente forma: 


| Capa | Carpeta | Rol |
| :--- | :--- | :--- |
| Datos | model | Base de datos local, DAOs, repositorios y datos provisorios para la demostración |
| Estado y lógica de las pantallas | viewmodel | Estado de la pantalla, reglas de negocio y validaciones |
| Interfaces para el usuario | view | Pantallas con composables, navegación y componentes reutilizables |
| Inyección Hilt | di | módulo Hilt que construye los DAOs y la base de datos |


El orden en el que está realizado el código fuente para seguir este patrón es este:

├── CappucareApp.kt            Application: arranca Hilt y siembra productos demo
├── MainActivity.kt            Única Activity: muestra AppNavHost
├── di/
│   └── DatabaseModule.kt      Provee AppDatabase y los 3 DAOs
├── model/
│   ├── SembradorDatos.kt      Productos y movimientos de ejemplo
│   ├── SesionUsuario.kt       Usuario y perfil de la sesión actual (en memoria)
│   ├── NotificadorStock.kt    Notificación de stock bajo
│   ├── ProductoRepository.kt
│   ├── MovimientoRepository.kt
│   ├── UsuarioRepository.kt
│   └── local/
│       ├── AppDataBase.kt     Base Room (clase AppDatabase)
│       ├── dao/               ProductoDao, MovimientoDao, UsuarioDao
│       └── entity/            ProductoEntity, MovimientoEntity, UsuarioEntity
├── viewModel/                 Login, Registro, Home, Sesion, Alertas, Historial,
│                              HistorialProducto, Movimiento, Usuarios, ProductoLista,
│                              ProductoDetalle, ProductoForm, Validadores, helpers
└── view/
    ├── navigation/            AppNavHost, BarraNavegacion
    ├── screens/               10 pantallas (Login ... Usuarios)
    ├── components/            CampoFormulario, EtiquetaEstado, FilaMovimiento, HistorialProducto
    └── theme/                 Color, Type, Theme
