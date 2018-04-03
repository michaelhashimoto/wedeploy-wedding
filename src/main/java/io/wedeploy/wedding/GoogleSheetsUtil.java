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
		_readGoogleSheet("EN's Guests!A:C", Guest.Category.FRIENDS);
		_readGoogleSheet("Bride's Parents Guests!A:C", Guest.Category.BRIDES_FAMILY);
		_readGoogleSheet("Groom's Parents Guests!A:C", Guest.Category.GROOMS_FAMILY);

		//_readTableAssignmentsGoogleSheet();
	}

	public static String fixURI(String range) {
		range = range.replace(" ", "%20");
		range = range.replace("/", "%2F");
		range = range.replace("'", "%27");
		range = range.replace(":", "%3A");

		return range;
	}

	private static void _readTableAssignmentsGoogleSheet() {
		StringBuilder sb = new StringBuilder();

		sb.append("https://sheets.googleapis.com/v4/spreadsheets/");
		sb.append(_sheetID);
		sb.append("/values/");
		sb.append(fixURI("Table Assignments!A:B"));

		JSONObject jsonObject = new JSONObject(
			CurlUtil.curl(
				sb.toString(), null, GoogleSheetsUtil.getAccessToken()));

		JSONArray valuesJSONArray = jsonObject.getJSONArray("values");

		List<String> guestNames = new ArrayList<>();

		int i = 1;

		while (i < valuesJSONArray.length()) {
			JSONArray valueJSONArray = valuesJSONArray.getJSONArray(i);

			String guestName = valueJSONArray.optString(0, null);

			if (guestName == null || guestName.equals("")) {
				break;
			}

			if (guestNames.contains(guestName)) {
				throw new RuntimeException(
					"Duplicate guest name on table assignments: " + guestName);
			}

			guestNames.add(guestName);

			if (Guest.exists(guestName)) {
				TableAssignment tableAssignment = new TableAssignment(
					i, Guest.getGuest(guestName));
			}

			i++;
		}

		for (Guest guest : Guest.getGuests()) {
			if (guestNames.contains(guest.getGuestName())) {
				continue;
			}

			TableAssignment tableAssignment = new TableAssignment(i, guest);

			i++;
		}

		// TableAssignment.update();
	}

	private static void _readGoogleSheet(
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

			Group group = new Group(primaryGuestName, guestNames, category);
		}
	}

	private static String _getPrimaryGuestName(JSONArray jsonArray) {
		String firstName = jsonArray.optString(0, null);
		String lastName = jsonArray.optString(1, "");

		String primaryGuestName = firstName + " " + lastName;

		return primaryGuestName.trim();
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
		sb.append("scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive&");
		sb.append("access_type=offline&");
		sb.append("include_granted_scopes=true&");
		sb.append("state=state_parameter_passthrough_value&");
		sb.append("redirect_uri=" + fixURI(_WEDDING_APP_URL + "/code") + "&");
		sb.append("response_type=code&");
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