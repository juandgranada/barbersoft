# Arquitectura del frontend — BarberSoft

## Propósito

Este frontend es una aplicación React construida con Vite. Se encarga de:

- Mostrar el flujo público de reserva de citas.
- Consultar barberos, servicios y disponibilidad.
- Crear reservas públicas.
- Permitir que un administrador gestione barberos, servicios, horarios, excepciones, festivos y reservas.
- Gestionar el acceso administrativo mediante JWT.

El backend consumido es una API REST de Spring Boot ubicada normalmente en:

```text
http://localhost:8080
```

El frontend se ejecuta normalmente en:

```text
http://localhost:5173
```

---

## Tecnologías

- React 19.
- React Router DOM 7.
- Axios.
- Vite 8.
- JavaScript ES modules.
- Lucide React para iconografía.
- CSS global personalizado.
- LocalStorage para conservar la sesión JWT.

---

## Arquitectura general

La aplicación está organizada por capas y dominios:

```text
src/
├── api/
├── components/
│   ├── layout/
│   └── ui/
├── context/
├── hooks/
├── pages/
│   ├── admin/
│   └── public/
├── routes/
├── utils/
├── App.jsx
├── main.jsx
└── index.css
```

### Flujo de datos

```text
Página
  → módulo API
  → cliente Axios
  → backend REST
  → respuesta Axios
  → estado React
  → componente visual
```

Las páginas no contienen directamente URLs HTTP ni configuración global de Axios. Toda comunicación con el backend se realiza mediante `src/api`.

---

# Módulo de arranque

## `main.jsx`

Es el punto de entrada de React.

Responsabilidades:

- Crear el árbol de React.
- Activar `StrictMode`.
- Importar los estilos globales.
- Montar `App.jsx` dentro del elemento `#root`.

## `App.jsx`

Responsabilidades:

- Instalar `AuthProvider`.
- Renderizar `AppRouter`.

No contiene lógica visual propia ni configuración de rutas.

## `index.css`

Contiene:

- Tokens visuales principales.
- Variables de dorado y carbóno.
- Estilos globales.
- Componentes visuales reutilizables.
- Layout administrativo.
- Diseño público de reservas.
- Reglas responsive.

---

# Contexto de autenticación

## `src/context/AuthContext.jsx`

Componente global que administra la sesión administrativa.

Estado disponible:

- `user`: información del usuario autenticado.
- `isAuthenticated`: indica si existe una sesión visible.
- `login`: inicia sesión y guarda el JWT.
- `logout`: elimina la sesión.

### Persistencia

Guarda en `localStorage`:

- `barbersoft_token`: JWT.
- `barbersoft_user`: datos básicos devueltos por el backend.

### Uso

```jsx
const { user, login, logout, isAuthenticated } = useAuth()
```

Este contexto permite compartir la autenticación sin pasar propiedades entre muchos componentes.

---

# Capa API

## `src/api/client.js`

Cliente Axios global.

Responsabilidades:

- Configurar la URL base del backend.
- Leer `VITE_API_URL`.
- Agregar `Authorization: Bearer <token>` a rutas protegidas.
- Eliminar JWT al realizar solicitudes públicas.
- Eliminar la sesión ante respuestas `401` protegidas.
- Emitir `barbersoft:unauthorized` cuando el backend rechaza un token administrativo.

Configuración predeterminada:

```text
VITE_API_URL=http://localhost:8080
```

### Rutas públicas

El cliente no adjunta JWT a:

- `POST /api/disponibilidad`.
- `POST /api/reservas`.
- `POST /api/auth/login`.
- `GET /api/barberos/activos`.
- `GET /api/servicios/activos`.

---

## `src/api/authApi.js`

Contiene el contrato de autenticación.

Operaciones:

- `login(credentials)`:
  `POST /api/auth/login`.

---

## `src/api/barberosApi.js`

Operaciones relacionadas con barberos:

- Obtener todos.
- Obtener activos.
- Obtener uno por ID.
- Crear.
- Actualizar.
- Activar o desactivar.

Incluye las rutas utilizadas por el panel administrativo y la reserva pública.

---

## `src/api/serviciosApi.js`

Operaciones relacionadas con servicios:

- Obtener todos.
- Obtener servicios activos.
- Crear.
- Actualizar.
- Activar o desactivar.
- Asignar un servicio a un barbero.
- Consultar servicios asignados a un barbero.
- Desasignar un servicio de un barbero.

Los tres últimos recursos utilizan:

```text
/api/assignments
```

---

## `src/api/reservasApi.js`

Operaciones relacionadas con reservas:

- Consultar disponibilidad.
- Crear reserva pública.
- Listar reservas administrativas.
- Obtener una reserva.
- Cambiar estado.

---

## `src/api/horariosApi.js`

Operaciones relacionadas con:

- Horarios semanales de barberos.
- Activación o desactivación de horarios.
- Excepciones de horario.
- Activación o desactivación de excepciones.

---

## `src/api/festivosApi.js`

Operaciones relacionadas con:

- Listado de festivos.
- Consulta por año.
- Creación.
- Actualización.
- Activación o desactivación.

