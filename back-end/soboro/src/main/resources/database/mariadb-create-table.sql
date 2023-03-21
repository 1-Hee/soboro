use soborodb;
create table user(
                     `user_no` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                     `user_id` varchar(15),
                     `user_password` varchar(255),
                     `user_name` varchar(50),
                     `user_email` varchar(100),
                     `user_phone` varchar(50),
                     `user_birthdate` char(8),
                     `user_gender` char(1),
                     `user_terms` boolean,
                     `user_provide_type` varchar(20),
                     `user_role_type` varchar(20),
                     `user_created_time` timestamp,
                     `user_active` boolean
);

create table consulting(
                   `consulting_no` int NOT NULL AUTO_INCREMENT primary key,
                   `consulting_id` int,
                   `consulting_visit_date` timestamp,
                   `consulting_visit_place` varchar(100),
                   `consulting_visit_class` varchar(100),
                   `video_location` varchar(255),
                   CONSTRAINT user_no foreign key (consulting_id) REFERENCES user (user_no) on delete cascade on update cascade
);