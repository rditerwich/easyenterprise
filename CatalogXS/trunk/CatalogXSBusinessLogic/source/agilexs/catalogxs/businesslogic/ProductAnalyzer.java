package agilexs.catalogxs.businesslogic;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import agilexs.catalogxs.jpa.catalog.Product;

public class ProductAnalyzer {

	public Term createKey(Product theProduct) {
		return new Term("id", String.valueOf(theProduct.getId()));
	}

	public Document createDocument(Product theProduct) {
		Document doc = new Document();
		doc.add(LuceneHelper.createKeyWord("id", String.valueOf(theProduct.getId())));

		return doc;
	}

}
