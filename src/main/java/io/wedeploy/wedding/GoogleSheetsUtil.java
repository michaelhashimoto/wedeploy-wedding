package io.wedeploy.wedding;

//import io.wedeploy.wedding.CurlUtil.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Builder;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;

public class GoogleSheetsUtil {

	public static void init() {
		_initSearchTableFromGoogleSheet("EN's Guests!A:I", Guest.Category.FRIENDS);
		_initSearchTableFromGoogleSheet("Bride's Parents Guests!A:I", Guest.Category.BRIDES_FAMILY);
		_initSearchTableFromGoogleSheet("Groom's Parents Guests!A:I", Guest.Category.GROOMS_FAMILY);

		_initTableAssignmentsFromGoogleSheet("Table Assignments!A:F");
	}

	public static String fixURI(String range) {
		range = range.replace(" ", "%20");
		range = range.replace("/", "%2F");
		range = range.replace("'", "%27");
		range = range.replace(":", "%3A");

		return range;
	}

	public static JSONObject readTableAssignmentsGoogleSheet(String range) {
		StringBuilder sb = new StringBuilder();

		sb.append("https://sheets.googleapis.com/v4/spreadsheets/");
		sb.append(_sheetID);
		sb.append("/values/");
		sb.append(fixURI(range));

		return new JSONObject(CurlUtil.curl(sb.toString(), null, GoogleSheetsUtil.getAccessToken()));
	}

