-- MySQL dump 10.13  Distrib 8.4.2, for Win64 (x86_64)
--
-- Host: localhost    Database: webrtc
-- ------------------------------------------------------
-- Server version	8.4.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `USERNAME` varchar(50) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  PRIMARY KEY (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES ('Alice','$2a$10$3gYInOjFrkgYvEgHIi7gquGG8.mVwY1oDES4p7431Pwmo04Rwlg7S'),('Bob','$2a$10$5UgetHf5b4pM/St25VuNC.H4pfJbMaDvL4WGxvkqrcaCv4zete/He'),('Consulente1','$2a$10$7pmRdA4HSB0Av9fKutuOR.watojgxTYZNdLYcdq.13Q024hPhXNJW'),('Marco','$2a$10$Ity9uXXHZWCwXKGjWZ.kXeGvqPcJFKFZ.FX4C8nffyHYWRRDFjecK'),('Pippo','$2a$10$e.06Tt4eqTdEwEOlwSc0guupLwBzJyT2lSxmkPIV7J2shsdnHSgX.');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `citycomments`
--

DROP TABLE IF EXISTS `citycomments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `citycomments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `city_name` varchar(255) NOT NULL,
  `comment` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `parent_comment_id` int DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `city_name` (`city_name`),
  KEY `fk_parent_comment` (`parent_comment_id`),
  CONSTRAINT `citycomments_ibfk_1` FOREIGN KEY (`city_name`) REFERENCES `homecity` (`city_name`),
  CONSTRAINT `fk_parent_comment` FOREIGN KEY (`parent_comment_id`) REFERENCES `citycomments` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `citycomments`
--

LOCK TABLES `citycomments` WRITE;
/*!40000 ALTER TABLE `citycomments` DISABLE KEYS */;
INSERT INTO `citycomments` VALUES (3,'Tokyo','Per chi fa questo viaggio è obbligatorio visitare la Tokyo Tower!','Marco',NULL),(13,'Tokyo','<script>alert(\"XSS non funziona fortunatamente\")</script>','Marco',NULL),(19,'Barcellona','Acquistate l\'abbonamento per la metro con lo sconto turisti','Marco',NULL),(47,'Tokyo','Ue giovane che stavi facendo?','admin',13),(95,'Parigi','Consiglio di visitare il Museo del Louvre','Marco',NULL),(96,'Tokyo','Stava cercando di effettuare un attacco XSS!','Bob',47),(97,'Parigi','Salite sulla Torre Eiffel!','Alice',NULL),(98,'Vienna','D\'inverno fa molto freddo ma ne vale la pena','Alice',NULL),(102,'Parigi','Lo farò!','Bob',97),(107,'Vienna','Hai ragione!!','Marco',98),(108,'Tokyo','Per gli appassionati di videogiochi consiglio di andare ad Akhiabara!','Marco',NULL),(109,'Tokyo','Grazie, ci sono stato e ho trovato tutto ciò che cercavo!','Bob',108),(110,'Tokyo','Sono da qulche giorno a Tokyo, riesci a consigliarmi qualche negozio in particolare?','Alice',108);
/*!40000 ALTER TABLE `citycomments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consulenti`
--

DROP TABLE IF EXISTS `consulenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consulenti` (
  `USERNAME` varchar(50) NOT NULL,
  `NOME` varchar(50) NOT NULL,
  `COGNOME` varchar(50) NOT NULL,
  `EMAIL` varchar(100) NOT NULL,
  PRIMARY KEY (`USERNAME`),
  CONSTRAINT `fk_consulenti_account` FOREIGN KEY (`USERNAME`) REFERENCES `account` (`USERNAME`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consulenti`
--

LOCK TABLES `consulenti` WRITE;
/*!40000 ALTER TABLE `consulenti` DISABLE KEYS */;
INSERT INTO `consulenti` VALUES ('Consulente1','Giovanni','Sarpi','consulente@email.com'),('Pippo','pippo','pippo','pippo@email.com');
/*!40000 ALTER TABLE `consulenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homecity`
--

DROP TABLE IF EXISTS `homecity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homecity` (
  `city_name` varchar(255) NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`city_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homecity`
--

LOCK TABLES `homecity` WRITE;
/*!40000 ALTER TABLE `homecity` DISABLE KEYS */;
INSERT INTO `homecity` VALUES ('Barcellona','/images/barcellona.jpg'),('Oslo','/images/oslo.jpg'),('Parigi','/images/parigi.jpg'),('Roma','/images/roma.jpg'),('Tokyo','/images/tokyo.jpg'),('Vienna','/images/vienna.jpg');
/*!40000 ALTER TABLE `homecity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utenti`
--

DROP TABLE IF EXISTS `utenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utenti` (
  `USERNAME` varchar(50) NOT NULL,
  `NOME` varchar(50) NOT NULL,
  `COGNOME` varchar(50) NOT NULL,
  `NICKNAME` varchar(50) DEFAULT NULL,
  `EMAIL` varchar(100) NOT NULL,
  PRIMARY KEY (`USERNAME`),
  CONSTRAINT `utenti_ibfk_1` FOREIGN KEY (`USERNAME`) REFERENCES `account` (`USERNAME`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utenti`
--

LOCK TABLES `utenti` WRITE;
/*!40000 ALTER TABLE `utenti` DISABLE KEYS */;
INSERT INTO `utenti` VALUES ('Alice','Alice','Alice','Alice','alice@email.com'),('Bob','Bob','Bob','Bob','bob@email.com'),('Marco','Marco','Bianchi','M4rk0','mark@email.com');
/*!40000 ALTER TABLE `utenti` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-16  8:14:09
