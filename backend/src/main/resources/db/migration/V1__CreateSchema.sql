CREATE TABLE IF NOT EXISTS category (
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) UNIQUE NOT NULL ,
    req_name varchar(255) UNIQUE NOT NULL ,
    deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS banner (
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) UNIQUE NOT NULL ,
    price dec(8,2) NOT NULL CHECK ( price >= 0.0 ) ,
    category_id int NOT NULL REFERENCES category(id) ,
    content text NOT NULL ,
    deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS request (
    id int PRIMARY KEY ,
    banner_id int NOT NULL REFERENCES banner(id) ,
    user_agent text ,
    ip_address varchar(255) ,
    date datetime NOT NULL
);
