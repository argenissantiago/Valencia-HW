-- MySQL dump 10.13  Distrib 9.2.0, for macos14.7 (arm64)
--
-- Host: localhost    Database: MovieApp
-- ------------------------------------------------------
-- Server version	9.2.0

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
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `rating` float DEFAULT NULL,
  `release_year` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `movies_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
INSERT INTO `movies` VALUES (1,1,'Anora',NULL,NULL,NULL),(2,1,'September 5',NULL,NULL,NULL),(3,1,'A Real Pain',NULL,NULL,NULL),(4,2,'Anora',NULL,NULL,NULL),(5,2,'The Substance',NULL,NULL,NULL),(6,2,'Conclave',NULL,NULL,NULL),(7,3,'The Brutalist',NULL,NULL,NULL),(8,3,'Dune: Part Two',NULL,NULL,NULL),(9,3,'Conclave',NULL,NULL,NULL),(10,4,'A Complete Unknown',NULL,NULL,NULL),(11,4,'Dune: Part Two',NULL,NULL,NULL),(12,4,'Wicked',NULL,NULL,NULL),(13,5,'Wicked',NULL,NULL,NULL),(14,5,'Anora',NULL,NULL,NULL),(15,5,'The Substance',NULL,NULL,NULL),(16,6,'Emilia Pérez',NULL,NULL,NULL),(17,6,'I\'m Still Here',NULL,NULL,NULL),(18,6,'The Substance',NULL,NULL,NULL),(19,7,'The Substance',NULL,NULL,NULL),(20,7,'Anora',NULL,NULL,NULL),(21,7,'Conclave',NULL,NULL,NULL),(22,8,'I\'m Still Here',NULL,NULL,NULL),(23,8,'Emilia Pérez',NULL,NULL,NULL),(24,8,'The Substance',NULL,NULL,NULL),(25,9,'Emilia Pérez',NULL,NULL,NULL),(26,9,'Dune: Part Two',NULL,NULL,NULL),(27,9,'Wicked',NULL,NULL,NULL),(28,10,'The Brutalist',NULL,NULL,NULL),(29,10,'Conclave',NULL,NULL,NULL),(30,10,'Anora',NULL,NULL,NULL),(31,11,'Wicked',NULL,NULL,NULL),(32,11,'Anora',NULL,NULL,NULL),(33,11,'The Substance',NULL,NULL,NULL),(34,12,'The Brutalist',NULL,NULL,NULL),(35,12,'Conclave',NULL,NULL,NULL),(36,12,'Anora',NULL,NULL,NULL),(37,13,'Conclave',NULL,NULL,NULL),(38,13,'The Substance',NULL,NULL,NULL),(39,13,'Anora',NULL,NULL,NULL),(40,14,'Anora',NULL,NULL,NULL),(41,14,'The Brutalist',NULL,NULL,NULL),(42,14,'Conclave',NULL,NULL,NULL),(43,15,'A Real Pain',NULL,NULL,NULL),(44,15,'Anora',NULL,NULL,NULL),(45,15,'The Substance',NULL,NULL,NULL),(46,16,'A Complete Unknown',NULL,NULL,NULL),(47,16,'Anora',NULL,NULL,NULL),(48,16,'Conclave',NULL,NULL,NULL),(49,17,'The Apprentice',NULL,NULL,NULL),(50,17,'Anora',NULL,NULL,NULL),(51,17,'Conclave',NULL,NULL,NULL),(52,18,'Conclave',NULL,NULL,NULL),(53,18,'Anora',NULL,NULL,NULL),(54,18,'The Substance',NULL,NULL,NULL),(55,19,'The Apprentice',NULL,NULL,NULL),(56,19,'Anora',NULL,NULL,NULL),(57,19,'Conclave',NULL,NULL,NULL),(58,20,'Sing Sing',NULL,NULL,NULL),(59,20,'Anora',NULL,NULL,NULL),(60,20,'The Substance',NULL,NULL,NULL),(61,1,'titanic',NULL,NULL,NULL),(62,1,'up',NULL,NULL,NULL),(63,1,'cars',NULL,NULL,NULL),(64,1,'moana',NULL,NULL,NULL),(65,1,'toy story',NULL,NULL,NULL),(66,1,'toy story 2',NULL,NULL,NULL),(67,1,'toy story 3',NULL,NULL,NULL);
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `pin` varchar(4) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'seanbaker','1001','sean.baker@anora.com',53,'2025-03-30 18:56:30'),(2,'mikemadison','1002','madison.mikey@anora.com',34,'2025-03-30 18:56:30'),(3,'adrienbrody','1003','brody.adrien@brutalist.com',51,'2025-03-30 18:56:30'),(4,'timotheechalamet','1004','chalamet.timothee@unknown.com',29,'2025-03-30 18:56:30'),(5,'cynthiae','1005','erivo.cynthia@wicked.com',38,'2025-03-30 18:56:30'),(6,'karlasg','1006','gascon.karla@emiliaperez.com',35,'2025-03-30 18:56:30'),(7,'demimoore','1007','moore.demi@substance.com',62,'2025-03-30 18:56:30'),(8,'fernandat','1008','torres.fernanda@stillhere.com',58,'2025-03-30 18:56:30'),(9,'zoesaldana','1009','saldana.zoe@emiliaperez.com',46,'2025-03-30 18:56:30'),(10,'guypearce','1010','pearce.guy@brutalist.com',57,'2025-03-30 18:56:30'),(11,'arianagrande','1011','grande.ariana@wicked.com',31,'2025-03-30 18:56:30'),(12,'felicityjones','1012','jones.felicity@brutalist.com',41,'2025-03-30 18:56:30'),(13,'isabellar','1013','rossellini.isabella@conclave.com',72,'2025-03-30 18:56:30'),(14,'yuraborisov','1014','borisov.yura@anora.com',32,'2025-03-30 18:56:30'),(15,'kieranculkin','1015','culkin.kieran@realpain.com',42,'2025-03-30 18:56:30'),(16,'edwardnorton','1016','norton.edward@unknown.com',55,'2025-03-30 18:56:30'),(17,'jeremystrong','1017','strong.jeremy@apprentice.com',46,'2025-03-30 18:56:30'),(18,'ralphfiennes','1018','fiennes.ralph@conclave.com',62,'2025-03-30 18:56:30'),(19,'sebastianstan','1019','stan.sebastian@apprentice.com',42,'2025-03-30 18:56:30'),(20,'colmandomingo','1020','domingo.colman@singsing.com',55,'2025-03-30 18:56:30');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-31  1:04:40
