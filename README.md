# PillBuddy

## Descripción del proyecto

**PillBuddy** es un pastillero inteligente el cual es monitorizado por una app web.


## Clases principales 

#### `PillBuddyApplication.java`
```java
@SpringBootApplication
public class PillBuddyApplication {
    public static void main(String[] args) {
        SpringApplication.run(PillBuddyApplication.class, args);
    }
}
```

- **Función**: Arranca el servidor embebido Tomcat y configura el contexto de Spring.

#### `ServletInitializer.java`
```java
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PillBuddyApplication.class);
    }
}
```
- **Responsabilidad**: Permite empaquetar la app como WAR y desplegarla en un servidor externo.
- **Uso**: Opcional, para despliegue en Tomcat/JBoss externos.

---

### Controllers

#### `UsuarioWebController.java` (Controlador MVC)
- **Anotación**: `@Controller`
- **Rutas base**: `/usuarios`
- **Responsabilidad**: Maneja peticiones HTTP que devuelven **vistas HTML** (Thymeleaf).

**Métodos:**

| Método | Ruta | Acción |
|--------|------|--------|
| `listarUsuarios()` | `GET /usuarios` | Muestra la lista de usuarios en `usuarios.html` |
| `mostrarFormularioNuevo()` | `GET /usuarios/nuevo` | Muestra formulario vacío para crear usuario |
| `guardarUsuario()` | `POST /usuarios/guardar` | Guarda (crea o actualiza) un usuario y redirige a lista |
| `mostrarFormularioEditar()` | `GET /usuarios/editar/{id}` | Muestra formulario prellenado con datos del usuario |
| `eliminarUsuario()` | `GET /usuarios/eliminar/{id}` | Elimina usuario y redirige a lista |

**Relaciones:**
- Inyecta `UsuarioService`.
- Retorna nombres de vistas que Thymeleaf resuelve en `/templates/`.

---

#### `UsuarioController.java` (API REST)
- **Anotación**: `@RestController`
- **Rutas base**: `/api/usuarios`
- **Responsabilidad**: Expone endpoints REST que devuelven **JSON** para integración con clientes (móvil, SPA, Postman).

**Métodos:**

| Método HTTP | Ruta | Acción |
|-------------|------|--------|
| `POST` | `/api/usuarios` | Crea un nuevo usuario (recibe JSON en body) |
| `GET` | `/api/usuarios` | Devuelve lista completa de usuarios en JSON |
| `GET` | `/api/usuarios/{id}` | Devuelve un usuario específico por ID |
| `PUT` | `/api/usuarios/{id}` | Actualiza un usuario existente (recibe JSON) |
| `DELETE` | `/api/usuarios/{id}` | Elimina un usuario por ID |

**Relaciones:**
- Inyecta `UsuarioService`.
- Devuelve `ResponseEntity<Usuario>` o `List<Usuario>` (serializado a JSON).

---

### Services

#### `UsuarioService.java`
- **Anotación**: `@Service`
- **Responsabilidad**: Contiene operaciones CRUD de usuarios.

**Métodos:**

| Método | Descripción |
|--------|-------------|
| `registrarUsuario(Usuario)` | Guarda un usuario nuevo o actualiza uno existente |
| `listarUsuarios()` | Devuelve lista de todos los usuarios |
| `obtenerPorId(Long)` | Busca un usuario por ID (retorna `Optional<Usuario>`) |
| `eliminarUsuario(Long)` | Elimina un usuario por ID |
| `actualizarUsuario(Long, Usuario)` | Actualiza campos de un usuario existente |

**Relaciones:**
- Inyecta `UsuarioRepository` (JPA).
- Usado por `UsuarioController` y `UsuarioWebController`.

---

#### `CuentaService.java`
- **Anotación**: `@Service`
- **Responsabilidad**: Lógica relacionada con autenticación y gestión de cuentas.

**Métodos:**

| Método | Descripción |
|--------|-------------|
| `buscarPorUsername(String)` | Busca una cuenta por username|
| `cambiarPassword(Long, String)` | Cambia la contraseña de una cuenta |

**Relaciones:**
- Inyecta `CuentaRepository`.


---

### Repositories

#### `UsuarioRepository.java`
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
```
- **Hereda de**: `JpaRepository<Usuario, Long>`
- **Responsabilidad**: CRUD automático para la entidad `Usuario` (sin necesidad de implementar métodos).
- **Métodos heredados**: `save()`, `findAll()`, `findById()`, `deleteById()`, etc.

---

#### `CuentaRepository.java`
```java
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByUsername(String username);
}
```
- **Hereda de**: `JpaRepository<Cuenta, Long>`
- **Responsabilidad**: Acceso a datos de la entidad abstracta `Cuenta`.
- **Método custom**: `findByUsername()` para buscar por username.

---

### Entities

#### `Cuenta.java` (Entidad Abstracta)
```java
@Entity
@Table(name = "cuentas")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    // Getters y Setters
}
```

**Características:**
- **Clase abstracta**: No se puede instanciar directamente.
- **Estrategia de herencia**: `JOINED` → Tabla separada para `Cuenta` y tablas hijas con FK.
- **Campos**:
  - `id`: Clave primaria autoincremental.
  - `username`: Único, requerido.
  - `password`: Requerido (sin encriptación por ahora).

**Propósito**: Clase base para diferentes tipos de usuarios (usuario, cuidador).

---

#### `Usuario.java` (Entidad Concreta)
```java
@Entity
@Table(name = "usuarios")
@PrimaryKeyJoinColumn(name = "cuenta_id")
public class Usuario extends Cuenta {
    @Column(nullable = false)
    private String nombre;
    
