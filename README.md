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

**Descripción:** En esta pantalla, los usuarios pueden iniciar sesión en el sistema. Se requiere ingresar un nombre de usuario y una contraseña. Dependiendo del rol del usuario (administrador o usuario normal), serán redirigidos a diferentes paneles de control. Este sistema de autenticación asegura que solo usuarios autorizados puedan acceder a las funcionalidades del sistema.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/4768fb46-2adb-4df8-beb9-fc5b00a923c9)

### Reserva de Laboratorios

**Descripción:** Aquí los usuarios pueden reservar laboratorios. Deben seleccionar el laboratorio deseado, la fecha y la hora de la reserva, e ingresar el propósito de la misma. Esta funcionalidad permite a los usuarios planificar y gestionar sus actividades en el laboratorio de manera eficiente.

![Reserva de Laboratorios](/Imagenes/Manual3.png)

### Gestión de Usuarios

**Descripción:** Los administradores pueden gestionar los usuarios del sistema. Pueden agregar nuevos usuarios, editar la información de usuarios existentes o eliminar usuarios. Esto facilita el mantenimiento de un registro actualizado y preciso de todas las personas que tienen acceso al sistema.

![Gestión de Usuarios](/Imagenes/Manual4.png)

#### Usuario Administrador

**Descripción:** Los administradores tienen acceso a un panel de control avanzado donde pueden gestionar todas las entidades del sistema, incluyendo usuarios, laboratorios, materiales y horarios.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/22b15ea3-d1e7-4219-a58c-580a2b83f02c)

##### Modificar Reservación

**Descripción:** Esta pantalla permite a los administradores modificar las reservaciones existentes. Pueden cambiar la fecha, hora, laboratorio y otros detalles de la reserva para asegurarse de que se ajusten a las necesidades actuales.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/993950b1-f2ab-4138-b523-227865219cc9)

##### Modificar Usuarios

**Descripción:** Los administradores pueden editar la información de los usuarios registrados. Esto incluye actualizar datos como el nombre, correo electrónico, rol y departamento del usuario.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/5dc5c448-1bef-4ca0-83b6-f8109b926372)

##### Modificar Laboratorios

**Descripción:** Los administradores pueden gestionar los laboratorios disponibles. Pueden agregar nuevos laboratorios, editar la información de los laboratorios existentes o eliminarlos del sistema.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/88896fa0-0cca-45a7-8cdf-729e879332c3)

##### Modificar Material

**Descripción:** Esta funcionalidad permite a los administradores gestionar los materiales disponibles en los laboratorios. Pueden agregar nuevos materiales, actualizar las cantidades disponibles y eliminar materiales obsoletos.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/6169fe58-e830-4582-996d-fa1fd1b1c74e)

##### Ver estadisticas

**Descripción:** Los administradores pueden visualizar estadísticas detalladas sobre el uso de los laboratorios y materiales. Esta información ayuda en la toma de decisiones y la planificación de recursos.

<p align="center">
  <img src="https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/ebe6d0cf-0af0-4b9d-bf20-9db2eefec0eb" width="300"/>
  <img src="https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/5502208f-140a-4249-8b96-7d59621dba55" width="300"/>
  <img src="https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/81353c4f-94e7-4b75-8722-6d759243cbe9" width="300"/>
  <img src="https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/24402686-247a-4a1a-8fb9-f92ff8aea02a" width="300"/>
</p>

#### Usuario

**Descripción:** Los usuarios tienen acceso a un panel de control donde pueden ver y gestionar sus propias reservas. Esto incluye hacer nuevas reservas, ver las reservas existentes y cancelar o modificar reservas según sea necesario.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/a20efa44-f7ac-4aa6-bbdc-ebaab2af5cfc)

##### Ver reservaciones

**Descripción:** Los usuarios pueden ver todas sus reservaciones en una lista. Esta funcionalidad permite a los usuarios mantener un seguimiento de sus actividades y planificar su uso del laboratorio de manera efectiva.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/28fb6ea6-4b67-4356-afaf-5ac0c2f59326)


### Generación de Reportes

**Descripción:** Los administradores pueden generar reportes detallados sobre las reservaciones y el uso de los laboratorios y materiales. Estos reportes pueden exportarse en formato PDF para su análisis y archivo.

![Generación de Reportes](/Imagenes/Manual8.png)

### Preferencias de apariencia para usuarios

**Descripción:** Los usuarios pueden personalizar la apariencia de la aplicación según sus preferencias. Pueden seleccionar diferentes temas y configuraciones de visualización para mejorar su experiencia de usuario.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/4523cc6d-515b-4ad7-9429-a05b9b12157b)

### Ayuda: Manual y documentación


**Descripción:** La aplicación proporciona una sección de ayuda y documentación detallada para asistir a los usuarios en el uso del sistema. Esta sección incluye un manual de usuario, tutoriales y respuestas a preguntas frecuentes.

![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/40c3af1f-6235-4766-9135-6e24a34d893f)
![image](https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER/assets/167843278/f0af79ff-c186-4ac7-bd07-5bb29cecb6a7)

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
