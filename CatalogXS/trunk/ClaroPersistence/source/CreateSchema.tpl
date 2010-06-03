package claro.persistence

import metaphor.psm.rdbms.CreateSchema
import metaphor.psm.rdbms.IRdbmsPackage

[template AdditionalCreateSchemaSql(IRdbmsPackage package) joins CreateSchema.AdditionalSql]

/* make keys unique so no duplicates can be added */
/*ALTER TABLE catalogxs.productproductgroupsproductsproductgroup ADD UNIQUE (products_id, productgroups_id);*/
/*ALTER TABLE catalogxs.productgroupchildrenparentsproductgroup ADD UNIQUE (children_id,parents_id);*/
/*ALTER TABLE catalogxs.productgroupdefaultpropertyvaluespropertyvalue ADD UNIQUE (defaultpropertyvalues_id,item_id);*/

/* Catalog */
INSERT INTO catalogxs.catalog (name,id) VALUES ('staples',1);


/* Root Product Group */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2000,'catalog.productgroup');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1000,'String',2000);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3000, 1000,'Name');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1010,'String',2000);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3001,1010,'ArticleNumber');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1011,'String',2000);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3002,1011,'Description');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1012,'Media',2000);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3003,1012,'Image');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1013,'Money',2000);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3004,1013,'PriceOld');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1014,'Money',2000);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3005,1014,'Price');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1015,'String',2000);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3006,1015,'Synopsis');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10000, 1000, 2000, null, 'Root');

/* Supplies Product Group */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2001,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10001, 1000, 2001, null, 'Supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10002, 1011, 2001, null, 'Contains supplies of all sorts');

/* SpareParts Product Group */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2011,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201101, 1000, 2011, null, 'Spare Parts');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201102, 1011, 2011, null, 'Contains spare parts of all sorts');

/* Service Product Group */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2012,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201201, 1000, 2012, null, 'Services');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201202, 1011, 2012, null, 'Contains service products');

/* Ink Product Group */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2002,'catalog.productgroup');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1020,'String', 2002);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3010,1020,'Color');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1021,'String',2002);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3007,1021,'Kleur Code');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1022,'Integer', 2002);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3008,1022,'Weight');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1023,'Integer', 2002);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3009,1023,'PackageUni');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10003, 1000, 2002, null, 'Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10004, 1011, 2002, null, 'Ink and ink supplies');

INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2003,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10005, 1000, 2003, null, 'K+E Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10006, 1011, 2003, null, 'K+E ink and ink supplies');

INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2004,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10007, 1000, 2004, null, 'Products');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10008, 1011, 2004, null, 'All products');

/*  Insert example Ink products */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5001,'catalog.product');
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5002,'catalog.product');
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5003,'catalog.product');

/* make relation of product to product group 2002 & 2003 */
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5001,2002);
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5002,2002);
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5003,2002);

/* Product group Saphira Supplies */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2005,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200501, 1000, 2005, null, 'Saphira Supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200502, 1011, 2005, null, 'Saphira offers you a wide range of consumables to cover all your needs - from prepress to postpress. Our experts provide technical and application support for Saphira products, and advise you on how to use them most effectively.');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200503, 1012, 2005, null, null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200504, 1011, 2005, 'de', 'Heidelberg bietet Ihnen mit Saphira eine vielfaltige Auswahl an Verbrauchsmaterialien, die Ihren Bedarf von der Druckvorstufe bis hin zur Weiterverarbeitung abdecken. Unsere Experten stehen Ihnen ausserdem mit Rat und Tat zur Seite, wenn es um technische oder andere Fragen zur Anwendung und Leistungsfahigkeit der Saphira Produkte geht.');

/* Product group Saphira Supplies Proofing */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2006,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200621, 1000, 2006, null, 'Saphira Proofing Supplies');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200622, 1011, 2006, null, 'Saphira offers you a wide range of consumables to cover all your needs - from prepress to postpress. Our experts provide technical and application support for Saphira products, and advise you on how to use them most effectively.');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200623, 1012, 2006, null, null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200624, 1011, 2006, 'de', 'Heidelberg bietet Ihnen mit Saphira eine vielfaltige Auswahl an Verbrauchsmaterialien, die Ihren Bedarf von der Druckvorstufe bis hin zur Weiterverarbeitung abdecken. Unsere Experten stehen Ihnen ausserdem mit Rat und Tat zur Seite, wenn es um technische oder andere Fragen zur Anwendung und Leistungsfahigkeit der Saphira Produkte geht.');

