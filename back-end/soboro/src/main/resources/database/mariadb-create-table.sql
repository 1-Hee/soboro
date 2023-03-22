use soborodb;
CREATE TABLE `user` (
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> a04fb1d (Merge branch 'doyeong' into 'BE')
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
<<<<<<< HEAD
)
=======
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

insert into user
values (1, 'test', '$2a$10$0AycQUE0MPdxY7zTylZmJebhQ1dqNksMhA4lrmg52TiefKPbV.Zju', 'testName', 'testEmail@Eamil.com', '01012345678', '20220101', 'N', 1, 'LOCAL', 'USER', '2023-03-20 10:53:35', 1);

CREATE TABLE `consulting` (
                              `consulting_no` int(11) NOT NULL AUTO_INCREMENT,
                              `consulting_user_no` int(11) DEFAULT NULL,
                              `consulting_visit_date` timestamp NULL DEFAULT NULL,
                              `consulting_visit_place` varchar(100) DEFAULT NULL,
                              `consulting_visit_class` varchar(100) DEFAULT NULL,
                              `video_location` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`consulting_no`),
                              KEY `fk_user_userno` (`consulting_user_no`),
                              CONSTRAINT `fk_user_userno` FOREIGN KEY (`consulting_user_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
>>>>>>> 0a72c92 (docs: sql table 수정)
=======
)
>>>>>>> a04fb1d (Merge branch 'doyeong' into 'BE')