    private int edad;
    
    // Constructores, Getters y Setters
}
```

**Características:**
- **Hereda de**: `Cuenta` (obtiene `id`, `username`, `password`).
- **Estrategia**: Tabla `usuarios` con FK `cuenta_id` que apunta a `cuentas.id`.
- **Campos adicionales**:
  - `nombre`: Nombre completo del usuario.
  - `edad`: Edad en años.

**Relación en BD:**
```
Tabla: cuentas
+----+----------+----------+
| id | username | password |
+----+----------+----------+

Tabla: usuarios
+------------+--------+------+
| cuenta_id  | nombre | edad |  (cuenta_id es FK y PK)
+------------+--------+------+
```

---

### Thymeleaf Templates

#### `usuarios.html`
- **Ruta**: `/usuarios` (GET)
- **Propósito**: Muestra tabla con lista de usuarios.
- **Características**:
  - Usa Bootstrap 5.3 para estilos.
  - Botón "Agregar Nuevo Usuario" → `/usuarios/nuevo`.
  - Cada fila tiene botones "Editar" y "Eliminar".
  - Los datos se inyectan con Thymeleaf: `th:each="usuario : ${listaUsuarios}"`.

**Fragmento clave:**
```html
<tr th:each="usuario : ${listaUsuarios}">
    <td th:text="${usuario.id}">1</td>
    <td th:text="${usuario.username}">user123</td>
    <td th:text="${usuario.nombre}">Juan Perez</td>
    <td th:text="${usuario.edad}">30</td>
    <td>
        <a th:href="@{/usuarios/editar/{id}(id=${usuario.id})}" class="btn btn-warning btn-sm">Editar</a>
        <a th:href="@{/usuarios/eliminar/{id}(id=${usuario.id})}" class="btn btn-danger btn-sm">Eliminar</a>
    </td>
</tr>
```

---

#### `formulario_usuario.html`
- **Rutas**: `/usuarios/nuevo` (GET), `/usuarios/editar/{id}` (GET), `/usuarios/guardar` (POST)
- **Propósito**: Formulario reutilizable para crear o editar usuarios.
- **Características**:
  - Campos: `username`, `password`, `nombre`, `edad`.
  - Campo oculto `id` para identificar si es creación o actualización.
  - Al enviar (POST), llama a `/usuarios/guardar`.

**Fragmento clave:**
```html
<form th:action="@{/usuarios/guardar}" th:object="${usuario}" method="POST">
    <input type="hidden" th:field="*{id}" />
    <input type="text" th:field="*{username}" class="form-control" required />
    <input type="password" th:field="*{password}" class="form-control" required />
    <input type="text" th:field="*{nombre}" class="form-control" required />
    <input type="number" th:field="*{edad}" class="form-control" required />
    <button type="submit" class="btn btn-success">Guardar</button>
</form>
```

---

## Flujo de Funcionamiento

1. **Usuario accede a**: `http://localhost:8080/usuarios`
2. `UsuarioWebController.listarUsuarios()` recibe la petición.
3. Llama a `usuarioService.listarUsuarios()` → obtiene lista desde BD.
4. Añade la lista al modelo: `model.addAttribute("listaUsuarios", usuarios)`.
5. Retorna nombre de vista: `"usuarios"`.
6. Thymeleaf renderiza `templates/usuarios.html` con los datos.
7. El navegador muestra la tabla HTML con los usuarios.

**Para crear/editar:**
1. Usuario hace clic en "Agregar" o "Editar".
2. `UsuarioWebController` muestra `formulario_usuario.html` (vacío o prellenado).
3. Usuario completa el formulario y envía (POST `/usuarios/guardar`).
4. `guardarUsuario()` llama a `usuarioService.registrarUsuario()`.
5. El servicio guarda en BD vía `usuarioRepository.save()`.
6. Redirige a `/usuarios` (se actualiza la lista).

---

## URLs y Endpoints Disponibles

### Interfaz Web (HTML)
| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/usuarios` | Lista de usuarios (tabla) |
| `http://localhost:8080/usuarios/nuevo` | Formulario para crear usuario |
| `http://localhost:8080/usuarios/editar/{id}` | Formulario para editar usuario |
| `http://localhost:8080/usuarios/eliminar/{id}` | Eliminar usuario (redirige a lista) |

### API REST (JSON)
| Método | URL | Descripción |
|--------|-----|-------------|
| `GET` | `http://localhost:8080/api/usuarios` | Obtener todos los usuarios |
| `GET` | `http://localhost:8080/api/usuarios/{id}` | Obtener usuario por ID |
| `POST` | `http://localhost:8080/api/usuarios` | Crear nuevo usuario |
| `PUT` | `http://localhost:8080/api/usuarios/{id}` | Actualizar usuario |
| `DELETE` | `http://localhost:8080/api/usuarios/{id}` | Eliminar usuario |

### Herramientas
| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/h2` | Consola de administración H2 |

---

### Acceder a la consola H2

1. Arrancar la app.
2. Abrir en el navegador: `http://localhost:8080/h2`
3. Configuración de conexión:
   - **JDBC URL**: `jdbc:h2:file:./database`
   - **Username**: `sa`
   - **Password**: (dejar vacío)
4. Hacer clic en "Connect".


### Archivos de BD Ignorados en Git

El archivo `.gitignore` incluye:
```
/database*
*.mv.db
*.h2.db
*.trace.db
```
Esto evita que los archivos de base de datos locales se suban al repositorio.
