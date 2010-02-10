package agilexs.catalogxs.persistence

import metaphor.psm.rdbms.CreateSchema
import metaphor.psm.rdbms.IRdbmsPackage

[template AdditionalCreateSchemaSql(IRdbmsPackage package) joins CreateSchema.AdditionalSql]

/* make keys unique so no duplicates can be added */
ALTER TABLE catalogxs.product_product_groups_products_product_group ADD UNIQUE (products_id, product_groups_id);
ALTER TABLE catalogxs.product_group_children_parents_product_group ADD UNIQUE (children_id,parents_id);
/*ALTER TABLE catalogxs.product_group_default_property_values_property_value ADD UNIQUE (default_property_values_id,product_group_id);*/

/* Catalog */
INSERT INTO catalogxs.catalog (name,id) VALUES ('staples',1);

/* Product Group Properties */
INSERT INTO catalogxs.property (id,owning_catalog_id,name,type_id) VALUES (1000,1,'Name','String');
INSERT INTO catalogxs.property (id,owning_catalog_id,name,type_id) VALUES (1001,1,'Description', 'String');
INSERT INTO catalogxs.property (id,owning_catalog_id,name,type_id) VALUES (1002,1,'Image','Media');

/* Product Groups */
INSERT INTO catalogxs.product_group (catalog_id,id,name) VALUES (1,2000,'product');
INSERT INTO catalogxs.product_group (catalog_id,id,name) VALUES (1,2001,'supplies');
INSERT INTO catalogxs.product_group (catalog_id,id,name) VALUES (1,2002,'ink');
INSERT INTO catalogxs.product_group (catalog_id,id,name) VALUES (1,2003,'keink');
INSERT INTO catalogxs.product_group (catalog_id,id,name) VALUES (1,2004,'products');

/* Product Group Property Values */
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10000, 1000, 2000, 'en', 'Root');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10001, 1000, 2001, 'en', 'Supplies');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10002, 1001, 2001, 'en', 'Contains supplies of all sorts');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10003, 1000, 2002, 'en', 'Ink');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10004, 1001, 2002, 'en', 'Ink and ink supplies');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10005, 1000, 2003, 'en', 'K+E Ink');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10006, 1001, 2003, 'en', 'K+E ink and ink supplies');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10007, 1000, 2004, 'en', 'Products');
INSERT INTO catalogxs.property_value (id,property_id,group_property_values_opposite_id,language,string_value) VALUES (10008, 1001, 2004, 'en', 'All products');

/* Product Group hierarchy */
INSERT INTO catalogxs.product_group_children_parents_product_group (children_id,parents_id) VALUES (2001,2000);
INSERT INTO catalogxs.product_group_children_parents_product_group (children_id,parents_id) VALUES (2002,2001);
INSERT INTO catalogxs.product_group_children_parents_product_group (children_id,parents_id) VALUES (2003,2002);
INSERT INTO catalogxs.product_group_children_parents_product_group (children_id,parents_id) VALUES (2001,2004);
INSERT INTO catalogxs.product_group_children_parents_product_group (children_id,parents_id) VALUES (2002,2004);

/* Root properties */
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1010,'ArticleNumber','String',2000);
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1011,'Description','String',2000);
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1012,'Image','Media',2000);
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1013,'PriceOld','Money',2000);
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1014,'Price','Money',2000);

/* Ink example properties */
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1020,'Color','String', 2002);
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1021,'Kleur Code','String',2002);
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1022,'Weight','Integer', 2002);
INSERT INTO catalogxs.property (id,name,type_id,product_group_id) VALUES (1023,'PackingUnit','Integer', 2002);

/*  Insert example Ink products */
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5001);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5002);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5003);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5004);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5005);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5006);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5007);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5008);
INSERT INTO catalogxs.product (catalog_id,id) VALUES (1,5009);

/* make relation of product to product group 2002 & 2003 */
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5001,2002);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5002,2002);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5003,2002);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5004,2002);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5005,2002);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5006,2002);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5007,2002);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5008,2003);
INSERT INTO catalogxs.product_product_groups_products_product_group (products_id,product_groups_id) VALUES (5009,2003);