	public static void writeTableAssignmentsGoogleSheet(String range) {
		JSONArray valuesRequestJSONArray = new JSONArray();

		List<Guest> guests = Guest.getGuests();

		for (int i = 0; i < guests.size() ; i++) {
			Guest guest = guests.get(i);

			valuesRequestJSONArray.put(new JSONArray()
				.put(guest.getGuestName())
				.put(guest.getRelatedGuestNames())
				.put(guest.getTableNumber())
				.put(guest.getCategory())
				.put(guest.getMenuChoice())
				.put(guest.getCheckedIn())
			);
		}

		for (int i = 0; i < 25 ; i++) {
			Guest guest = guests.get(i);

			valuesRequestJSONArray.put(new JSONArray()
				.put("")
				.put("")
				.put("")
				.put("")
				.put("")
				.put("")
			);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("https://sheets.googleapis.com/v4/spreadsheets/");
		sb.append(_sheetID);
		sb.append("/values:batchUpdate");

		JSONObject requestJSONObject = new JSONObject()
			.put("valueInputOption", "RAW")
			.put("data", new JSONArray()
				.put(new JSONObject()
					.put("range", "Table Assignments!A2:F")
					.put("majorDimension", "ROWS")
					.put("values", valuesRequestJSONArray)
				)
			);

		CurlUtil.curl(sb.toString(), requestJSONObject.toString(), _accessToken);
	}

	private static void _initTableAssignmentsFromGoogleSheet(String range) {
		JSONObject jsonObject = readTableAssignmentsGoogleSheet(range);

		JSONArray valuesResponseJSONArray = jsonObject.getJSONArray("values");

		List<String> guestNames = new ArrayList<>();

		for (int i = 1; i < valuesResponseJSONArray.length(); i++) {
			JSONArray valueJSONArray = valuesResponseJSONArray.getJSONArray(i);

			String guestName = valueJSONArray.optString(0, null);

			if (guestName == null || guestName.equals("")) {
				break;
			}

			if (guestNames.contains(guestName)) {
				throw new RuntimeException(
					"Duplicate guest name on table assignments: " + guestName);
			}

			guestNames.add(guestName);

			if (!Guest.exists(guestName)) {
				throw new RuntimeException(
					"Unknown guest name on table assignment " + guestName +
						" at line " + i);
			}

			Guest guest = Guest.getGuest(guestName);

			guest.setTableNumber(valueJSONArray.optInt(2, 0));
			guest.setCheckedIn(valueJSONArray.optBoolean(5, false));
		}

		writeTableAssignmentsGoogleSheet(range);
	}

	private static void _initSearchTableFromGoogleSheet(
		String range, Guest.Category category) {

		StringBuilder sb = new StringBuilder();

		sb.append("https://sheets.googleapis.com/v4/spreadsheets/");
		sb.append(_sheetID);
		sb.append("/values/");
		sb.append(fixURI(range));

		JSONObject jsonObject = new JSONObject(
			CurlUtil.curl(
				sb.toString(), null, GoogleSheetsUtil.getAccessToken()));

		JSONArray valuesJSONArray = jsonObject.getJSONArray("values");

		for (int i = 1; i < valuesJSONArray.length(); i++) {
			JSONArray valueJSONArray = valuesJSONArray.getJSONArray(i);

			String firstName = valueJSONArray.optString(0, null);

			if (firstName == null || firstName.equals("")) {
				break;
			}

			String primaryGuestName = _getPrimaryGuestName(valueJSONArray);
			List<String> guestNames = _getGuestNames(valueJSONArray);
			String menuChoice = _getMenuChoice(valueJSONArray);

			int adultsAttending = _getAdultsAttending(valueJSONArray);

			if (adultsAttending <= 0) {
				continue;
			}

			Group group = new Group(primaryGuestName, guestNames, category, menuChoice);
		}
	}

	private static int _getAdultsAttending(JSONArray jsonArray) {
		return jsonArray.optInt(7, 0);
	}

	private static String _getPrimaryGuestName(JSONArray jsonArray) {
		String firstName = jsonArray.optString(0, null);
		String lastName = jsonArray.optString(1, "");

		String primaryGuestName = firstName + " " + lastName;

		return primaryGuestName.trim();
	}

	private static String _getMenuChoice(JSONArray jsonArray) {
		String menuChoice = jsonArray.optString(6, null);

		if (menuChoice == null) {
			return "";
		}

		if (menuChoice.contains("C") && menuChoice.contains("V")) {
			return "Chinese & Vegetarian";
		}

		if (menuChoice.contains("C")) {
			return "Chinese";
		}

		if (menuChoice.contains("V")) {
			return "Vegetarian";
		}

		return menuChoice;
	}

	private static List<String> _getGuestNames(JSONArray jsonArray) {
		List<String> guestNames = new ArrayList<>();

		String primaryGuestName = _getPrimaryGuestName(jsonArray);

		guestNames.add(primaryGuestName);

		String guests = jsonArray.optString(2, null);

		if (guests == null || guests.isEmpty()) {
			return guestNames;
		}

		for (String guest : guests.split(",")) {
			guest = guest.trim();

			if (guest.isEmpty()) {
				throw new RuntimeException(
					"Invalid 'Guests' for " + primaryGuestName);
			}

			if (guestNames.contains(guest)) {
				throw new RuntimeException(
					"Duplicate 'Guests' for " + primaryGuestName);
			}

			guestNames.add(guest.trim());
		}

		return guestNames;
	}

	public static String getSheetID() {
		return _sheetID;
	}

	public static String getAccessToken() {
		return _accessToken;
	}

	public static void storeAccessToken(String code) {
		StringBuilder sb = new StringBuilder();

		sb.append("https://www.googleapis.com/oauth2/v4/token?");
		sb.append("code=" + code + "&");
		sb.append("client_id=" + _GOOGLE_CLIENT_ID + "&");
		sb.append("client_secret=" + _GOOGLE_CLIENT_SECRET + "&");
		sb.append("redirect_uri=" + fixURI(_WEDDING_APP_URL + "/code") + "&");
		sb.append("grant_type=" + "authorization_code");

		_accessTokenJSONObject = new JSONObject(CurlUtil.curl(
			sb.toString(), "{}"));

		_accessToken = _accessTokenJSONObject.getString("access_token");
	}

	public static String getLoginURL() {
		StringBuilder sb = new StringBuilder();

		sb.append("https://accounts.google.com/o/oauth2/v2/auth?");
		sb.append("scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.file https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fspreadsheets&");
		sb.append("access_type=offline&");
		sb.append("include_granted_scopes=true&");
		sb.append("state=state_parameter_passthrough_value&");
		sb.append("redirect_uri=" + fixURI(_WEDDING_APP_URL + "/code") + "&");
		sb.append("response_type=code&");
		sb.append("prompt=select_account&");
		sb.append("client_id=" + _GOOGLE_CLIENT_ID);

		return sb.toString();
	}

	private static JSONObject _accessTokenJSONObject;

	private static String _accessToken;
	private static String _sheetID = "1S-upsjmEjzzJ4JI4G55qhdSAoqshIccQewjaErbQwmY";

	private final static String _WEDDING_APP_URL = EnvironmentUtil.get("WEDDING_APP_URL");
	private final static String _GOOGLE_CLIENT_ID = EnvironmentUtil.get("GOOGLE_CLIENT_ID");
	private final static String _GOOGLE_CLIENT_SECRET = EnvironmentUtil.get("GOOGLE_CLIENT_SECRET");
	private final static String _GOOGLE_KEY = EnvironmentUtil.get("GOOGLE_KEY");

}