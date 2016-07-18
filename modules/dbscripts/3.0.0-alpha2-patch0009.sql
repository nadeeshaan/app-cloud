-- to make identical local setup database with production database
delete from AC_RUNTIME_CONTAINER_SPECIFICATIONS where id = 2 and CON_SPEC_ID = 1;
insert into AC_RUNTIME_CONTAINER_SPECIFICATIONS values(5,4);
insert into AC_RUNTIME_CONTAINER_SPECIFICATIONS values(6,4);

