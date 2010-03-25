package agilexs.catalogxsadmin.presentation.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface I18NCatalogXS extends Messages {

  @DefaultMessage("<h2>{0}</h2>")
  String h2(String text);

  @DefaultMessage("<h3>{0}</h3>")
  String h3(String text);

  String inheritedProperties();

  @DefaultMessage("Today {0} products in promotion")
  @PluralText({"none", "Today no promotions",
    "one", "Today 1 product in promotion"})
  String nrOfPromotions(@PluralCount int nrOfPromotions);

  String newGroup();

  String saveChanges();

  String containsProducts();

  String newProduct();

  String noProductsInGroup();

  String backToProductOverview();

  String explainNavigationList();

  String name();

  String type();

  String value();

  String languageSpecificValue();

  String languageSpecificName();

  String groupOnly();

  String add();

  String products();

  String group();

  String catalog();

  String navigation();

  String promotions();

  String settings();

  String todo();

  String promotionSaved();

  String productSaved();

  String productGroupSaved(String groupName);

  String fileUploaded(String fileName);

  String navigationSaved();
}
