package agilexs.catalogxs.businesslogic;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class LuceneHelper {

	private File dir = new File("productMaintanceIndex");

	public IndexWriter getWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriter writer = new IndexWriter(FSDirectory.open(getMaintanceStore()), new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
		return writer;
	}

	public static Field createKeyWord(String name, String value) {
		if (name == null || value == null) {
			return null;
		}
		return new Field(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED);
	}

	public File getMaintanceStore() {
		return dir;
	}

}
