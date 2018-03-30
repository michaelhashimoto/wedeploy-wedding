package io.wedeploy.wedding;

import org.json.JSONArray;
import org.json.JSONObject;

public class Guest {

	public Guest(JSONObject jsonObject) {
		_jsonObject = jsonObject;

		_name = _jsonObject.getString("name");
		_checkIn = _jsonObject.getBoolean("check_in");
		_table = _jsonObject.getInt("table_num");
	}

	public String getName() {
		return _name;
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String toJSONString() {
		return _jsonObject.toString();
	}

	private Boolean _checkIn;
	private JSONObject _jsonObject;
	private String _name;
	private Integer _table;

}