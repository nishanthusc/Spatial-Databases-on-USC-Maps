DROP INDEX STUD_IDX;

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='TEST1';

DROP INDEX BUILDING_IDX;

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='BUILDINGS1';

DROP INDEX AS_IDX;

DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='ANNOUNCEMENTSYSTEMS';

DROP TABLE TEST1;

DROP TABLE BUILDINGS1;

DROP TABLE ANNOUNCEMENTSYSTEMS;