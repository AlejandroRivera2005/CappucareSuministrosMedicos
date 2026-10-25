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
| Interfaces para el usuario | view | Pantallas con composables, con navegación y componentes reutilizables |
| Inyección Hilt | di | módulo Hilt que construye los DAOs y la base de datos |
