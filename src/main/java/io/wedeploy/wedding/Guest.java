package io.wedeploy.wedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class Guest {

	public Guest(
		String guestName, List<String> relatedGuestNames, Category category, String menuChoice) {

		if (_guests.containsKey(guestName)) {
			throw new RuntimeException("Duplicate guest " + guestName);
		}

		_guests.put(guestName, this);

		_guestName = guestName;
		_relatedGuestNames = relatedGuestNames;
		_category = category;
		_menuChoice = menuChoice;
	}

	public String getCategory() {
		return _category.toString();
	}

	public String getCheckedIn() {
		if(_checkedIn) {
			return "TRUE";
		}

		return "";
	}

	public String getGuestName() {
		return _guestName;
	}

	public String getMenuChoice() {
		return _menuChoice;
	}

	public String getRelatedGuestNames() {
		return StringUtils.join(_relatedGuestNames, ", ");
	}

	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject()
			.put("category", getCategory())
			.put("checked_in", getCheckedIn())
			.put("guest_name", getGuestName())
			.put("menu_choice", getMenuChoice())
			.put("related_guest_names", getRelatedGuestNames())
			.put("table_num", getTableNumber());

		return jsonObject;
	}

	public String getTableNumber() {
		if (_tableNumber <= 0) {
			return "";
		}

		return String.valueOf(_tableNumber);
	}

	public void setCheckedIn(Boolean checkedIn) {
		_checkedIn = checkedIn;
	}

	public void setTableNumber(Integer tableNumber) {
		_tableNumber = tableNumber;
	}

	public static Guest getGuest(String guestName) {
		if (_guests.containsKey(guestName)) {
			return _guests.get(guestName);
		}

		return null;
	}

	public static List<Guest> getGuests() {
		List<Guest> guests = new ArrayList<>(_guests.values());

		return guests;
	}

	public static JSONArray getGuestsJSONArray() {
		JSONArray jsonArray = new JSONArray();

		for (Guest guest : _guests.values()) {
			jsonArray.put(guest.getJSONObject());
		}

		return jsonArray;
	}

	public static Boolean exists(String guestName) {
		if (_guests.containsKey(guestName)) {
			return true;
		}

		return false;
	}

	private Category _category;
	private boolean _checkedIn;
	private String _guestName;
	private String _menuChoice;
	private int _tableNumber;
	private List<String> _relatedGuestNames;

	public static enum Category {
		BRIDES_FAMILY("Bride's Parent's Guests"), FRIENDS("EN's Guests"),
		GROOMS_FAMILY("Groom's Parent's Guests");

		public String toString() {
			return _value;
		}

		private Category(String value) {
			_value = value;
		}

		private String _value;
	};

	private static Map<String, Guest> _guests = new TreeMap<>();
}