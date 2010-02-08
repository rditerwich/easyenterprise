package agilexs.catalogxs.persistence

import metaphor.psm.rdbms.CreateSchema
import metaphor.psm.rdbms.IRdbmsPackage

[template AdditionalCreateSchemaSql(IRdbmsPackage package) joins CreateSchema.AdditionalSql]

/* make keys unique so no duplicates can be added */
ALTER TABLE "catalogxs"."product_groups_properties" ADD UNIQUE(properties_id,product_groups_id);
ALTER TABLE "catalogxs"."product_product_groups_products_product_group" ADD UNIQUE ("products_id", "product_groups_id");
ALTER TABLE "catalogxs"."product_group_children_parents_product_group" ADD UNIQUE ("children_id","parents_id");
ALTER TABLE "catalogxs"."product_group_default_property_values_property_value" ADD UNIQUE ("default_property_values_id","product_group_id");

INSERT INTO "catalogxs"."catalog" (name,id) VALUES ('staples',1);
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,1,'product');
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,2,'Supplies');
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,3,'Inkt en hulpmiddelen');
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,4,'K+E inkten en hulpmiddelen');

INSERT INTO "catalogxs"."product_group_children_parents_product_group" (children_id,parents_id) VALUES (2,1);
INSERT INTO "catalogxs"."product_group_children_parents_product_group" (children_id,parents_id) VALUES (3,2);
INSERT INTO "catalogxs"."product_group_children_parents_product_group" (children_id,parents_id) VALUES (4,3);

/* CatalogView */
INSERT INTO "catalogxs"."catalog_view" (name,catalog_id,id) VALUES ('webshop',1,1);
INSERT INTO "catalogxs"."catalog_view_top_level_product_groups_view_product_group" (view_id,top_level_product_groups_id) VALUES (1,2);

/* Properties */
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('ProductGroupName','String',1);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('ArticleNumber','String',2);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('ProductDescription','String',3);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('ProductImage','Media',4);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('ProductPrice','Money',5);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('ProductPriceNew','Money',6);

INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (1,1);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (2,1);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (3,1);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (4,1);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (5,1);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (6,1);

/* labels for product groups */
INSERT INTO "catalogxs"."label" (language,label,id,property_id,enum_value_id) VALUES ('en','Supplies',1,1,null);
INSERT INTO "catalogxs"."label" (language,label,id,property_id,enum_value_id) VALUES ('en','Ink and ink supplies',2,1,null);
INSERT INTO "catalogxs"."label" (language,label,id,property_id,enum_value_id) VALUES ('en','K+E ink and ink supplies',3,1,null);

INSERT INTO "catalogxs"."product_group_labels_label" (labels_id,product_group_id) VALUES (1,2);
INSERT INTO "catalogxs"."product_group_labels_label" (labels_id,product_group_id) VALUES (2,3);
INSERT INTO "catalogxs"."product_group_labels_label" (labels_id,product_group_id) VALUES (3,4);

/*  Insert example Ink products */
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,1);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,2);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,3);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,4);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,5);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,6);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,7);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,8);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,9);

/* make relation of product to product group 4 */
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (1,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (2,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (3,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (4,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (5,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (6,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (7,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (8,4);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (9,4);

/* Insert default values for product Group property name*/
/*INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (1,'en','Product',1);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (1,1);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (1,'en','Supplies',2);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (2,2);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (1,'en','Ink and Ink supplies',3);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (1,'en','K+E Ink and Ink supplies',4);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (3,3);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (4,4);
*/

/* specific Ink example properties */
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('Color','String', 21);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('Kleur Code','String',22);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('Weight','Integer', 23);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('PackingUnit','Integer', 24);

INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (21,3);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (22,3);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (23,3);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (24,3);

INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (3,'en', 'K+E inkt',7);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (23,'en', '1kg',5);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (24,'en', 'Canister',6);

INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (5,3);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (6,3);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (7,3);

INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (2,'en', '188078',10,1);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (22,'en', '39K HKS',11,1);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',12,1);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (2,'en', '190470',20,2);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (22,'en', '41N HKS',21,2);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',22,2);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (2,'en', '175778',30,3);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (22,'en', '51K* HKS',31,3);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',32,3);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (2,'en', '114272',40,4);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (22,'en', '43K HKS',41,4);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',42,4);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (2,'en', '3052083',50,5);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (22,'en', '275-C NP',51,5);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',52,5);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (2,'en', '193235',60,5);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (22,'en', '44N HKS',61,6);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',62,6);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (2,'en', '114280',70,7);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (22,'en', '47K HKS',71,7);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id, product_id) VALUES (21,'en', 'blue',72,7);

/* Promotion examples */
INSERT INTO "catalogxs"."promotion" (type,start_date,end_date,id,product_id,price,price_currency,volume_discount) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-20','2011-01-20',1,1,100,'EUR',3);
INSERT INTO "catalogxs"."promotion" (type,start_date,end_date,id,product_id,price,price_currency,volume_discount) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-22','2011-02-20',2,2,700,'EUR',4);
INSERT INTO "catalogxs"."promotion" (type,start_date,end_date,id,product_id,price,price_currency,volume_discount) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-10','2011-03-10',3,3,80,'EUR',5);
INSERT INTO "catalogxs"."promotion" (type,start_date,end_date,id,product_id,price,price_currency,volume_discount) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2009-12-30','2011-04-02',4,4,90,'EUR',6);
INSERT INTO "catalogxs"."promotion" (type,start_date,end_date,id,product_id,price,price_currency,volume_discount) VALUES ('agilexs.catalogxs.jpa.catalog.VolumeDiscountPromotion','2010-01-05','2011-05-05',5,5,110,'EUR',7);

[/template]