---

# Rutas

## `src/routes/AppRouter.jsx`

Contiene todas las rutas públicas y administrativas.

### Rutas públicas

| Ruta | Página |
|---|---|
| `/` | Reserva pública |
| `/admin/login` | Login administrativo |

### Rutas protegidas

| Ruta | Página |
|---|---|
| `/admin` | Dashboard |
| `/admin/reservas` | Reservas |
| `/admin/barberos` | Barberos y asignación de servicios |
| `/admin/servicios` | Servicios |
| `/admin/horarios` | Horarios y excepciones |
| `/admin/festivos` | Festivos |

Las rutas administrativas requieren autenticación mediante `ProtectedRoute`.

## `src/components/layout/ProtectedRoute.jsx`

Componente global de protección.

Comportamiento:

- Si existe sesión, renderiza `Outlet`.
- Si no existe, redirige a `/admin/login`.
- Conserva la ubicación solicitada para regresar después del login.

---

# Componentes layout

## `src/components/layout/AdminLayout.jsx`

Layout reutilizable de toda la zona administrativa.

Incluye:

- Sidebar.
- Navegación por dominios.
- Nombre del usuario.
- Botón de cierre de sesión.
- Header administrativo responsive.
- `Outlet` para las páginas hijas.

Es el contenedor visual compartido por todas las pantallas `/admin`.

## `src/components/layout/ProtectedRoute.jsx`

Protege los layouts y rutas administrativas.

---

# Componentes UI globales

## `src/components/ui/UI.jsx`

Es el componente central de reutilización visual actual.

## `Button`

Botón global reutilizable.

Variantes:

- `primary`.
- `secondary`.
- `outline`.
- `success`.
- `danger`.

Tamaños:

- `sm`.
- `md`.
- `lg`.

Soporta:

- Estados de carga.
- Deshabilitación.
- Elementos de tipo `submit`.
- Iconos.

## `Input`

Campo de texto global con:

- Label.
- Placeholder.
- Mensaje de error.
- Soporte para `type`, `required`, `min`, `max`, `autocomplete`, etc.

## `Select`

Selector global compatible con `<option>`.

## `Textarea`

Campo multilínea reutilizable.

## `Card`

Contenedor visual reutilizable para:

- Formularios.
- Dashboard.
- CRUD.
- Reservas.
- Estados vacíos.

## `Badge`

Indicador visual compacto.

Tonos disponibles:

- `neutral`.
- `green`.
- `blue`.
- `red`.

## `PageHeader`

Encabezado común para páginas administrativas.

Incluye:

- Título.
- Descripción.
- Acción primaria opcional.

## `Alert`

Alerta visual reutilizable para mostrar errores o mensajes.

## `LoadingBlock`

Estado reutilizable de carga.

## `EmptyState`

Componente preparado para estados vacíos cuando no existen registros.

Estos componentes deben utilizarse antes de crear variantes locales equivalentes.

---

# Hook reutilizable

## `src/hooks/useAsync.js`

Hook para solicitudes asincrónicas.

Administra:

- `data`.
- `loading`.
- `error`.
- `execute(task)`.

Evita repetir estados manuales de carga y errores en páginas pequeñas o modales futuros.

Ejemplo:

```jsx
const { loading, error, execute } = useAsync()

await execute(() => getBarberos())
```

---

# Utilidades

## `src/utils/format.js`

Funciones compartidas:

- `money(value)`.
- `dateTime(value)`.
- `date(value)`.
- `getErrorMessage(error)`.

Centralizar formatos evita diferentes representaciones de moneda, fechas y errores en cada página.

---

# Página pública

## `src/pages/public/ReservaPage.jsx`

Implementa el flujo público de reserva.

Etapas:

1. Seleccionar barbero.
2. Seleccionar servicios y fecha.
3. Consultar horarios disponibles.
4. Registrar datos del cliente.
5. Confirmar la reserva.

### Componentes asociados

- `bookingSteps.jsx`:
  - `StepIndicator`.
  - `BookingSection`.

### Dependencias

- `barberosApi`.
- `serviciosApi`.
- `reservasApi`.
- Componentes UI globales.
- Utilidades de formato.

El flujo mantiene el estado en la página, pero los controles visuales se separan en funciones pequeñas y componentes locales reutilizables.

---

# Páginas administrativas

## Login

### `src/pages/admin/LoginPage.jsx`

Formulario administrativo que:

- Recibe correo y contraseña.
- Ejecuta el login.
- Guarda el JWT mediante `AuthContext`.
- Redirige al panel administrativo.
- Muestra errores normalizados.

---

## Dashboard

### `src/pages/admin/dashboard/DashboardPage.jsx`

Consume datos de:

- Reservas.
- Barberos.
- Servicios.

Muestra tarjetas estadísticas dentro del layout administrativo.

---

## Reservas

### `src/pages/admin/reservas/ReservasPage.jsx`

Permite:

- Filtrar reservas por fecha.
- Filtrar por barbero.
- Filtrar por estado.
- Completar una reserva confirmada.
- Cancelar una reserva confirmada.

