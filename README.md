# PillBuddy - Sistema de Gestión de Usuarios

## 📋 Descripción del Proyecto

**PillBuddy** es una aplicación web desarrollada con **Spring Boot 3.5.8** y **Java 21** que implementa un sistema completo de gestión de usuarios (CRUD) con interfaz web interactiva y API REST. El proyecto está orientado a funcionar como base para un dispensador inteligente de medicamentos.


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

**Ejemplo de uso (PowerShell):**
```powershell
# Crear usuario
$body = @{ username="ana"; password="123"; nombre="Ana Lopez"; edad=25 } | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8080/api/usuarios -Method Post -Body $body -ContentType 'application/json'

# Listar usuarios
Invoke-RestMethod -Uri http://localhost:8080/api/usuarios -Method Get
```

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
- **Método custom**: `findByUsername()` para buscar por username (usado en login).

---

### 🗃️ Capa de Modelo (Entities)

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

**Propósito**: Clase base para diferentes tipos de usuarios (Usuario, Médico, Administrador, etc.).

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

### 🖼️ Capa de Vista (Thymeleaf Templates)

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
**Nota**: Los textos como "user123", "Juan Perez", "30" son **placeholders** (valores de ejemplo estáticos). Cuando la app se ejecuta, Thymeleaf los reemplaza con datos reales de la BD.

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

## 🔄 Flujo de Funcionamiento

### 🌐 Flujo Web (MVC - Interfaz Visual)

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

### 🔌 Flujo API REST (JSON)

1. **Cliente (Postman/curl/app móvil) hace petición**: 
   ```http
   POST http://localhost:8080/api/usuarios
   Content-Type: application/json
   
   {
     "username": "maria",
     "password": "abc123",
     "nombre": "Maria Gomez",
     "edad": 28
   }
   ```
2. `UsuarioController.crearUsuario(@RequestBody Usuario usuario)` recibe el JSON.
3. Spring deserializa el JSON a objeto `Usuario`.
4. Llama a `usuarioService.registrarUsuario(usuario)`.
5. El servicio guarda en BD.
6. Retorna el objeto `Usuario` creado (con ID asignado) en formato JSON.

---

## ⚙️ Configuración (`application.properties`)

```properties
# Nombre de la aplicación
spring.application.name=PillBuddy

# Base de datos H2 en modo archivo
spring.datasource.url=jdbc:h2:file:./database
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update       # Crea/actualiza tablas automáticamente
spring.jpa.show-sql=true                   # Muestra SQL en logs

# Consola H2 (admin de BD en navegador)
spring.h2.console.enabled=true
spring.h2.console.path=/h2                 # Acceso en http://localhost:8080/h2

# Soporte para métodos DELETE/PUT en Thymeleaf
spring.mvc.hiddenmethod.filter.enabled=true
```

**Características clave:**
- **Base de datos**: H2 embebida, archivo `./database.mv.db` en la raíz del proyecto.
- **Consola H2**: Accesible en `http://localhost:8080/h2` (usuario: `sa`, contraseña vacía, JDBC URL: `jdbc:h2:file:./database`).
- **Puerto**: 8080 (por defecto, no especificado).

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- **Java 21** instalado.
- **Maven** (o usar el wrapper incluido `mvnw`/`mvnw.cmd`).

### Opción 1: Ejecutar con Maven Wrapper (recomendado)

**En PowerShell (Windows):**
```powershell
# Navegar a la raíz del proyecto
cd C:\Users\ivang\Documents\PillBuddy-Advanced

# Ejecutar la aplicación
.\mvnw.cmd spring-boot:run
```

La aplicación arrancará en: **http://localhost:8080**

### Opción 2: Empaquetar y ejecutar JAR

```powershell
# Compilar y empaquetar (sin tests)
.\mvnw.cmd -DskipTests package

# Ejecutar el JAR generado
java -jar .\target\PillBuddy-0.0.1-SNAPSHOT.jar
```

---

## 🌐 URLs y Endpoints Disponibles

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

## 🧪 Ejemplos de Uso

### Crear usuario desde PowerShell (API REST)
```powershell
$body = @{
    username = "pedro"
    password = "pass123"
    nombre   = "Pedro Martinez"
    edad     = 35
} | ConvertTo-Json

Invoke-RestMethod -Uri http://localhost:8080/api/usuarios -Method Post -Body $body -ContentType 'application/json'
```

### Obtener lista de usuarios
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/usuarios -Method Get
```

### Abrir interfaz web en navegador
```powershell
Start-Process "http://localhost:8080/usuarios"
```

---

## 📊 Diagrama de Relaciones entre Clases

```
┌─────────────────────────────────────────────────────────────┐
│                   CONTROLLERS LAYER                         │
│  ┌─────────────────────┐      ┌──────────────────────┐     │
│  │UsuarioWebController │      │  UsuarioController   │     │
│  │   (@Controller)     │      │  (@RestController)   │     │
│  │                     │      │                      │     │
│  │ /usuarios           │      │ /api/usuarios        │     │
│  └──────────┬──────────┘      └──────────┬───────────┘     │
└─────────────┼─────────────────────────────┼─────────────────┘
              │                             │
              ├─────────────────────────────┤
              ▼                             ▼
