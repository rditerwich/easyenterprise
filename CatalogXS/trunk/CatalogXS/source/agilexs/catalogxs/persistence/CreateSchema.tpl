package agilexs.catalogxs.persistence

import metaphor.psm.rdbms.CreateSchema
import metaphor.psm.rdbms.IRdbmsPackage

[template AdditionalCreateSchemaSql(IRdbmsPackage package) joins CreateSchema.AdditionalSql]

/* make keys unique so no duplicates can be added */
/*ALTER TABLE catalogxs.productproductgroupsproductsproductgroup ADD UNIQUE (products_id, productgroups_id);*/
/*ALTER TABLE catalogxs.productgroupchildrenparentsproductgroup ADD UNIQUE (children_id,parents_id);*/
/*ALTER TABLE catalogxs.productgroupdefaultpropertyvaluespropertyvalue ADD UNIQUE (defaultpropertyvalues_id,item_id);*/

/* Catalog */
INSERT INTO catalogxs.catalog (name,id) VALUES ('staples',1);

/* Product Group Properties */
INSERT INTO catalogxs.property (id,owningcatalog_id,name,type_id) VALUES (1000,1,'Name','String');
INSERT INTO catalogxs.property (id,owningcatalog_id,name,type_id) VALUES (1001,1,'Description', 'String');
INSERT INTO catalogxs.property (id,owningcatalog_id,name,type_id) VALUES (1002,1,'Image','Media');

/* Product Groups */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2000,'product','catalog.productgroup');
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2001,'supplies','catalog.productgroup');
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2002,'ink','catalog.productgroup');
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2003,'keink','catalog.productgroup');
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2004,'products','catalog.productgroup');

/* Product Group Property Values */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10000, 1000, 2000, 'en', 'Root');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10001, 1000, 2001, 'en', 'Supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10002, 1001, 2001, 'en', 'Contains supplies of all sorts');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10003, 1000, 2002, 'en', 'Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10004, 1001, 2002, 'en', 'Ink and ink supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10005, 1000, 2003, 'en', 'K+E Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10006, 1001, 2003, 'en', 'K+E ink and ink supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10007, 1000, 2004, 'en', 'Products');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10008, 1001, 2004, 'en', 'All products');

/* Root properties */
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1010,'ArticleNumber','String',2000);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1011,'Description','String',2000);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1012,'Image','Media',2000);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1013,'PriceOld','Money',2000);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1014,'Price','Money',2000);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1015,'Synopsis','String',2000);

/* Ink example properties */
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1020,'Color','String', 2002);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1021,'Kleur Code','String',2002);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1022,'Weight','Integer', 2002);
INSERT INTO catalogxs.property (id,name,type_id,item_id) VALUES (1023,'PackingUnit','Integer', 2002);

/*  Insert example Ink products */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5001,'catalog.product');
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5002,'catalog.product');
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5003,'catalog.product');

/* make relation of product to product group 2002 & 2003 */
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5001,2002);
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5002,2002);
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5003,2002);

/* Product group Saphira Supplies */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2005,'saphirasupplies','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200501, 1000, 2005, 'en', 'Saphira Supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200502, 1001, 2005, 'en', 'Saphira offers you a w_ide range of consumables to cover all your needs - from prepress to postpress. Our experts prov_ide technical and application support for Saphira products, and advise you on how to use them most effectively.');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200503, 1002, 2005, 'en', null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200504, 1001, 2005, 'de', 'He_idelberg bietet Ihnen mit Saphira eine vielfaltige Auswahl an Verbrauchsmaterialien, die Ihren Bedarf von der Druckvorstufe bis hin zur Weiterverarbeitung abdecken. Unsere Experten stehen Ihnen ausserdem mit Rat und Tat zur Seite, wenn es um technische oder andere Fragen zur Anwendung und Leistungsfahigkeit der Saphira Produkte geht.');

/* Product group Saphira Supplies Proofing */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2006,'saphirasuppliesproofing','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200621, 1000, 2006, 'en', 'Saphira Proofing Supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200622, 1001, 2006, 'en', 'Saphira offers you a w_ide range of consumables to cover all your needs - from prepress to postpress. Our experts prov_ide technical and application support for Saphira products, and advise you on how to use them most effectively.');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200623, 1002, 2006, 'en', null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200624, 1001, 2006, 'de', 'He_idelberg bietet Ihnen mit Saphira eine vielfaltige Auswahl an Verbrauchsmaterialien, die Ihren Bedarf von der Druckvorstufe bis hin zur Weiterverarbeitung abdecken. Unsere Experten stehen Ihnen ausserdem mit Rat und Tat zur Seite, wenn es um technische oder andere Fragen zur Anwendung und Leistungsfahigkeit der Saphira Produkte geht.');

