CREATE DATABASE empresa;
USE empresa;

CREATE TABLE departamentos (
    id int PRIMARY KEY auto_increment,
    nombre varchar(255) not null
);

CREATE TABLE empleados (
    id int PRIMARY KEY auto_increment,
    nombre varchar(255) not null,
    id_departamento int not null,
    FOREIGN KEY (id_departamento) REFERENCES departamentos(id)
);

INSERT INTO departamentos (nombre) VALUES ('RRHH'), ('IT'), ('Contabilidad');

INSERT INTO empleados (nombre, id_departamento) VALUES 
('Ana', 1), ('Carlos', 2), ('Pedro', 3),('Luis', 1),('Maria', 2),('Laura', 3),('Juan', 1),('Sofia', 2),('Pablo', 3),('Elena', 1);

Select * from empleados;

CREATE TABLE userData (
    id int PRIMARY KEY auto_increment,
    username varchar(255) not null,
    passwd varchar(255) not null
);

CREATE TABLE loginLog (
    id int PRIMARY KEY auto_increment,
    user_id int not null,
    fecha_hora DATETIME not null,
    FOREIGN KEY (user_id) REFERENCES userData(id)
);

INSERT INTO userData (username, passwd) VALUES ('admin', 'admin');
INSERT INTO userData (username, passwd) VALUES ('usuario', '1234');


SELECT * FROM loginLog;

CREATE TABLE almacenes (
    id int PRIMARY KEY auto_increment,
    nombre varchar(255) not null,
    pais varchar(255) not null
);

CREATE TABLE productos (
    id int PRIMARY KEY auto_increment,
    nombre varchar(255) not null,
    precio decimal(10,2) not null
);

-- Un producto puede estar en varios almacenes con distinto stock
CREATE TABLE stock_almacen (
    id int PRIMARY KEY auto_increment,
    id_producto int not null,
    id_almacen int not null,
    stock int not null,
    FOREIGN KEY (id_producto) REFERENCES productos(id),
    FOREIGN KEY (id_almacen) REFERENCES almacenes(id)
);

INSERT INTO productos (nombre, precio) VALUES
('Manzana', 1.99),('Naranja', 1.50),('Platano', 0.99),('Tomate', 2.50),('Lechuga', 1.30);

INSERT INTO stock_almacen (id_producto, id_almacen, stock) VALUES
(1, 1, 100),(2, 1, 50),(3, 2, 75),(4, 2, 120),(5, 3, 200),(1, 3, 30),(2, 4, 60),(3, 5, 45);
Select * from almacenes;

INSERT INTO almacenes (nombre, pais) VALUES 
('Almacen Barcelona', 'España'),('Almacen Madrid', 'España'),
('Almacen Paris', 'Francia'),('Almacen Lyon', 'Francia'),
('Almacen Roma', 'Italia');