┌─────────────────────────────────────────────────────────────┐
│                   SERVICE LAYER                             │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │   UsuarioService     │      │   CuentaService      │    │
│  │    (@Service)        │      │    (@Service)        │    │
│  └──────────┬───────────┘      └──────────┬───────────┘    │
└─────────────┼──────────────────────────────┼────────────────┘
              │                              │
              ├──────────────────────────────┤
              ▼                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  REPOSITORY LAYER                           │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │  UsuarioRepository   │      │  CuentaRepository    │    │
│  │   (JpaRepository)    │      │   (JpaRepository)    │    │
│  └──────────┬───────────┘      └──────────┬───────────┘    │
└─────────────┼──────────────────────────────┼────────────────┘
              │                              │
              ├──────────────────────────────┤
              ▼                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    ENTITY LAYER                             │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │      Usuario         │─────▶│      Cuenta          │    │
│  │     (@Entity)        │      │  (@Entity, abstract) │    │
│  │                      │      │                      │    │
│  │ + nombre: String     │      │ + id: Long           │    │
│  │ + edad: int          │      │ + username: String   │    │
│  │                      │      │ + password: String   │    │
│  └──────────────────────┘      └──────────────────────┘    │
│            ▲                             ▲                  │
│            └─────────────────────────────┘                  │
│                   (Herencia JOINED)                         │
└─────────────────────────────────────────────────────────────┘
```

**Leyenda de relaciones:**
- `UsuarioWebController` → `UsuarioService` (inyección `@Autowired`)
- `UsuarioController` → `UsuarioService` (inyección `@Autowired`)
- `UsuarioService` → `UsuarioRepository` (inyección `@Autowired`)
- `CuentaService` → `CuentaRepository` (inyección `@Autowired`)
- `Usuario` → `Cuenta` (herencia, `extends`)

---

## 🗂️ Gestión de Base de Datos

### Tablas Creadas Automáticamente

Cuando ejecutas la app, Hibernate crea automáticamente (gracias a `spring.jpa.hibernate.ddl-auto=update`):

**Tabla `cuentas`:**
```sql
CREATE TABLE cuentas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
```

**Tabla `usuarios`:**
```sql
CREATE TABLE usuarios (
    cuenta_id BIGINT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    edad INT,
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(id)
);
```

### Acceder a la Consola H2

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


## 📝 Comandos Útiles

### Compilar el proyecto
```powershell
.\mvnw.cmd clean compile
```

### Ejecutar tests
```powershell
.\mvnw.cmd test
```

### Empaquetar sin tests
```powershell
.\mvnw.cmd -DskipTests package
```

### Limpiar archivos generados
```powershell
.\mvnw.cmd clean
```

### Ver dependencias
```powershell
.\mvnw.cmd dependency:tree
```

---

## 🤝 Contribuciones

Este proyecto está en desarrollo activo. Para contribuir:

1. Fork el repositorio.
2. Crea una rama con tu feature: `git checkout -b feature/nueva-funcionalidad`
3. Haz commit de tus cambios: `git commit -m "Añadir nueva funcionalidad"`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request.

---

## 📄 Licencia

Este proyecto es de uso educativo/académico para el **Grupo X**.

---

## 👥 Autores

- **Grupo X** - Proyecto PillBuddy Advanced

---

## 🐛 Resolución de Problemas

### Error: "Puerto 8080 en uso"
```powershell
# Cambiar puerto temporalmente
$env:SERVER_PORT='9090'; .\mvnw.cmd spring-boot:run
```

### Error: "Cannot find JDK"
Asegúrate de tener `JAVA_HOME` configurado apuntando a JDK 21:
```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
.\mvnw.cmd spring-boot:run
```

### La base de datos está vacía
La BD se crea automáticamente. Para añadir usuarios:
- Usa la interfaz web: `http://localhost:8080/usuarios/nuevo`
- O envía un POST a `/api/usuarios` con JSON.

### No se muestran datos en `usuarios.html`
Verifica:
1. Que la app esté corriendo.
2. Que accedas vía `http://localhost:8080/usuarios` (no abriendo el archivo HTML directamente).
3. Que haya usuarios en la BD (revisa en `/h2` o crea uno nuevo).

---

## 📞 Contacto y Soporte

Para preguntas o issues, abre un ticket en el repositorio de GitHub o contacta al equipo de desarrollo.

---

**¡Gracias por usar PillBuddy! 💊🤖**