Estados visuales:

- Confirmada: azul.
- Cancelada: rojo.
- Completada: verde.

---

## Barberos y asignación de servicios

### `src/pages/admin/barberos/BarberosPage.jsx`

Responsabilidades:

- Crear barberos.
- Editar datos del barbero.
- Activar o desactivar barberos.
- Mostrar servicios activos.
- Mostrar qué servicios están asignados.
- Asignar o desasignar servicios.

### Relación seleccionada

Un barbero puede ofrecer varios servicios. La asignación se representa por medio de:

```text
Barbero + Servicio + activo
```

### Flujo de asignación

1. Se cargan los barberos.
2. Se cargan los servicios activos.
3. Se selecciona un barbero para editar.
4. Se solicitan sus asignaciones actuales.
5. Se comparan los IDs con los servicios disponibles.
6. Al seleccionar o quitar un servicio:
   - Se ejecuta `POST /api/assignments`.
   - O `DELETE /api/assignments/barbero/{id}/servicio/{id}`.

Esto permite que el servicio quede disponible en la vista pública para ese barbero.

---

## Servicios

### `src/pages/admin/servicios/ServiciosPage.jsx`

Permite:

- Crear servicios.
- Editar servicios.
- Activar o desactivar servicios.
- Mostrar precio y duración.

---

## Horarios

### `src/pages/admin/horarios/HorariosPage.jsx`

Permite:

- Seleccionar un barbero.
- Crear turnos de lunes a sábado.
- Activar o desactivar horarios.
- Crear excepciones por rango de fechas.
- Activar o desactivar excepciones.

Los turnos admitidos por el backend están limitados a:

```text
08:00–12:00
14:00–17:00
```

---

## Festivos

### `src/pages/admin/festivos/FestivosPage.jsx`

Permite:

- Crear festivos.
- Editar nombre y fecha.
- Activar o desactivar festivos.

---

# Convenciones de desarrollo

## Creación de una nueva página

1. Crear la carpeta por dominio dentro de `src/pages/admin` o `src/pages/public`.
2. Crear el módulo API correspondiente dentro de `src/api`.
3. Usar componentes globales de `UI.jsx`.
4. Usar `PageHeader` en páginas administrativas.
5. Usar `LoadingBlock`, `Alert` y estados vacíos consistentes.
6. Agregar la ruta en `AppRouter.jsx`.
7. Proteger la página si administra datos mediante `ProtectedRoute`.

## Creación de componentes globales

Un componente debe convertirse en global cuando:

- Se repite en varias páginas.
- Representa una interacción estándar.
- Puede recibir contenido mediante propiedades.
- No depende de un dominio específico.

Ejemplos:

- Botón.
- Campo.
- Tarjeta.
- Badge.
- Encabezado.
- Loader.
- Alerta.

## Convenciones de estado

- Estados visuales pequeños permanecen en la página.
- Estados compartidos por toda la app permanecen en contexto.
- Lógica HTTP permanece en módulos API.
- Formato de datos permanece en utilidades.
- Reglas visuales permanecen en componentes UI o CSS.

## Convenciones de API

Cada módulo API debe:

- Exportar funciones pequeñas.
- Recibir cuerpos o parámetros de forma explícita.
- No administrar estados React.
- No contener componentes visuales.

## Convenciones de diseño

El sistema visual utiliza:

- Dorado `#D4AF37` como primario.
- Carbón `#181818` para sidebar y superficies oscuras.
- Fondo cálido claro `#F8F7F4`.
- Bordes gris claros `#E5E7EB`.
- Verde para éxito.
- Rojo para peligro.
- Azul para estado confirmado.
- Ámbar para advertencias.

Los componentes principales deben priorizar:

- Bordes suaves.
- Radios entre 8 y 12 píxeles.
- Sombras ligeras.
- Tipografía legible.
- Diseño responsive.
- Estados claros de carga, error y vacío.

---

# Servicios públicos y asignaciones

Para que un barbero aparezca disponible con un servicio público se necesitan:

1. Barbero activo.
2. Servicio activo.
3. Asignación activa entre ambos.
4. Horario activo del barbero.
5. Fecha sin festivo ni excepción.

La asignación se realiza desde:

```text
/admin/barberos
  → editar barbero
  → Servicios que realiza
  → seleccionar o quitar servicio
```

---

# Comandos

Ejecutar frontend:

```powershell
cd C:\Users\buqx\barbersoft\barbersoft-front
npm run dev
```

Compilar frontend:

```powershell
npm run build
```

Ejecutar lint:

```powershell
npm run lint
```

---

# Próximas extensiones recomendadas

- Añadir un componente de confirmación para desasignaciones.
- Separar formularios CRUD grandes en componentes dedicados.
- Añadir invalidación visual al editar una asignación.
- Agregar una página de detalle de reserva.
- Agregar tests unitarios de componentes.
- Agregar tests de integración de rutas y APIs.
- Agregar un proveedor de notificaciones tipo toast.
- Incorporar cancelación de peticiones al desmontar páginas.
- Normalizar completamente los colores hardcoded mediante variables CSS.
