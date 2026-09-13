# TravelGo 🧳✈️

App móvil para una agencia de viajes que permite gestionar un catálogo de destinos turísticos, con autenticación de usuarios y un CRUD completo conectado a Firebase.

**Segundo Desafío Práctico — Desarrollo de Software para Móviles (DSM)**
**Universidad Don Bosco**

## 👤 Alumno

**Nombre:** [Paola Matilde Orellana Castillo OC250609]

## 🎥 Video de defensa

[https://youtu.be/n-TrK3vPi2E]

## 📱 Descripción del proyecto

TravelGo permite a los agentes de viajes:

- Registrarse e iniciar sesión de forma segura (Firebase Authentication).
- Crear, ver, editar y eliminar destinos turísticos (Firebase Firestore).
- Subir y visualizar fotos de cada destino (Firebase Storage + Glide).
- Validar todos los campos del formulario antes de guardar (nombre, país, precio mayor a 0, descripción mínima de 20 caracteres, imagen obligatoria).

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **UI:** XML + ConstraintLayout + Material Components + View Binding
- **Backend:** Firebase Authentication, Cloud Firestore, Cloud Storage
- **Carga de imágenes:** Glide
- **Coroutines:** para llamadas asíncronas a Firebase
- **Arquitectura:** separación por paquetes (`model`, `repository`, `ui`, `adapter`)

## 📂 Estructura del proyecto
app/src/main/java/com/udb/travelgo/
├── model/ → Clase Destination (modelo de datos)
├── repository/ → DestinationRepository (toda la lógica de Firebase)
├── ui/ → LoginActivity, RegisterActivity, CatalogActivity, AddEditDestinationActivity
└── adapter/ → DestinationAdapter (RecyclerView)

## ✅ Funcionalidades

1. **Login / Registro** — Autenticación con Firebase Auth (email/contraseña).
2. **Catálogo (Read)** — RecyclerView con CardView mostrando foto, nombre, país, precio y descripción de cada destino, filtrado por usuario y en tiempo real.
3. **Crear destino (Create)** — Formulario con Spinner de país, selector de imagen de galería, y validaciones completas.
4. **Editar destino (Update)** — Permite modificar cualquier dato, incluyendo la imagen.
5. **Eliminar destino (Delete)** — Con diálogo de confirmación previo.

## 📦 APK

El APK de la última versión funcional se encuentra en la carpeta [`/apk/TravelGo.apk`](./apk/TravelGo.apk) de este repositorio.

## 🔥 Firebase

El proyecto usa Firebase (Auth, Firestore y Storage) bajo el proyecto `travelgo-dsm`. Las reglas de seguridad exigen que el usuario esté autenticado (`request.auth != null`) para leer o escribir datos e imágenes.

## 🎨 Diseño

- Ícono y nombre personalizados, diseñados con Android Asset Studio.
- Paleta de colores propia (turquesa + coral + dorado).
- Tipografía personalizada (Fredoka, de Google Fonts).
- Todos los textos centralizados en `strings.xml` (en inglés).
