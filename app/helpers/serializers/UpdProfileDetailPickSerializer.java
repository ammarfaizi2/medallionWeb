package helpers.serializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.UdProfileDetailMap;
import com.simian.medallion.model.UpdProfileDetail;

public class UpdProfileDetailPickSerializer implements JsonSerializer<UpdProfileDetail> {
	@Override
	public JsonElement serialize(UpdProfileDetail src, Type arg1, JsonSerializationContext arg2) {
		JsonObject object = new JsonObject();
		object.addProperty("profileDetailKey", src.getProfileDetailKey());
		object.addProperty("noSeq", src.getNoSeq());
		object.addProperty("defaultValue", src.getDefaultValue());
		object.addProperty("sourceField", src.getSourceField());
		object.addProperty("targetField", src.getTargetField());
		object.addProperty("systemField", src.getSystemField());
		object.addProperty("mandatory", src.getMandatory());
		object.addProperty("mappingRequired", src.isMappingRequired());
		object.addProperty("systemDefaultValue", src.isSystemDefaultValue());
		object.addProperty("sourceType", src.getSourceType());

		JsonObject fixValue = new JsonObject();
		if (src.getFixValue() != null) {
			fixValue.addProperty("lookupId", src.getFixValue().getLookupId());
			fixValue.addProperty("lookupDescription", src.getFixValue().getLookupDescription());
		}
		object.add("fixValue", fixValue);

		JsonObject dataType = new JsonObject();
		if (src.getDataType() != null) {
			dataType.addProperty("lookupId", src.getDataType().getLookupId());
			dataType.addProperty("lookupDescription", src.getDataType().getLookupDescription());
			dataType.addProperty("lookupCode", src.getDataType().getLookupCode());
		}

		object.add("dataType", dataType);

		object.addProperty("length", src.getLength());
		JsonObject formatType = new JsonObject();
		if (src.getFormatType() != null) {
			formatType.addProperty("lookupId", src.getFormatType().getLookupId());
			formatType.addProperty("lookupDescription", src.getFormatType().getLookupDescription());
		}
		object.add("formatType", formatType);

		/*
		 * JsonObject mappingCode = new JsonObject(); if( src.getMappingCode()
		 * != null ){ if( src.getMappingCode().size() > 0 ){ for(
		 * UdProfileDetailMap map : src.getMappingCode() ){
		 * mappingCode.addProperty("mappingKey", map.getMappingKey());
		 * mappingCode.addProperty("toCode", map.getToCode());
		 * mappingCode.addProperty("fromCode", map.getFromCode());
		 * mappingCode.addProperty("profileDetail",
		 * map.getProfileDetail().getProfileDetailKey()); } } }
		 */
		List<UdProfileDetailMap> listMap = new ArrayList<UdProfileDetailMap>();
		if (src.getMappingCode() != null) {

			for (UdProfileDetailMap map : src.getMappingCode()) {
				// set null for ever posibility of circular
				UpdProfileDetail d_ = new UpdProfileDetail();
				d_.setProfileDetailKey(src.getProfileDetailKey());
				d_.setNoSeq(src.getNoSeq());
				map.setProfileDetail(d_);
				listMap.add(map);
			}
			object.add("mappingCode", arg2.serialize(src.getMappingCode()));
		}
		// object.add("mappingCode", arg2.serialize(listMap));
		return object;

	}

}
