package agilexs.catalogxs.presentation.cache

import agilexs.catalogxs.jpa.{catalog => jpa}

case class Promotion(catalog : Catalog, promotion : jpa.Promotion) {

}
