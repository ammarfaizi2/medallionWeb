package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScCouponSchedule;

import controllers.MedallionController;

public class CouponSchedulePickSerializer extends MedallionController implements JsonSerializer<ScCouponSchedule> {
	@Override
	public JsonElement serialize(ScCouponSchedule src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getId().getSecurityKey() + "-" + src.getId().getCouponNo());
		object.addProperty("securityKey", src.getId().getSecurityKey());
		object.addProperty("couponNo", src.getId().getCouponNo());
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		object.addProperty("description", dateFormat.format(src.getPaymentDate()));
		object.addProperty("fraction", src.getFraction());
		object.addProperty("fractionBase", src.getFractionBase());
		object.addProperty("interestRate", src.getInterestRate());
		object.addProperty("paidInterest", src.getPaidInterest());
		object.addProperty("lastPaymentDate", src.getLastPaymentDate().getTime());
		object.addProperty("nextPaymentDate", src.getNextPaymentDate().getTime());
		if (src.getSecurity() != null) {
			object.addProperty("isFraction", src.getSecurity().getIsFraction());
		}
		return object;
	}

}
