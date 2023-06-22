package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnMenu;

public class MenuPickSerializer implements JsonSerializer<GnMenu> {
	@Override
	public JsonElement serialize(GnMenu src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getMenuKey());
		object.addProperty("number", src.getMenuNumber());
		object.addProperty("description", src.getMenuBreadCrumb());
		return object;
	}
}
