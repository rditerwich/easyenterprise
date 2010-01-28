package agilexs.catalogxs.persistence

import metaphor.psm.rdbms.CreateSchema
import metaphor.psm.rdbms.IRdbmsPackage

[template AdditionalCreateSchemaSql(IRdbmsPackage package) joins CreateSchema.AdditionalSql]

/* make keys unique so no duplicates can be added */
ALTER TABLE "catalogxs"."product_groups_properties" ADD UNIQUE(properties_id,product_groups_id);
ALTER TABLE "catalogxs"."product_product_groups_products_product_group" ADD UNIQUE ("products_id", "product_groups_id");
ALTER TABLE "catalogxs"."product_group_children_parent_product_group" ADD UNIQUE ("children_id","parent_id");
ALTER TABLE "catalogxs"."product_group_default_property_values_property_value" ADD UNIQUE ("default_property_values_id","product_group_id");

INSERT INTO "catalogxs"."catalog" (name,id) VALUES ('staples',1);
INSERT INTO "catalogxs"."taxonomy" (name,catalog_id,id) VALUES ('webshop',1,1);
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,1,'product');
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,2,'Supplies');
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,3,'Inkt en hulpmiddelen');
INSERT INTO "catalogxs"."product_group" (catalog_id,id,name) VALUES (1,4,'K+E inkten en hulpmiddelen');

INSERT INTO "catalogxs"."product_group_children_parent_product_group" (children_id,parent_id) VALUES (2,1);
INSERT INTO "catalogxs"."product_group_children_parent_product_group" (children_id,parent_id) VALUES (3,2);
INSERT INTO "catalogxs"."product_group_children_parent_product_group" (children_id,parent_id) VALUES (4,3);

INSERT INTO "catalogxs"."product_group_taxonomy_top_level_product_groups_taxonomy" (taxonomy_id,top_level_product_groups_id) VALUES (1,2);

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

/*  Insert products */
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,1);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,2);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,3);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,4);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,5);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,6);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,7);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,8);
INSERT INTO "catalogxs"."product" (catalog_id,id) VALUES (1,9);

/* make relation of product to product group 1 */
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (1,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (2,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (3,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (4,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (5,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (6,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (7,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (8,1);
INSERT INTO "catalogxs"."product_product_groups_products_product_group" (products_id,product_groups_id) VALUES (9,1);

/* Insert default values for product Group property name*/
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (1,'en', 'Product',1);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (1,'en', 'Supplies',2);

INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (1,1);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (2,2);

/* specific Ink example properties */
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (1,'en', 'Inkt en hulpmiddelen',3);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (1,'en', 'K+E inkten en hulpmiddelen',4);

INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (3,3);
INSERT INTO "catalogxs"."product_group_default_property_values_property_value" (default_property_values_id,product_group_id) VALUES (4,4);

INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('Color','String', 22);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('Kleur Code','String',23);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('Weight','Integer', 24);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('PackingUnit','Integer', 25);
INSERT INTO "catalogxs"."property" (name,type_id,id) VALUES ('Unit','String', 26);

INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (20,3);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (21,3);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (22,3);
INSERT INTO "catalogxs"."product_groups_properties" (properties_id,product_groups_id) VALUES (23,3);

INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (3,'en', 'K+E inkt',7);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (23,'en', '1kg',5);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (24,'en', 'Canister',6);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (2,'en', '188078',10);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (22,'en', '39K HKS',11);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (21,'en', 'blue',12);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (2,'en', '190470',20);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (22,'en', '41N HKS',21);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (21,'en', 'blue',22);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (2,'en', '175778',30);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (22,'en', '51K* HKS',31);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (21,'en', 'blue',32);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (2,'en', '114272',40);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (22,'en', '43K HKS',41);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (21,'en', 'blue',42);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (2,'en', '3052083',50);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (22,'en', '275-C NP',51);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (21,'en', 'blue',52);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (2,'en', '193235',60);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (22,'en', '44N HKS',61);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (21,'en', 'blue',62);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (2,'en', '114280',70);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (22,'en', '47K HKS',71);
INSERT INTO "catalogxs"."property_value" (property_id,language,string_value,id) VALUES (21,'en', 'blue',72);

[/template]
