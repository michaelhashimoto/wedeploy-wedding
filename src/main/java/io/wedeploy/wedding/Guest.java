package io.wedeploy.wedding;

import org.json.JSONArray;
import org.json.JSONObject;

public class Guest {

	public Guest(JSONObject jsonObject) {
		_jsonObject = jsonObject;

		_name = _jsonObject.getString("name");
		_firstName = _jsonObject.getString("first_name");
		_lastName = _jsonObject.getString("last_name");
		_checkedIn = _jsonObject.getBoolean("checked_in");
		_table = _jsonObject.getInt("table");
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

	private Boolean _checkedIn;
	private String _firstName;
	private JSONObject _jsonObject;
	private String _lastName;
	private String _name;
	private Integer _table;

}