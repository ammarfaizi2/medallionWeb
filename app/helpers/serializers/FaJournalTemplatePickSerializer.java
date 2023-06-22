package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.FaJournalTemplate;

public class FaJournalTemplatePickSerializer implements JsonSerializer<FaJournalTemplate> {
	@Override
	public JsonElement serialize(FaJournalTemplate src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject objectMain = new JsonObject();
		JsonObject objectFaCoa = new JsonObject();
		JsonObject objectFaTrx = new JsonObject();
		JsonObject objectFaRec = new JsonObject();

		objectFaCoa.addProperty("coaCode", src.getFaCoaMaster().getCoaCode());
		objectFaCoa.addProperty("description", src.getFaCoaMaster().getDescription());
		objectFaCoa.addProperty("coaMasterKey", src.getFaCoaMaster().getCoaMasterKey());

		objectFaTrx.addProperty("transactionKey", "");
		objectFaRec.addProperty("recurringKey", "");

		objectMain.add("faCoaMaster", objectFaCoa);
		objectMain.add("faTransaction", objectFaTrx);
		objectMain.add("faRecurring", objectFaRec);

		objectMain.addProperty("dorc", src.getDorc());
		objectMain.addProperty("amount", 0);
		objectMain.addProperty("detailKey", "");
		objectMain.addProperty("subLedger", "");

		return objectMain;
	}
}