package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CBestMessage;

public class CBestMessagePickSerializer implements JsonSerializer<CBestMessage> {

	@Override
	public JsonElement serialize(CBestMessage src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		object.addProperty("name", src.getName());
		if (src.getData() != null) {
			object.addProperty("uniqueidentifier", src.getData().getUniqueidentifier());
			object.addProperty("externalreference", src.getData().getExternalreference());
			object.addProperty("instructiontype", src.getData().getInstructiontype());
			object.addProperty("participantcode", src.getData().getParticipantcode());
			object.addProperty("participantaccount", src.getData().getParticipantaccount());
			object.addProperty("participantaccountcash", src.getData().getParticipantaccountcash());
			object.addProperty("counterpartcode", src.getData().getCounterpartcode());
			object.addProperty("securitycodetype", src.getData().getSecuritycodetype());
			object.addProperty("securitycode", src.getData().getSecuritycode());
			object.addProperty("numberofsecurities", src.getData().getNumberofsecurities());
			object.addProperty("currencycode", src.getData().getCurrencycode());
			object.addProperty("ctpreference", src.getData().getCtpreference());
			object.addProperty("settlementamount", src.getData().getSettlementamount());
			object.addProperty("settlementdate", src.getData().getSettlementdate());
			object.addProperty("description", src.getData().getDescription());
			object.addProperty("counterparttype", src.getData().getCounterparttype());
			object.addProperty("taxsubmittedbyksei", src.getData().getTaxsubmittedbyksei());
			object.addProperty("maturitydate", src.getData().getMaturitydate());
			object.addProperty("tradedate", src.getData().getTradedate());
			object.addProperty("settlementdate", src.getData().getSettlementdate());
			object.addProperty("facevalue", src.getData().getFacevalue());
			object.addProperty("price", src.getData().getPrice());
			object.addProperty("yield", src.getData().getYield());
			object.addProperty("interestrate", src.getData().getInterestrate());
			object.addProperty("accrueddays", src.getData().getAccrueddays());
			object.addProperty("accruedinterest", src.getData().getAccruedinterest());
			object.addProperty("miscamount", src.getData().getMiscamount());
			object.addProperty("capitalgaintax", src.getData().getCapitalgaintax());
			object.addProperty("accruedinteresttax", src.getData().getAccruedinteresttax());
			object.addProperty("netproceeds", src.getData().getNetproceeds());
			object.addProperty("sourceaccount", src.getData().getSourceaccount());
			object.addProperty("targetaccount", src.getData().getTargetaccount());
			object.addProperty("cleanprice", src.getData().getCleanprice());
			object.addProperty("accruedinterestamount", src.getData().getAccruedinterestamount());
			object.addProperty("cancelmessagetype", src.getData().getCancelmessagetype());
		}
		return object;
	}

}
