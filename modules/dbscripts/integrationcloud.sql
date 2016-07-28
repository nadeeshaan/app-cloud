-------------------------------------------------------------------------------------------------
-- adding integration cloud
-------------------------------------------------------------------------------------------------

INSERT INTO `AC_CLOUD` (`id`, `name`) VALUES
(2, 'integration-cloud');

INSERT INTO `AC_CLOUD_APP_TYPE` (`cloud_id`, `app_type_id`) VALUES
(2, 6);

-------------------------------------------------------------------------------------------------
-- subscription plan per cloud
-------------------------------------------------------------------------------------------------

INSERT INTO AC_SUBSCRIPTION_PLANS (PLAN_ID, PLAN_NAME, MAX_APPLICATIONS, MAX_DATABASES, CLOUD_ID) VALUES
(3, 'FREE', 3, 3, 2),
(4, 'PAID', 10, 6, 2);