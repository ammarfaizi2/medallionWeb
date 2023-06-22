package dbf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

public class Reader {
	private static Logger log = Logger.getLogger(Reader.class);

	@Test
	public void readFile() {
		try {

			// create a DBFReader object
			String fileName = "E:\\Erwin\\ProjectArchive\\Medallion\\UploadDownload\\SS130905.DBF";
			InputStream inputStream = new FileInputStream(fileName); // take dbf
																		// file
																		// as
																		// program
																		// argument
			DBFReader reader = new DBFReader(inputStream);

			// get the field count if you want for some reasons like the
			// following
			//
			int numberOfFields = reader.getFieldCount();

			// use this count to fetch all field information
			// if required
			//
			for (int i = 0; i < numberOfFields; i++) {

				DBFField field = reader.getField(i);

				// do something with it if you want
				// refer the JavaDoc API reference for more details
				//
				log.debug(field.getName() + "\t");
			}

			// Now, lets us start reading the rows
			//
			Object[] rowObjects;

			while ((rowObjects = reader.nextRecord()) != null) {

				for (int i = 0; i < rowObjects.length; i++) {

					log.debug(rowObjects[i] + "\t");
				}
			}

			// By now, we have itereated through all of the rows

			inputStream.close();
		} catch (DBFException e) {

			log.debug(e.getMessage());
		} catch (IOException e) {

			log.debug(e.getMessage());
		}
	}

}
