CREATE TABLE IF NOT EXISTS category (
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) UNIQUE NOT NULL ,
    req_name varchar(255) UNIQUE NOT NULL ,
    deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS banner (
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) UNIQUE NOT NULL ,
    price dec(8,2) NOT NULL,
    category_id int NOT NULL ,
    content text NOT NULL ,
    deleted boolean NOT NULL DEFAULT false ,
    CONSTRAINT banner_category_fk FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT banner_price_positive_value CHECK ( price >= 0.0 )
);

CREATE TABLE IF NOT EXISTS request (
    id int PRIMARY KEY AUTO_INCREMENT,
    banner_id int NOT NULL ,
    user_agent text ,
    ip_address varchar(255) ,
    date datetime NOT NULL ,
    CONSTRAINT request_banner_fk FOREIGN KEY (banner_id) REFERENCES banner(id)
);
