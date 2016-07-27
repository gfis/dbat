-- phpMyAdmin SQL Dump
-- version 2.8.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 13. August 2006 um 13:26
-- Server Version: 5.0.21
-- PHP-Version: 5.1.4
--
-- Datenbank: 'worddb'
--
DROP   DATABASE worddb;
CREATE DATABASE worddb DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE worddb;
REVOKE ALL PRIVILEGES ON 'worddb'.* FROM 'worduser'@'localhost';
GRANT  ALL PRIVILEGES ON 'worddb'.* FROM 'worduser'@'localhost' WITH GRANT OPTION;
COMMIT;
