-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: labtimemanager
-- ------------------------------------------------------
-- Server version	8.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `administrator`
--

DROP TABLE IF EXISTS `administrator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrator` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrator`
--

LOCK TABLES `administrator` WRITE;
/*!40000 ALTER TABLE `administrator` DISABLE KEYS */;
/*!40000 ALTER TABLE `administrator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `laboratory`
--

DROP TABLE IF EXISTS `laboratory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `laboratory` (
  `id_lab` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `capacity` int NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`id_lab`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `laboratory`
--

LOCK TABLES `laboratory` WRITE;
/*!40000 ALTER TABLE `laboratory` DISABLE KEYS */;
INSERT INTO `laboratory` VALUES (1,'Laboratorio de Fisicoquímica','Edificio Z',40,'Fisicoquímica'),(2,'Laboratorio de Ing. Civil','Edificio B',25,'Ingeniería Civil'),(3,'Laboratorio de Ing. Eléctrica','Edificio C',20,'Ingeniería Eléctrica'),(4,'Laboratorio de Ing. Industrial','Edificio D',40,'Ingeniería Industrial'),(6,'Ingenieria en Sistemas','Edificio I',80,'Simulación'),(8,'Laboratorio de Ing. Mecatronica','Edificio C',30,'Ingeniería Eléctrica');
/*!40000 ALTER TABLE `laboratory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `material`
--

DROP TABLE IF EXISTS `material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material` (
  `id_material` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `id_lab` int NOT NULL,
  PRIMARY KEY (`id_material`),
  KEY `id_lab` (`id_lab`),
  CONSTRAINT `material_ibfk_1` FOREIGN KEY (`id_lab`) REFERENCES `laboratory` (`id_lab`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `material`
--

LOCK TABLES `material` WRITE;
/*!40000 ALTER TABLE `material` DISABLE KEYS */;
INSERT INTO `material` VALUES (1,'Reactivos Químicos',100,1),(2,'Cementos y Arenas',30,2),(3,'Resistencias y Capacitores',200,3),(4,'Equipos de Medición',30,4),(7,'Material Genérico',9999,1),(9,'Audifonos',40,2),(10,'Calculadora',40,1),(11,'Multimetro',15,2);
/*!40000 ALTER TABLE `material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id_reservation` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `id_lab` int NOT NULL,
  `id_schedule` int NOT NULL,
  `purpose` varchar(255) NOT NULL,
  `status` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`id_reservation`),
  KEY `id_user` (`id_user`),
  KEY `id_lab` (`id_lab`),
  KEY `id_schedule` (`id_schedule`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`),
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`id_lab`) REFERENCES `laboratory` (`id_lab`),
  CONSTRAINT `reservation_ibfk_3` FOREIGN KEY (`id_schedule`) REFERENCES `schedule` (`id_schedule`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (16,1,1,1,'Experimentación con reactivos','confirmed','Experimentación'),(18,3,3,3,'Simulación de circuitos','pending','Simulación'),(19,1,4,4,'Estudio de tiempos de producción','cancelled','Estudio'),(34,2,2,27,'Medir materia','confirmed','Clase'),(36,2,1,29,'Medir material','confirmed','Práctica'),(40,2,3,33,'Medir la masa de los muchos compuestos quimicos','confirmed','Clase'),(41,1,1,34,'','confirmed','Práctica'),(42,2,2,35,'Crear un merchero','confirmed','Práctica'),(43,2,1,36,'','confirmed','Práctica'),(44,2,2,37,'Hola','confirmed','Clase'),(45,2,2,38,'Prueba 2','confirmed','Práctica');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation_material`
--

DROP TABLE IF EXISTS `reservation_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_material` (
  `id_reservation` int NOT NULL,
  `id_material` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id_reservation`,`id_material`),
  KEY `id_material` (`id_material`),
  CONSTRAINT `reservation_material_ibfk_1` FOREIGN KEY (`id_reservation`) REFERENCES `reservation` (`id_reservation`),
  CONSTRAINT `reservation_material_ibfk_2` FOREIGN KEY (`id_material`) REFERENCES `material` (`id_material`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation_material`
--

LOCK TABLES `reservation_material` WRITE;
/*!40000 ALTER TABLE `reservation_material` DISABLE KEYS */;
INSERT INTO `reservation_material` VALUES (34,1,1),(36,1,1),(40,1,1),(41,3,1),(42,2,1),(42,7,1),(44,9,1),(45,1,1);
/*!40000 ALTER TABLE `reservation_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `id_schedule` int NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  PRIMARY KEY (`id_schedule`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` VALUES (1,'2024-05-20','08:00:00','10:00:00'),(2,'2024-05-21','10:00:00','12:00:00'),(3,'2024-05-22','13:00:00','15:00:00'),(4,'2024-05-23','15:00:00','17:00:00'),(5,'2024-05-24','09:00:00','11:00:00'),(6,'2024-05-16','07:00:00','08:00:00'),(7,'2024-05-16','07:00:00','08:00:00'),(8,'2024-05-16','07:00:00','08:00:00'),(9,'2024-05-16','07:00:00','08:00:00'),(10,'2024-05-16','07:00:00','08:00:00'),(11,'2024-05-16','07:00:00','08:00:00'),(12,'2024-05-16','07:00:00','08:00:00'),(13,'2024-05-16','07:00:00','08:00:00'),(14,'2024-05-16','07:00:00','08:00:00'),(15,'2024-05-16','07:00:00','08:00:00'),(16,'2024-05-16','07:00:00','08:00:00'),(17,'2024-05-16','07:00:00','08:00:00'),(18,'2024-05-16','07:00:00','08:00:00'),(19,'2024-05-22','10:00:00','11:00:00'),(20,'2024-05-22','10:00:00','11:00:00'),(21,'2024-05-22','07:00:00','08:00:00'),(22,'2024-05-22','11:00:00','12:00:00'),(23,'2024-05-20','10:00:00','11:00:00'),(24,'2024-05-20','10:00:00','11:00:00'),(25,'2024-05-23','11:00:00','12:00:00'),(26,'2024-05-23','07:00:00','08:00:00'),(27,'2024-05-20','07:00:00','08:00:00'),(28,'2024-05-02','07:00:00','08:00:00'),(29,'2024-05-20','07:00:00','08:00:00'),(30,'2024-05-21','12:00:00','13:00:00'),(31,'2024-05-23','07:00:00','08:00:00'),(32,'2024-05-24','12:00:00','13:00:00'),(33,'2024-05-21','09:00:00','10:00:00'),(34,'2024-05-21','12:00:00','13:00:00'),(35,'2024-05-30','07:00:00','08:00:00'),(36,'2024-05-21','07:00:00','08:00:00'),(37,'2024-05-22','09:00:00','10:00:00'),(38,'2024-05-23','11:00:00','12:00:00');
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `department` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'JesusAngel','12345','jesusangelmartinezmendoza0702@gmail.com','administrador','Sistemas'),(2,'JennyDiego','12345','jenniferdiegogarcia3@gmail.com','usuario','Sistemas'),(3,'MariaLopez','password123','marialopez@gmail.com','usuario','Química'),(13,'AdelinaCruzAlcala','54321','ade@gmail.com','administrador','Sistemas');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-20 17:42:31
