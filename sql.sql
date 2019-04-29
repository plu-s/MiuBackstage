SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `t_user`;
DROP TABLE IF EXISTS `t_user_register`;
DROP TABLE IF EXISTS `t_discuss`;
DROP TABLE IF EXISTS `t_discuss_do_like`;
DROP TABLE IF EXISTS `t_discuss_comment`;
DROP TABLE IF EXISTS `t_discuss_image`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `t_user` (
  `mail` varchar(30) NOT NULL,
  `name` varchar(20) NOT NULL,
  `passwords` varchar(20) NOT NULL,
  `state` varchar(16) NOT NULL,
  `pic_url`varchar(128),
  PRIMARY KEY (`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_user_register` (
  `mail` varchar(30) NOT NULL,
  `token` varchar(32) NOT NULL,
  PRIMARY KEY (`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_discuss` (
  `id` varchar (32) NOT NULL,
  `author_mail` varchar (32) NOT NULL,
  `title` varchar(32) NOT NULL,
  `content`  longtext NOT NULL,
  `create_date` timestamp NOT NULL,
  `like_count` int,
  `comment_count` int,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`author_mail`) REFERENCES t_user(`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_discuss_do_like` (
  `discuss_id` varchar (32) NOT NULL,
  `user_mail` varchar (32) NOT NULL,
  FOREIGN KEY (`discuss_id`) REFERENCES t_discuss(`id`),
  FOREIGN KEY (`user_mail`) REFERENCES `t_user`(`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_discuss_comment` (
  `discuss_id` varchar (32) NOT NULL,
  `user_mail` varchar (32) NOT NULL,
  `content` varchar(256) NOT NULL,
  `create_date` timestamp NOT NULL,
  FOREIGN KEY (`discuss_id`) REFERENCES `t_discuss`(`id`),
  FOREIGN KEY (`user_mail`) REFERENCES `t_user`(`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_discuss_image` (
  `discuss_id` varchar (32) NOT NULL,
  `image_url` varchar(256) NOT NULL,
  FOREIGN KEY (`discuss_id`) REFERENCES `t_discuss`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;