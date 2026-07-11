-- --------------------- 
-- datos falsos creado con mockaroo
-- habría que tocar los de cliente y encargo para que cuadren los datos

insert into coche (id, marca, modelo, precio, stock) values (1, 'Ford', 'E250', 115692, 25);
insert into coche (id, marca, modelo, precio, stock) values (2, 'Honda', 'Accord Crosstour', 157481, 2);
insert into coche (id, marca, modelo, precio, stock) values (3, 'Volkswagen', 'Passat', 114097, 17);
insert into coche (id, marca, modelo, precio, stock) values (4, 'Mazda', 'CX-7', 106057, 23);
insert into coche (id, marca, modelo, precio, stock) values (5, 'Dodge', 'Durango', 116763, 8);
insert into coche (id, marca, modelo, precio, stock) values (6, 'Chevrolet', 'Sportvan G30', 149298, 8);
insert into coche (id, marca, modelo, precio, stock) values (7, 'Ford', 'Econoline E350', 121882, 0);
insert into coche (id, marca, modelo, precio, stock) values (8, 'Dodge', 'Ram Van 2500', 166965, 2);
insert into coche (id, marca, modelo, precio, stock) values (9, 'Honda', 'Odyssey', 165497, 16);
insert into coche (id, marca, modelo, precio, stock) values (10, 'Audi', 'V8', 100964, 25);


-- 


insert into cliente (id, nombre, ciudad, gastado) values (1, 'Marci', 'Mapinrea', 2763301.35);
insert into cliente (id, nombre, ciudad, gastado) values (2, 'Emmey', 'Sheshan', 6969105.44);
insert into cliente (id, nombre, ciudad, gastado) values (3, 'Ros', 'Viedma', 8060845.46);
insert into cliente (id, nombre, ciudad, gastado) values (4, 'Ami', 'Coronel Suárez', 3435009.99);
insert into cliente (id, nombre, ciudad, gastado) values (5, 'Tobye', 'São João de Caparica', 248104.70);

--

insert into encargo (coche_id, cliente_id, cantidad, fecha) values (1, 1, 16, '2016/01/16');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (2, 2, 1, '2017/08/13');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (3, 3, 9, '2020/06/26');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (4, 4, 32, '2019/11/07');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (5, 5, 40, '2016/01/24');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (6, 1, 4, '2018/07/18');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (7, 2, 60, '2018/01/18');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (8, 3, 78, '2017/05/12');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (9, 4, 95, '2019/03/26');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (10, 5, 11, '2016/02/27');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (1, 1, 23, '2018/08/15');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (2, 2, 90, '2017/11/06');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (3, 3, 12, '2019/06/07');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (4, 4, 100, '2018/10/23');
insert into encargo (coche_id, cliente_id, cantidad, fecha) values (5, 5, 100, '2018/04/11');
