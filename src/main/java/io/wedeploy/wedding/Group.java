package io.wedeploy.wedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Group {

	protected Group(
		String primaryGuestName, List<String> guestNames,
		Guest.Category category, String menuChoice) {

		if (_groups.containsKey(primaryGuestName)) {
			throw new RuntimeException("Duplicate group " + primaryGuestName);
		}

		_primaryGuestName = primaryGuestName;
		_category = category;

		_groups.put(primaryGuestName, this);

		for (int i = 0; i < guestNames.size(); i++) {
			String guestName = guestNames.get(i);

			List<String> relatedGuestNames = new ArrayList<>(guestNames);

			relatedGuestNames.remove(guestName);

			if (menuChoice.equals("Chinese & Vegetarian")) {
				if (i < 1) {
					_guests.add(new Guest(guestName, relatedGuestNames, category, "Chinese"));
				}
				else {
					_guests.add(new Guest(guestName, relatedGuestNames, category, "Vegetarian"));
				}
			}
			else {
				_guests.add(new Guest(guestName, relatedGuestNames, category, menuChoice));
			}
		}
	}

	private Guest.Category _category;
	private List<Guest> _guests = new ArrayList<>();
	private String _primaryGuestName;

	public static void init() {
		_groups.clear();
	}

	private static Map<String, Group> _groups = new HashMap<>();

}