package helpers.serializers;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsPortfolio;
import com.simian.medallion.model.PlTransaction;
import com.simian.medallion.model.PlTransactionDetail;

import controllers.MedallionController;

public class PledgingPickSerializer extends MedallionController implements JsonSerializer<PlTransaction> {
	private static Logger log = Logger.getLogger(PledgingPickSerializer.class);

	@Override
	public JsonElement serialize(PlTransaction src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		String detailPledgings = null;
		try {
			for (PlTransactionDetail plDetail : src.getPlTransactionDetails()) {
				CsPortfolio portfolio = accountService.getPortfolio(plDetail.getAccount().getCustodyAccountKey(), plDetail.getSecurity().getSecurityKey(), plDetail.getClassification().getLookupId(), plDetail.getHoldingRefs());
				plDetail.setPortfolio(portfolio);
				plDetail.setPortoQuantity(portfolio.getPortfolioQuantity());
				src.getPlTransactionDetails().add(plDetail);
			}
			JsonHelper json = new JsonHelper().getPledgingDetailSerializer();
			detailPledgings = json.serialize(src.getPlTransactionDetails());
			log.debug("DATA = " + detailPledgings);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		object.addProperty("detailPledgings", detailPledgings);
		object.addProperty("transactionNo", src.getTransactionKey());
		object.addProperty("pledgingAmount", src.getAmount());
		object.addProperty("remarks", src.getRemarks());
		object.addProperty("transactionDate", src.getTransactionDate().getTime());
		return object;
	}
}
