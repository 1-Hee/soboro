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
                   `consulting_id` long,
                   `consulting_visitdate` timestamp,
                   `consulting_visitplace` varchar(100),
                   `consulting_visitclass` varchar(100),
                   CONSTRAINT user_no foreign key (consulting_no) REFERENCES user (user_no) on delete cascade on update cascade
);

create table video(
                  `video_no` int NOT NULL AUTO_INCREMENT primary key,
                  `video_location` varchar(255),
                  CONSTRAINT consulting_no foreign key (video_no) REFERENCES consulting (consulting_no) on delete cascade on update cascade
);