/* Product group Saphira Supplies Ink */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2007,'saphirasuppliesink','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200711, 1000, 2007, 'en', 'Saphira Inks');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200712, 1001, 2007, 'en', 'Saphira offers you a w_ide range of consumables to cover all your needs - from prepress to postpress. Our experts prov_ide technical and application support for Saphira products, and advise you on how to use them most effectively.');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200713, 1002, 2007, 'en', null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200714, 1000, 2007, 'de', 'Saphira Lacke');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200715, 1001, 2007, 'de', 'He_idelberg bietet Ihnen mit Saphira eine vielfaltige Auswahl an Verbrauchsmaterialien, die Ihren Bedarf von der Druckvorstufe bis hin zur Weiterverarbeitung abdecken. Unsere Experten stehen Ihnen ausserdem mit Rat und Tat zur Seite, wenn es um technische oder andere Fragen zur Anwendung und Leistungsfahigkeit der Saphira Produkte geht.');

/* Product group Brands */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2008,'brands','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200801, 1000, 2008, 'en', 'Brands');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200802, 1001, 2008, 'en', 'Products grouped by brand.');

/* Product group Brand: Others */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2009,'otherbrands','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200901, 1000, 2009, 'en', 'Other');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200902, 1001, 2009, 'en', 'Other brands.');

/* Product group Brand: Saphira */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2010,'saphira','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201001, 1000, 2010, 'en', 'Saphira');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201002, 1001, 2010, 'en', 'Saphira Products');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201003, 1002, 2010, 'en', null);

/* Product group Machines */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2020,'machines','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202001, 1000, 2020, 'en', 'Machines');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202002, 1001, 2020, 'en', 'Products by machine');

/* Product group Machine Speedmaster XL 105 */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2021,'speedmasterxl105','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202101, 1000, 2021, 'en', 'Speedmaster XL 105');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202102, 1001, 2021, 'en', 'Products suitable for the He_idelberg Speedmaster XL 105');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202103, 1002, 2021, 'en', null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202104, 1015, 2021, 'en', 'Door een vergaand automatiseringsniveau, 100% volautomatische snelhe_idscompensatie, geavanceerde toepassingen voor een optimaal veltransport en hoge productiesnelheden van 18.000 druks per uur, zet de XL 105 ook in de praktijk een nieuwe categorie neer. Voor de hoge mate van stabiliteit die vereist is bij het produceren van topdrukwerk bij hoge snelheden, heeft He_idelberg een geheel nieuw platform ontworpen als basis voor de XL 105.');

/* Product group Machine He_idelberg Speedmaster XL 145 */
INSERT INTO catalogxs.item (catalog_id,id,name,type) VALUES (1,2022,'speedmasterxl145','catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202201, 1000, 2022, 'en', 'Speedmaster XL 145');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202202, 1001, 2022, 'en', 'Products suitable for the He_idelberg Speedmaster XL 145');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202203, 1002, 2022, 'en', null);

/* Product Group hierarchy */
INSERT INTO catalogxs.item_parents (parents_id,children_id) VALUES

/* Root */ 
(2000,2001),

/* Supplies */
(2001,2002),
(2001,2006),
(2001,2007),

/* Ink */
(2002,2003),
(2002,2007),

/* Saphira */
(2005,2006),
(2005,2007),

/* Products */
(2004,2001),
(2004,2002),

/* Brands */
(2008,2009),
(2008,2010),

/* Brand Other */
(2009,2003),

/* Brand Saphira */
(2010,2006),
(2010,2007),

/* Machines */
(2020,2021),
(2020,2022);

/* Product 5001 Property Values */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10010, 1010, 5001, 'en', '188078');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10011, 1011, 5001, 'en', 'Blue Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (10012, 1014, 5001, 'en', 1295, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10013, 1020, 5001, 'en', 'blue');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10014, 1021, 5001, 'en', '39K HKS');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10015, 1023, 5001, 'en', 'Rol');

/* Product 5002 Property Values */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10020, 1010, 5002, 'en', '128116');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10021, 1011, 5002, 'en', 'Red Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (10022, 1014, 5002, 'en', 2295, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10023, 1020, 5002, 'en', 'red');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10024, 1021, 5002, 'en', '275-C NP');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10025, 1023, 5002, 'en', 'Rol');

