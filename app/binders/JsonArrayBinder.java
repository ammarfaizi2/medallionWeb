package binders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import play.data.binding.Global;
import play.data.binding.TypeBinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@Global
public class JsonArrayBinder implements TypeBinder<JsonArray> {

	@Override
	public Object bind(String name, Annotation[] annotation, String value, Class actualClass, Type genericType) throws Exception {
		return new JsonParser().parse(value).getAsJsonArray();
	}

}
