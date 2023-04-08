use soborodb;
CREATE TABLE `user` (
                        `user_no` int(11) NOT NULL AUTO_INCREMENT,
                        `user_id` varchar(15) DEFAULT NULL,
                        `user_password` varchar(255) DEFAULT NULL,
                        `user_name` varchar(50) DEFAULT NULL,
                        `user_email` varchar(100) DEFAULT NULL,
                        `user_phone` varchar(50) DEFAULT NULL,
                        `user_birthdate` char(8) DEFAULT NULL,
                        `user_gender` char(1) DEFAULT NULL,
                        `user_terms` tinyint(1) DEFAULT NULL,
                        `user_provide_type` varchar(20) DEFAULT NULL,
                        `user_role_type` varchar(20) DEFAULT NULL,
                        `user_created_time` timestamp NULL DEFAULT NULL,
                        `user_active` tinyint(1) DEFAULT NULL,
                        PRIMARY KEY (`user_no`)
);
CREATE TABLE `consulting` (
                              `consulting_no` int(11) NOT NULL AUTO_INCREMENT,
                              `consulting_user_no` int(11) DEFAULT NULL,
                              `consulting_visit_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                              `consulting_visit_place` varchar(100) DEFAULT NULL,
                              `consulting_visit_class` varchar(100) DEFAULT NULL,
                              `video_location` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`consulting_no`),
                              KEY `fk_user_userno` (`consulting_user_no`),
                              CONSTRAINT `fk_user_userno` FOREIGN KEY (`consulting_user_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE ON UPDATE CASCADE
);

LOCK TABLES `consulting` WRITE;
/*!40000 ALTER TABLE `consulting` DISABLE KEYS */;
INSERT INTO `consulting` VALUES
                             (1,1,'2023-03-20 10:53:35','봉명동 은행1','은행','www.video.com'),
                             (2,1,'2023-03-20 10:53:35','봉명동 우체국1','은행','www.video.com'),
                             (3,2,'2023-03-22 11:04:34','봉명동 동사무소','동사무소','www.video.com'),
                             (4,2,'2023-03-20 10:53:35','봉명동 우체국','은행','www.video.com'),
                             (5,1,'2023-03-21 10:53:35','봉명동 도서관1','도서관','www.video.com'),
                             (6,1,'2023-03-21 12:53:35','봉명동 동사무소1','복지센터','www.video.com'),
                             (7,1,'2023-03-20 10:53:35','봉명동 은행2','은행','www.video.com'),
                             (8,1,'2023-03-20 10:53:35','봉명동 우체국2','은행','www.video.com'),
                             (9,1,'2023-03-22 11:04:34','봉명동 동사무소2','동사무소','www.video.com'),
                             (10,1,'2023-03-20 10:53:35','봉명동 우체국3','은행','www.video.com'),
                             (11,1,'2023-03-21 10:53:35','봉명동 도서관3','도서관','www.video.com'),
                             (12,1,'2023-03-21 12:53:35','봉명동 동사무소3','복지센터','www.video.com'),
                             (13,1,'2023-03-20 10:53:35','봉명동 은행4','은행','www.video.com'),
                             (14,1,'2023-03-20 10:53:35','봉명동 우체국4','은행','www.video.com'),
                             (15,1,'2023-03-22 11:04:34','봉명동 동사무소4','동사무소','www.video.com'),
                             (16,1,'2023-03-20 10:53:35','봉명동 우체국5','은행','www.video.com'),
                             (17,1,'2023-03-21 10:53:35','봉명동 도서관5','도서관','www.video.com'),
                             (18,1,'2023-03-21 12:53:35','봉명동 동사무소5','복지센터','www.video.com');
/*!40000 ALTER TABLE `consulting` ENABLE KEYS */;
UNLOCK TABLES;