/* Product 5003 Property Values */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10030, 1010, 5003, 'en', '175778');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10031, 1011, 5003, 'en', 'Yellow Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (10032, 1014, 5003, 'en', 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10033, 1020, 5003, 'en', 'yellow');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10034, 1021, 5003, 'en', '51K* HKS');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10035, 1023, 5003, 'en', 'Blik');

/* Product 5010 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5010,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5010,2007), (5010,2021);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501001, 1010, 5010, 'en', 'SU18364565');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501002, 1011, 5010, 'en', 'Saphira Anicolor Ink S Black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501003, 1014, 5010, 'en', 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501004, 1020, 5010, 'en', 'black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501005, 1023, 5010, 'en', 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501006, 1015, 5010, 'en', 'Standard density Black ink for coated papers. Specifically formulated to react quickly to changes in temperature.');

/* Product 5011 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5011,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5011,2007), (5011,2021);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501101, 1010, 5011, 'en', 'SU18364566');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501102, 1011, 5011, 'en', 'Saphira Anicolor Ink S Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501103, 1014, 5011, 'en', 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501104, 1020, 5011, 'en', 'Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501105, 1023, 5011, 'en', 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501106, 1015, 5011, 'en', 'Standard density Cyan ink for coated papers. Specifically formulated to react quickly to changes in temperature.');

/* Product 5012 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5012,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5012,2007), (5012,2021);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501201, 1010, 5012, 'en', 'SU18364567');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501202, 1011, 5012, 'en', 'Saphira Anicolor Ink S Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501203, 1014, 5012, 'en', 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501204, 1020, 5012, 'en', 'Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501205, 1023, 5012, 'en', 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501206, 1015, 5012, 'en', 'Standard density Magenta ink for coated papers. Specifically formulated to react quickly to changes in temperature.');

/* Product 5013 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5013,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5013,2007), (5013,2022);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501301, 1010, 5013, 'en', 'SU18364575');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501302, 1011, 5013, 'en', 'Saphira Ink Bio-speed Black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501303, 1014, 5013, 'en', 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501304, 1020, 5013, 'en', 'black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501305, 1023, 5013, 'en', 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501306, 1015, 5013, 'en', 'Black bio process ink for best results at a high speed.');

/* Product 5014 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5014,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5014,2007), (5014,2022);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501401, 1010, 5014, 'en', 'SU18364576');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501402, 1011, 5014, 'en', 'Saphira Ink Bio-speed Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501403, 1014, 5014, 'en', 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501404, 1020, 5014, 'en', 'Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501405, 1023, 5014, 'en', 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501406, 1015, 5014, 'en', 'Cyan bio process ink for best results at a high speed.');

/* Product 5015 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5015,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5015,2007), (5015,2022);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501501, 1010, 5015, 'en', 'SU18364577');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501502, 1011, 5015, 'en', 'Saphira Ink Bio-speed Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501503, 1014, 5015, 'en', 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501504, 1020, 5015, 'en', 'Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501505, 1023, 5015, 'en', 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501506, 1015, 5015, 'en', 'Magenta bio process ink for best results at a high speed.');

/* View */
INSERT INTO catalogxs.catalogview (name,catalog_id,id) VALUES ('webshop',1,1);
INSERT INTO catalogxs.catalogview_toplevelproductgroups (view_id,toplevelproductgroups_id) VALUES (1,2004);
INSERT INTO catalogxs.catalogview_toplevelproductgroups (view_id,toplevelproductgroups_id) VALUES (1,2008);
INSERT INTO catalogxs.catalogview_toplevelproductgroups (view_id,toplevelproductgroups_id) VALUES (1,2020);

/* Promotion examples */
INSERT INTO catalogxs.promotion (type,startdate,enddate,id,product_id,price,pricecurrency,volumediscount,view_id) VALUES ('catalog.volumediscountpromotion','2010-01-20','2011-01-20',1,5001,1000,'EUR',3,1);
INSERT INTO catalogxs.promotion (type,startdate,enddate,id,product_id,price,pricecurrency,volumediscount,view_id) VALUES ('catalog.volumediscountpromotion','2010-01-22','2011-02-20',2,5001,700,'EUR',4,1);
INSERT INTO catalogxs.promotion (type,startdate,enddate,id,product_id,price,pricecurrency,volumediscount,view_id) VALUES ('catalog.volumediscountpromotion','2010-01-10','2011-03-10',3,5002,800,'EUR',5,1);

[/template]
