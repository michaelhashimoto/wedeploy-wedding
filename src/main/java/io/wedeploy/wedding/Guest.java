package io.wedeploy.wedding;

import org.json.JSONArray;
import org.json.JSONObject;

public class Guest {

	public Guest(JSONObject jsonObject) {
		_jsonObject = jsonObject;

		_firstName = _jsonObject.getString("first_name");
		_lastName = _jsonObject.getString("last_name");
		_checkedIn = _jsonObject.getBoolean("checked_in");
		_table = _jsonObject.getInt("table");
	}

	public String getName() {
		return _firstName + " " + _lastName;
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String toJSONString() {
		return _jsonObject.toString();
	}

	private Boolean _checkedIn;
	private String _firstName;
	private JSONObject _jsonObject;
	private String _lastName;
	private Integer _table;

}