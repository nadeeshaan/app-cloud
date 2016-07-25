-- to make identical local setup database with production database
delete from AC_RUNTIME_CONTAINER_SPECIFICATIONS where id = 2 and CON_SPEC_ID = 1;
insert into AC_RUNTIME_CONTAINER_SPECIFICATIONS values(5,4);
insert into AC_RUNTIME_CONTAINER_SPECIFICATIONS values(6,4);

-- app types per cloud
CREATE TABLE IF NOT EXISTS `AppCloudDB`.`AC_CLOUD` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`name`))
ENGINE = InnoDB;

INSERT INTO `AC_CLOUD` (`id`, `name`) VALUES
(1, 'app-cloud');

CREATE TABLE IF NOT EXISTS `AppCloudDB`.`AC_CLOUD_APP_TYPE` (
  `cloud_id` INT NOT NULL,
  `app_type_id` INT NOT NULL,
  CONSTRAINT `fk_cloud_has_cloudAppType_cloud`
    FOREIGN KEY (`cloud_id`)
    REFERENCES `AppCloudDB`.`AC_CLOUD` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cloud_has_cloudAppType_appType`
    FOREIGN KEY (`app_type_id`)
    REFERENCES `AppCloudDB`.`AC_APP_TYPE` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

INSERT INTO `AC_CLOUD_APP_TYPE` (`cloud_id`, `app_type_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5);