/* Product group Saphira Supplies Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2007,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200711, 1000, 2007, null, 'Saphira Inks');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200712, 1011, 2007, null, 'Saphira offers you a wide range of consumables to cover all your needs - from prepress to postpress. Our experts provide technical and application support for Saphira products, and advise you on how to use them most effectively.');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200713, 1012, 2007, null, null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200714, 1000, 2007, 'de', 'Saphira Lacke');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200715, 1011, 2007, 'de', 'Heidelberg bietet Ihnen mit Saphira eine vielfaltige Auswahl an Verbrauchsmaterialien, die Ihren Bedarf von der Druckvorstufe bis hin zur Weiterverarbeitung abdecken. Unsere Experten stehen Ihnen ausserdem mit Rat und Tat zur Seite, wenn es um technische oder andere Fragen zur Anwendung und Leistungsfahigkeit der Saphira Produkte geht.');

/* Product group Brands */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2008,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200801, 1000, 2008, null, 'Brands');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200802, 1011, 2008, null, 'Products grouped by brand.');

/* Product group Brand: Others */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2009,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200901, 1000, 2009, null, 'Other');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (200902, 1011, 2009, null, 'Other brands.');

/* Product group Brand: Saphira */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2010,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201001, 1000, 2010, null, 'Saphira');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201002, 1011, 2010, null, 'Saphira Products');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (201003, 1012, 2010, null, null);

/* Product group Machines */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2020,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202001, 1000, 2020, null, 'Machines');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202002, 1011, 2020, null, 'Products by machine');

/* Product group Machine Speedmaster XL 105 */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2021,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202101, 1000, 2021, null, 'Speedmaster XL 105');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202102, 1011, 2021, null, 'Products suitable for the Heidelberg Speedmaster XL 105');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202103, 1012, 2021, null, null);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202104, 1015, 2021, null, 'Door een vergaand automatiseringsniveau, 100% volautomatische snelhe_idscompensatie, geavanceerde toepassingen voor een optimaal veltransport en hoge productiesnelheden van 18.000 druks per uur, zet de XL 105 ook in de praktijk een nieuwe categorie neer. Voor de hoge mate van stabiliteit die vereist is bij het produceren van topdrukwerk bij hoge snelheden, heeft Heidelberg een geheel nieuw platform ontworpen als basis voor de XL 105.');

/* Product group Machine Heidelberg Speedmaster XL 145 */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2022,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202201, 1000, 2022, null, 'Speedmaster XL 145');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202202, 1011, 2022, null, 'Products suitable for the Heidelberg Speedmaster XL 145');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (202203, 1012, 2022, null, null);

/* Product group Process */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2040,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204001, 1000, 2040, null, 'Process');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204002, 1011, 2040, null, 'Process Navigation');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204003, 1012, 2040, null, null);

/* Product group PrePress */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2041,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204101, 1000, 2041, null, 'Pre-Press');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204102, 1011, 2041, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204103, 1012, 2041, null, null);

/* Product group Press */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2042,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204201, 1000, 2042, null, 'Press');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204202, 1011, 2042, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204203, 1012, 2042, null, null);

/* Product group PostPress */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2043,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204301, 1000, 2043, null, 'Post Press');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204302, 1011, 2043, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204303, 1012, 2043, null, null);

/* Product group Prepress: Normalizing */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2044,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204401, 1000, 2044, null, 'Normalizing');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204402, 1011, 2044, null, 'Normalizing');

/* Product group Prepress: Color Conversion */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2045,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204501, 1000, 2045, null, 'Color Conversion');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204502, 1011, 2045, null, 'Color Conversion');

/* Product group Prepress: Page Proofing */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2046,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204601, 1000, 2046, null, 'Page Proofing');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204602, 1011, 2046, null, 'Page Proofing');

/* Product group Press: Presetting */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2047,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204701, 1000, 2047, null, 'Presetting');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204702, 1011, 2047, null, 'Presetting');

/* Product group Press: Image Control */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2048,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204801, 1000, 2048, null, 'Image Control');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204802, 1011, 2048, null, 'Image Control');

/* Product group Press: Printing */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2049,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204901, 1000, 2049, null, 'Printing');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (204902, 1011, 2049, null, 'Printing');

