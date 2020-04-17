package com.indigo.esb.jms;

public enum IndigoHeaderJMSPropertyEnum {
	TARGET_ADAPTOR_NAME("esb_target_adaptor_name") {
	},
	SOURCE_ADAPTOR_NAME("esb_source_adaptor_name") {
	},
	TARGET_DESTINATION("esb_target_destination") {
	},
	ESB_RESULT_TARGET_DESTINATION("esb_result_target_destination") {
	},
	TARGET_TABLE_NAME("esb_target_table_name") {
	},
	SOURCE_TABLE_NAME("esb_source_table_name") {
	},
	IF_DATA_TYPE("esb_if_data_type") {
	},
	IF_TYPE("esb_if_type") {
	},
	IF_ID("esb_if_id") {
	},
	UNKNOWN("unknown");

	private final String key;

	IndigoHeaderJMSPropertyEnum(String key) {
		this.key = key;
	}

	public static IndigoHeaderJMSPropertyEnum fromValue(String value) {
		if (value != null) {
			for (IndigoHeaderJMSPropertyEnum prop : values()) {
				if (prop.key.equals(value)) {
					return prop;
				}
			}
		}
		return UNKNOWN;
	}

	@Override
	public String toString() {
		return key;
	}

}