/* Product 5001 Property Values */
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10010, 1010, 5001, 'en', '188078');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10011, 1020, 5001, 'en', 'blue');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10012, 1021, 5001, 'en', '39K HKS');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10013, 1023, 5001, 'en', 'Rol');

/* Product 5002 Property Values */
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10020, 1010, 5002, 'en', '128116');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10021, 1020, 5002, 'en', 'red');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10022, 1021, 5002, 'en', '275-C NP');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10023, 1023, 5002, 'en', 'Rol');

/* Product 5003 Property Values */
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10030, 1010, 5003, 'en', '175778');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10031, 1020, 5003, 'en', 'yellow');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10032, 1021, 5003, 'en', '51K* HKS');
INSERT INTO catalogxs.property_value (id,property_id,product_id,language,string_value) VALUES (10033, 1023, 5003, 'en', 'Blik');

/*
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (2,'en', '188078',10,1);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (22,'en', '39K HKS',11,1);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',12,1);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (2,'en', '190470',20,2);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (22,'en', '41N HKS',21,2);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',22,2);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (2,'en', '175778',30,3);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (22,'en', '51K* HKS',31,3);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',32,3);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (2,'en', '114272',40,4);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (22,'en', '43K HKS',41,4);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',42,4);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (2,'en', '3052083',50,5);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (22,'en', '275-C NP',51,5);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',52,5);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (2,'en', '193235',60,5);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (22,'en', '44N HKS',61,6);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',62,6);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (2,'en', '114280',70,7);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (22,'en', '47K HKS',71,7);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',72,7);
*/
/* Insert default values for product Group property name*/
/*INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (1,'en','Product',1);
INSERT INTO catalogxs.product_group_default_property_values_property_value (default_property_values_id,product_group_id) VALUES (1,1);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (1,'en','Supplies',2);
INSERT INTO catalogxs.product_group_default_property_values_property_value (default_property_values_id,product_group_id) VALUES (2,2);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (1,'en','Ink and Ink supplies',3);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id, product_id) VALUES (1,'en','K+E Ink and Ink supplies',4);
INSERT INTO catalogxs.product_group_default_property_values_property_value (default_property_values_id,product_group_id) VALUES (3,3);
INSERT INTO catalogxs.product_group_default_property_values_property_value (default_property_values_id,product_group_id) VALUES (4,4);

INSERT INTO catalogxs.property_value (property_id,language,string_value,id) VALUES (3,'en', 'K+E inkt',7);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id) VALUES (23,'en', '1kg',5);
INSERT INTO catalogxs.property_value (property_id,language,string_value,id) VALUES (24,'en', 'Canister',6);

INSERT INTO catalogxs.product_group_default_property_values_property_value (default_property_values_id,product_group_id) VALUES (5,3);
INSERT INTO catalogxs.product_group_default_property_values_property_value (default_property_values_id,product_group_id) VALUES (6,3);
INSERT INTO catalogxs.product_group_default_property_values_property_value (default_property_values_id,product_group_id) VALUES (7,3);
*/


/* View */
INSERT INTO catalogxs.catalog_view (name,catalog_id,id) VALUES ('webshop',1,1);
INSERT INTO catalogxs.catalog_view_top_level_product_groups_view_product_group (view_id,top_level_product_groups_id) VALUES (1,2004);

/* Promotion examples */
INSERT INTO catalogxs.promotion (type,start_date,end_date,id,product_id,price,price_currency,volume_discount,view_id) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-20','2011-01-20',1,5001,100,'EUR',3,1);
INSERT INTO catalogxs.promotion (type,start_date,end_date,id,product_id,price,price_currency,volume_discount,view_id) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-22','2011-02-20',2,5001,700,'EUR',4,1);
INSERT INTO catalogxs.promotion (type,start_date,end_date,id,product_id,price,price_currency,volume_discount,view_id) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-10','2011-03-10',3,5002,80,'EUR',5,1);
INSERT INTO catalogxs.promotion (type,start_date,end_date,id,product_id,price,price_currency,volume_discount,view_id) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2009-12-30','2011-04-02',4,5003,90,'EUR',6,1);
INSERT INTO catalogxs.promotion (type,start_date,end_date,id,product_id,price,price_currency,volume_discount,view_id) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-05','2011-05-05',5,5004,110,'EUR',7,1);

[/template]