/* Product group Post Press: Cutting */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2050,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (205001, 1000, 2050, null, 'Cutting');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (205902, 1011, 2050, null, 'Cutting');

/* Product group Post Press: Folding */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2051,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (205101, 1000, 2051, null, 'Folding');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (205102, 1011, 2051, null, 'Folding');

/* Product group Post Press: Stiching */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2052,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (205201, 1000, 2052, null, 'Stiching');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (205202, 1011, 2052, null, 'Stiching');

/* Product group Xingraphics */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2080,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208001, 1000, 2080, null, 'Xingraphics');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208002, 1011, 2080, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208003, 1012, 2080, null, null);

/* Product group Xingraphics: Plates */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2081,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208101, 1000, 2081, null, 'Plates');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208102, 1011, 2081, null, 'Plates');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208103, 1012, 2081, null, null);

/* Properties for Xingraphics: Plates */
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1080,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3080,1080,'Type');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1081,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3081,1081,'Substrate');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1082,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3082,1082,'Spectral sensitivity');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1083,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3083,1083,'Exposure energy');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1084,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3084,1084,'Resolution');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1085,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3085,1085,'Run length');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1086,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3086,1086,'Maximum width');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1087,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3087,1087,'Start up');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1088,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3088,1088,'Roll up');
INSERT INTO catalogxs.property (id,type_id,item_id) VALUES (1089,'String', 2081);
INSERT INTO catalogxs.label (id,property_id,label) VALUES (3089,1089,'Shelf life');

/* Product group Xingraphics: Chemicals */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2082,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208201, 1000, 2082, null, 'Chemicals');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208202, 1011, 2082, null, 'Chemicals');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208203, 1012, 2082, null, null);

/* Product group Xingraphics: eCO Primo */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2083,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208301, 1000, 2083, null, 'FIT eCO Primo');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208302, 1011, 2083, null, 'FIT eCO Primo Processless Thermal Plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208303, 1012, 2083, null, null);

/* Property values for Xingraphics: eCO Primo */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208310, 1080, 2083, null, 'Thermal negative processless plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208311, 1081, 2083, null, 'High quality grained and anodized aluminum');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208312, 1082, 2083, null, '830nm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208313, 1083, 2083, null, '300 mj/cm2(varies based on platesetter manufacture)');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208314, 1084, 2083, null, 'AM 1-99% @ 200 lpi; 20 micron stochastic');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208315, 1085, 2083, null, '20,000-50,000 impressions ');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208316, 1086, 2083, null, '1050mm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208317, 1087, 2083, null, 'Pre-dampening required');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208318, 1088, 2083, null, '<15 impressions');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208319, 1089, 2083, null, '18 months when away from excessive cold, heat and humidity');

/* Product group Xingraphics: Xtra Melior */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2084,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208401, 1000, 2084, null, 'FIT Xtra Melior');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208402, 1011, 2084, null, 'FIT Xtra Melior Thermal CTP Plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208403, 1012, 2084, null, null);

/* Property values for Xingraphics: Xtra Melior */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208410, 1080, 2084, null, 'Positive working thermal CTP plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208411, 1081, 2084, null, 'Electro-chemically grained and anodized lithographic-grade aluminum with hydrophilic treatment');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208412, 1082, 2084, null, '800~850n');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208413, 1083, 2084, null, '100~120mj/cm2');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208414, 1084, 2084, null, '1 ~ 99% @ 450 lpi');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208415, 1085, 2084, null, '250,000 impressions unbaked and 1,000,000 impressions post-baked');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208416, 1086, 2084, null, '1480mm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208417, 1087, 2084, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208418, 1088, 2084, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208419, 1089, 2084, null, '18 months when away from excessive cold, heat and humidity');

/* Product group Xingraphics: Melior */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,2085,'catalog.productgroup');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208501, 1000, 2085, null, 'FIT Melior');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208502, 1011, 2085, null, 'FIT Melior Thermal CTP Plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208503, 1012, 2085, null, null);

/* Property values for Xingraphics: Melior */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208510, 1080, 2085, null, 'Positive working thermal CTP plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208511, 1081, 2085, null, 'Electro-chemically grained and anodized lithographic-grade aluminum with hydrophilic treatment');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208512, 1082, 2085, null, '830nm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208513, 1083, 2085, null, '120mj/cm2');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208514, 1084, 2085, null, '1 ~ 99% @ 450lp');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208515, 1085, 2085, null, '250,000 impressions unbaked and 1,000,000 impressions post-baked');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208516, 1086, 2085, null, '1450mm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208517, 1087, 2085, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208518, 1088, 2085, null, '');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (208519, 1089, 2085, null, '18 months when away from excessive cold, heat and humidity');

