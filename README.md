# LabTimeManager

---

[Visitar el sitio web del Proyecto incluida la Documentación y Manual](https://jesusangelmm.github.io/Proyectos/LabTimeManager/Inicio)

---

## Descripción del Proyecto

**LabTimeManager** es una aplicación diseñada para gestionar eficientemente los laboratorios en instituciones académicas y de investigación. Proporciona funcionalidades para la reserva de laboratorios, gestión de materiales, horarios, usuarios, y generación de reportes. El proyecto está desarrollado en Java y utiliza MySQL como base de datos.

## Propósito del Proyecto

El propósito de **LabTimeManager** es proporcionar una herramienta eficaz para la gestión de laboratorios, abordando varios desafíos comunes:

1. **Complejidad de la Gestión de Horarios**: Facilitar la asignación y gestión de horarios de manera eficiente, evitando conflictos y optimizando el uso de los recursos.
2. **Diversidad de Usuarios**: Adaptarse a las necesidades de diferentes tipos de usuarios, como investigadores, estudiantes y técnicos, mediante la asignación de permisos y privilegios específicos.
3. **Seguimiento y Control**: Mantener un registro detallado de la utilización de los laboratorios, facilitando la supervisión y control de las actividades.
4. **Integración de Recursos**: Gestionar no solo los horarios, sino también la reserva de equipos especializados, materiales y espacios adicionales dentro del laboratorio.
5. **Facilidad de Acceso y Reservas**: Permitir a los usuarios verificar la disponibilidad de horarios y realizar reservas desde cualquier lugar, reduciendo la necesidad de coordinación manual.
6. **Generación de Reportes**: Proporcionar informes y estadísticas sobre el uso de los laboratorios, útiles para la toma de decisiones y planificación de recursos.

## Características Clave

- **Gestión de Reservas**: Permite a los usuarios reservar laboratorios y materiales necesarios para sus actividades.
- **Panel de Administración**: Ofrece a los administradores herramientas para gestionar usuarios, laboratorios, materiales y horarios.
- **Informes y Estadísticas**: Genera informes detallados sobre la utilización de laboratorios y materiales.
- **Acceso Remoto**: Los usuarios pueden acceder al sistema desde cualquier lugar para realizar reservas y verificar disponibilidades.

## Arquitectura del Proyecto

El proyecto sigue el patrón de diseño Modelo-Vista-Controlador (MVC). Este enfoque facilita la separación de responsabilidades y mejora la mantenibilidad del código.

### Modelo

Las clases del modelo representan las entidades del sistema y gestionan la lógica de negocio y la interacción con la base de datos.

- **User.java**
- **Laboratory.java**
- **Schedule.java**
- **Reservation.java**
- **Material.java**

### Vista

Las clases de la vista gestionan la interfaz gráfica del usuario utilizando Swing. Estas clases son responsables de mostrar la información al usuario y capturar sus interacciones.

- **LoginWindow.java**
- **UserDashboard.java**
- **AdminDashboard.java**
- **ScheduleViewDialog.java**
- **ReservationDialog.java**
- **ModifyUsersDialog.java**
- **ModifyLabsDialog.java**
- **ModifyMaterialsDialog.java**
- **ModifyScheduleDialog.java**
- **StatisticsDashboard.java**

### Controlador

Los controladores gestionan la lógica de la aplicación y actúan como intermediarios entre el modelo y la vista. Manejan los eventos de la interfaz de usuario y actualizan el modelo y la vista en consecuencia.

## Tecnologías Utilizadas

- **Lenguaje de Programación**: Java
- **Base de Datos**: MySQL
- **Interfaz Gráfica**: Swing
- **Librerías**:
  - JDBC: Para la conexión a la base de datos
  - FlatLaf: Para mejorar el aspecto visual de la interfaz gráfica
  - Apache PDFBox: Para la generación de reportes en PDF

## Instrucciones para Compilar y Ejecutar

### Prerrequisitos

- **Java JDK 8 o superior** debe estar instalado en tu sistema.
- **MySQL** debe estar instalado y configurado.

### Configuración de la Base de Datos

Ejecuta el siguiente script SQL para crear la base de datos y las tablas necesarias:

```sql
-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS LabTimeManager;
USE LabTimeManager;

-- Creación de la tabla 'USER'
CREATE TABLE IF NOT EXISTS `USER` (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    department VARCHAR(100)
);

-- Creación de la tabla 'LABORATORY'
CREATE TABLE IF NOT EXISTS `LABORATORY` (
    id_lab INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    type VARCHAR(50) NOT NULL
);

-- Creación de la tabla 'SCHEDULE'
CREATE TABLE IF NOT EXISTS `SCHEDULE` (
    id_schedule INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

-- Creación de la tabla 'RESERVATION'
CREATE TABLE IF NOT EXISTS `RESERVATION` (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    id_lab INT NOT NULL,
    id_schedule INT NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_user) REFERENCES USER(id_user),
    FOREIGN KEY (id_lab) REFERENCES LABORATORY(id_lab),
    FOREIGN KEY (id_schedule) REFERENCES SCHEDULE(id_schedule)
);

-- Creación de la tabla 'MATERIAL'
CREATE TABLE IF NOT EXISTS `MATERIAL` (
    id_material INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    id_lab INT NOT NULL,
    FOREIGN KEY (id_lab) REFERENCES LABORATORY(id_lab)
);

-- Creación de la tabla 'RESERVATION_MATERIAL'
CREATE TABLE IF NOT EXISTS `RESERVATION_MATERIAL` (
    id_reservation INT NOT NULL,
    id_material INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (id_reservation) REFERENCES RESERVATION(id_reservation),
    FOREIGN KEY (id_material) REFERENCES MATERIAL(id_material),
    PRIMARY KEY (id_reservation, id_material)
);

INSERT INTO MATERIAL (name, quantity, id_lab) VALUES ('Material Genérico', 9999, 1);
```

### Configuración del Archivo `config.properties`

Crea un archivo `config.properties` en el directorio de tu proyecto con los siguientes contenidos:

```properties
db.url=jdbc:mysql://localhost:3306/labtimemanager?useTimeZone=true&serverTimezone=UTC&autoReconnect=true&useSSL=false
db.user=root
db.password=password
```

### Compilación y Ejecución

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER.git
   ```
2. **Importar el proyecto en tu IDE** (NetBeans, IntelliJ IDEA, Eclipse, etc.).
3. **Compilar el proyecto** utilizando tu IDE.
4. **Ejecutar la aplicación** desde tu IDE o generar un archivo `.jar` y ejecutarlo con el siguiente comando:
   ```bash
   java -jar nombre_del_archivo.jar
   ```

## Ejemplos de Uso

### Inicio de Sesión

![Pantalla de Inicio de Sesión](/Imagenes/documentacion1)

### Reserva de Laboratorios

![Reserva de Laboratorios](/Imagenes/Manual3.png)

### Gestión de Usuarios

![Gestión de Usuarios](/Imagenes/Manual4.png)

### Generación de Reportes

![Generación de Reportes](/Imagenes/Manual8.png)

---

Para obtener más detalles y la documentación completa, visita nuestro [sitio web](https://jesusangelmm.github.io/Proyectos/LabTimeManager/Inicio).

---

**Autores**:
- Jesús Ángel Martínez Mendoza
- Jennifer Diego García

**Contacto**:
- jesusangelmartinezmendoza0702@gmail.com
- jenniferdiegogarcia3@gmail.com

**Repositorio en GitHub**: [LabTimeManager](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER.git)

---

Gracias por tu interés en **LabTimeManager**. Estamos emocionados de ver cómo nuestra herramienta puede hacer una diferencia en tu institución.