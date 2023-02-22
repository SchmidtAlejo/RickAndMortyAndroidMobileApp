# RickAndMortyAndroidMobileApp
Esta Aplicación Mobile desarrollada en Kotlin fue elaborada con el proposito de demostrar mis principales conocimientos que tengo sobre este lenguaje.  Fue desarrollada usando la API publica de The Rick and Morty.

### Alcance:
- Se podra iniciar sesión y mantener su estado.
- Se podran ver los diferentes personajes de "The Rick and Morty".
- Se podran ver las descripciones de los personajes de forma individual.
- Se podran agregar y eliminar a los personajes de la lista de "Favoritos".
- Se puede alternar la aplicación de Modo Dia a Modo Noche en el apartado de Ajustes.

### Implementaciones:

- Se Mantiene estado de sesión, de Favoritos, y Modo Noche mediante Local Storage usando **DataStore**.
- Se muestra la lista de personajes usando **RecyclerView**.
- Las images de los personajes se muestran usando la librería de **glide**.
- Las llamadas a la API se realizan usando las librerías de **Retrofit 2** y **Coroutines** para las funciones asíncronas.