/* Product Group hierarchy */
INSERT INTO catalogxs.item_parents (parents_id,children_id) VALUES

/* Root */ 
(2000,2004),
(2000,2008),
(2000,2020),
(2000,2040),

/* Supplies */
(2001,2002),
(2001,2005),

/* Ink */
(2002,2003),
(2002,2007),

/* Saphira supplies */
(2005,2006),
(2005,2007),

/* Products */
(2004,2001),
(2004,2011),
(2004,2012),

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
(2020,2022),

/* Process */
(2040,2041),
(2040,2042),
(2040,2043),

/* PrePress */
(2041,2044),
(2041,2045),
(2041,2046),

/* Press */
(2042,2047),
(2042,2048),
(2042,2049),

/* Press */
(2043,2050),
(2043,2051),
(2043,2052),

/* Xingraphics */
(2008,2080),

/* Xingraphics Plates & Chemicals */
(2080, 2081),
(2080, 2082),

/* Xingraphics Plates under Print */
(2049, 2081), 

/* Xingraphics Plates */
(2081, 2083),
(2081, 2084),
(2081, 2085)
;

/* Product 5001 Property Values */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10010, 1010, 5001, null, '188078');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10011, 1011, 5001, null, 'Blue Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (10012, 1014, 5001, null, 1295, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10013, 1020, 5001, null, 'blue');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10014, 1021, 5001, null, '39K HKS');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10015, 1023, 5001, null, 'Rol');

/* Product 5002 Property Values */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10020, 1010, 5002, null, '128116');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10021, 1011, 5002, null, 'Red Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (10022, 1014, 5002, null, 2295, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10023, 1020, 5002, null, 'red');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10024, 1021, 5002, null, '275-C NP');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10025, 1023, 5002, null, 'Rol');

/* Product 5003 Property Values */
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10030, 1010, 5003, null, '175778');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10031, 1011, 5003, null, 'Yellow Ink');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (10032, 1014, 5003, null, 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10033, 1020, 5003, null, 'yellow');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10034, 1021, 5003, null, '51K* HKS');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (10035, 1023, 5003, null, 'Blik');

/* Product 5010 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5010,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5010,2007), (5010,2021);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501001, 1010, 5010, null, 'SU18364565');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501002, 1011, 5010, null, 'Saphira Anicolor Ink S Black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501003, 1014, 5010, null, 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501004, 1020, 5010, null, 'black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501005, 1023, 5010, null, 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501006, 1015, 5010, null, 'Standard density Black ink for coated papers. Specifically formulated to react quickly to changes in temperature.');

/* Product 5011 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5011,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5011,2007), (5011,2021);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501101, 1010, 5011, null, 'SU18364566');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501102, 1011, 5011, null, 'Saphira Anicolor Ink S Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501103, 1014, 5011, null, 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501104, 1020, 5011, null, 'Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501105, 1023, 5011, null, 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501106, 1015, 5011, null, 'Standard density Cyan ink for coated papers. Specifically formulated to react quickly to changes in temperature.');

/* Product 5012 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5012,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5012,2007), (5012,2021);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501201, 1010, 5012, null, 'SU18364567');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501202, 1011, 5012, null, 'Saphira Anicolor Ink S Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501203, 1014, 5012, null, 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501204, 1020, 5012, null, 'Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501205, 1023, 5012, null, 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501206, 1015, 5012, null, 'Standard density Magenta ink for coated papers. Specifically formulated to react quickly to changes in temperature.');

/* Product 5013 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5013,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5013,2007), (5013,2022);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501301, 1010, 5013, null, 'SU18364575');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501302, 1011, 5013, null, 'Saphira Ink Bio-speed Black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501303, 1014, 5013, null, 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501304, 1020, 5013, null, 'black');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501305, 1023, 5013, null, 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501306, 1015, 5013, null, 'Black bio process ink for best results at a high speed.');

/* Product 5014 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5014,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5014,2007), (5014,2022);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501401, 1010, 5014, null, 'SU18364576');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501402, 1011, 5014, null, 'Saphira Ink Bio-speed Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501403, 1014, 5014, null, 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501404, 1020, 5014, null, 'Cyan');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501405, 1023, 5014, null, 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501406, 1015, 5014, null, 'Cyan bio process ink for best results at a high speed.');

/* Product 5015 Saphira Ink */
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5015,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5015,2007), (5015,2022);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501501, 1010, 5015, null, 'SU18364577');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501502, 1011, 5015, null, 'Saphira Ink Bio-speed Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,moneyvalue, moneycurrency) VALUES (501503, 1014, 5015, null, 855, 'EUR');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501504, 1020, 5015, null, 'Magenta');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501505, 1023, 5015, null, 'Box');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (501506, 1015, 5015, null, 'Magenta bio process ink for best results at a high speed.');

