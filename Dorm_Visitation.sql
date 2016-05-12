/**
 * Dorm_Visitation.sql
 * Description: This file contains all database tables. The information will be stored in these tables.
 * 
 * Date: 03/16/16
 * @author Hanif Mirza
 *
 */

# Before creating new tables, drop all the tables if they exist
SET FOREIGN_KEY_CHECKS = 0;

drop table if exists RD;
drop table if exists Employee;
drop table if exists RA;
drop table if exists DM;
drop table if exists Resident;
drop table if exists Visitation_Detail;
drop table if exists Lockout_Detail;
drop table if exists Banned_Guest;
drop table if exists Log_Detail;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE RD(
    userID           VARCHAR(20),
    password         VARCHAR(15),
    first_name       VARCHAR(20),
    last_name        VARCHAR(20),
    email            VARCHAR(50),
    phone            VARCHAR(20),

    PRIMARY KEY (userID)
);


CREATE TABLE Employee(
    userID           VARCHAR(20),
    first_name       VARCHAR(20),
    last_name        VARCHAR(20),
    email            VARCHAR(50),
    phone            VARCHAR(20),
    userID_RD        VARCHAR(20) NOT NULL,

    PRIMARY KEY (userID),
    
    FOREIGN KEY (userID_RD) REFERENCES RD(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE RA(
    userID                    VARCHAR(20),
    password                  VARCHAR(15),

    PRIMARY KEY(userID),
    
    FOREIGN KEY(userID) REFERENCES Employee(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE DM(
    userID                    VARCHAR(20),
    password                  VARCHAR(15),

    PRIMARY KEY(userID),
    
    FOREIGN KEY(userID) REFERENCES Employee(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Resident(
    userID           	VARCHAR(20),
    first_name       	VARCHAR(20),
    last_name        	VARCHAR(20),
    gender				VARCHAR(10),
    email            	VARCHAR(50),
    phone            	VARCHAR(20),
    dorm_name		 	VARCHAR(15),
    room_number		 	VARCHAR(5),
    number_of_lockouts	INT,

    PRIMARY KEY (userID)
);


CREATE TABLE Visitation_Detail(
    visitationID             INT NOT NULL AUTO_INCREMENT,
    guest_name				 VARCHAR(20),
    guest_age				 INT,    
    guest_ID_type			 VARCHAR(10),
    visitation_date          DATE,
    time_in             	 TIME,
    time_out             	 TIME,
    overnight_status		 VARCHAR(10),
    empID					 VARCHAR(20) NOT NULL,
    residentID			     VARCHAR(20) NOT NULL,
    
    PRIMARY KEY(visitationID),
    
    FOREIGN KEY(empID) REFERENCES Employee(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY(residentID) REFERENCES Resident(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Lockout_Detail(
    lockoutID                INT NOT NULL AUTO_INCREMENT,
    lockout_date             DATE,
    lockout_time             TIME,
    ra_name  			 	 VARCHAR(20),
    empID					 VARCHAR(20) NOT NULL,
    residentID			     VARCHAR(20) NOT NULL,
    
    PRIMARY KEY(lockoutID),
    
    FOREIGN KEY(empID) REFERENCES Employee(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY(residentID) REFERENCES Resident(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Banned_Guest(
    guestID          INT NOT NULL AUTO_INCREMENT,
    guest_name		 VARCHAR(20),
    gender			 VARCHAR(10),
    dorm_name		 VARCHAR(20),
    category		 VARCHAR(20),
	comments         VARCHAR(50),
    userID_RD        VARCHAR(20) NOT NULL,

    PRIMARY KEY (guestID),
    
    FOREIGN KEY (userID_RD) REFERENCES RD(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Log_Detail(
    logID                    INT NOT NULL AUTO_INCREMENT,
    log_date             	 DATE,
    login_time               TIME,
    logout_time              TIME,
    empID					 VARCHAR(20) NOT NULL,
    
    PRIMARY KEY(logID),
    
    FOREIGN KEY(empID) REFERENCES Employee(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

# Insert Resident Director's (admin) information to the RD table
INSERT INTO RD(userID,password,first_name,last_name,email,phone) VALUES ( 'admin','password','Alexis','Keller','alexis.keller@fairmontstate.edu','304-367-4789');


