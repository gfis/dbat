-- @(#) $Id$
-- Sample for the creation of a test database
-- Caution: modify the password!
-- 2017-04-19, GEorg Fischer: 
-- to be run with:
-- mysql -u root -p
-- source <this file>
-- quit
--
DROP   DATABASE IF EXISTS worddb;
CREATE DATABASE worddb DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE             worddb;
DROP   USER     IF EXISTS 'worduser'@'localhost';
CREATE USER               'worduser'@'localhost' IDENTIFIED BY '????'; 
GRANT  ALL ON worddb.* TO 'worduser'@'localhost';
COMMIT;