/* Product 5080 Xingraphics eCO Prima 1x1*/
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5080,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5080,2083);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508001, 1010, 5080, null, 'SKU467262');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508002, 1011, 5080, null, 'FIT eCO Primo 1x1');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508003, 1014, 5080, null, '100.95');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508010, 1080, 5080, null, 'Thermal negative processless plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508011, 1081, 5080, null, 'High quality grained and anodized aluminum');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508012, 1082, 5080, null, '830nm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508013, 1083, 5080, null, '300 mj/cm2(varies based on platesetter manufacture)');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508014, 1084, 5080, null, 'AM 1-99% @ 200 lpi; 20 micron stochastic');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508015, 1085, 5080, null, '20,000-50,000 impressions ');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508016, 1086, 5080, null, '1050mm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508017, 1087, 5080, null, 'Pre-dampening required');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508018, 1088, 5080, null, '<15 impressions');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508019, 1089, 5080, null, '18 months when away from excessive cold, heat and humidity');

/* Product 5081 Xingraphics eCO Prima 1x5*/
INSERT INTO catalogxs.item (catalog_id,id,type) VALUES (1,5081,'catalog.product');
INSERT INTO catalogxs.item_parents (children_id,parents_id) VALUES (5081,2083);
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508101, 1010, 5081, null, 'SKU467262');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508102, 1011, 5081, null, 'FIT eCO Primo 1x5');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508103, 1014, 5081, null, '399.00');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508110, 1080, 5081, null, 'Thermal negative processless plate');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508111, 1081, 5081, null, 'High quality grained and anodized aluminum');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508112, 1082, 5081, null, '830nm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508113, 1083, 5081, null, '300 mj/cm2(varies based on platesetter manufacture)');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508114, 1084, 5081, null, 'AM 1-99% @ 200 lpi; 20 micron stochastic');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508115, 1085, 5081, null, '20,000-50,000 impressions ');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508116, 1086, 5081, null, '1050mm');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508117, 1087, 5081, null, 'Pre-dampening required');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508118, 1088, 5081, null, '<15 impressions');
INSERT INTO catalogxs.propertyvalue (id,property_id,item_id,language,stringvalue) VALUES (508119, 1089, 5081, null, '18 months when away from excessive cold, heat and humidity');

/* bShop */
INSERT INTO catalogxs.shop (name,catalog_id,id) VALUES ('webshop',1,1);
INSERT INTO catalogxs.shop_toplevelproductgroups (shop_id,toplevelproductgroups_id) VALUES (1,2040);
INSERT INTO catalogxs.shop_toplevelproductgroups (shop_id,toplevelproductgroups_id) VALUES (1,2004);
INSERT INTO catalogxs.shop_toplevelproductgroups (shop_id,toplevelproductgroups_id) VALUES (1,2008);
INSERT INTO catalogxs.shop_toplevelproductgroups (shop_id,toplevelproductgroups_id) VALUES (1,2020);

/* Promotion examples */
INSERT INTO catalogxs.promotion (type,startdate,enddate,id,product_id,price,pricecurrency,volumediscount,shop_id) VALUES ('shop.volumediscountpromotion','2010-01-20','2011-01-20',1,5001,1000,'EUR',3,1);
INSERT INTO catalogxs.promotion (type,startdate,enddate,id,product_id,price,pricecurrency,volumediscount,shop_id) VALUES ('shop.volumediscountpromotion','2010-01-22','2011-02-20',2,5001,700,'EUR',4,1);
INSERT INTO catalogxs.promotion (type,startdate,enddate,id,product_id,price,pricecurrency,volumediscount,shop_id) VALUES ('shop.volumediscountpromotion','2010-01-10','2011-03-10',3,5002,800,'EUR',5,1);

[/template]
