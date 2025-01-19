-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema blodged
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema blodged
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `blodged` DEFAULT CHARACTER SET utf8 ;
USE `blodged` ;

-- -----------------------------------------------------
-- Table `blodged`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blodged`.`users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `bio` TEXT NULL DEFAULT NULL,
  `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_private` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username` (`username` ASC),
  UNIQUE INDEX `email` (`email` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `blodged`.`follows`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blodged`.`follows` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_following` INT(11) NOT NULL,
  `user_followed` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_follows_users_idx` (`user_following` ASC),
  INDEX `fk_follows_users1_idx` (`user_followed` ASC),
  CONSTRAINT `fk_follows_users`
    FOREIGN KEY (`user_following`)
    REFERENCES `blodged`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_follows_users1`
    FOREIGN KEY (`user_followed`)
    REFERENCES `blodged`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `blodged`.`posts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blodged`.`posts` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `post_content` TEXT NOT NULL,
  `post_user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `blodged`.`likes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blodged`.`likes` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `post_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_likes_users1_idx` (`user_id` ASC),
  INDEX `fk_likes_posts1_idx` (`post_id` ASC),
  CONSTRAINT `fk_likes_posts1`
    FOREIGN KEY (`post_id`)
    REFERENCES `blodged`.`posts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_likes_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `blodged`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `blodged`.`replies`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blodged`.`replies` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  `post_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_replies_posts1_idx` (`post_id` ASC),
  INDEX `fk_replies_users1_idx` (`user_id` ASC),
  CONSTRAINT `fk_replies_posts1`
    FOREIGN KEY (`post_id`)
    REFERENCES `blodged`.`posts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_replies_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `blodged`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
