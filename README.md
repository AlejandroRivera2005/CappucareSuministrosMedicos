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

El flujo de la aplicación siempre es este, la pantalla observa un StateFlow de su ViewModel, y al realizar cambios, View (la pantalla involucrada) llama a ViewModel, ViewModel aplica la lógica de negocio, y en base a esta, llama a los repositorios de Model, y finalmente la base de datos de Room genera los cambios, siguiendo este mismo flujo de manera inversa. Este flujo de funcionamiento garantiza que las responsabilidades dentro de la aplicación estén repartidas, siguiendo el principio de modularidad.

## Proceso de arranque de la aplicación
Cada vez que se ejecuta la aplicación, Android crea CappucareApp, Hilt configura el contenedor de dependencias y MainActivity renderiza AppNavHost, la cual tiene como instrucción hacer que la pantalla de entrada sea el inicio de sesión (login).

Dentro de CappucareApp.kt, Hilt también inyecta los datos de demostración (SembradorDatos.kt). Estos datos tienen la función de ser meramente demostrativos, con el fin de mostrar las funcionalidades de nuestra aplicación.

También se ha cargado un usuario de ejemplo, para poder acceder a la aplicación por primera vez. Pero este se encuentra dentro de UsuarioRepository, en la función sembrarEncargadoDemo(). Estos son los datos para concretar el inicio de sesión:

- Correo Electrónico: encargado@cappucare.cl
- Contraseña: Cappucare123
