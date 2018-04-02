package io.wedeploy.wedding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;

public class CurlUtil {

	public static String curl(String curl) {
		return curl(curl, null, null);
	}

	public static String curl(String curl, String request) {
		return curl(curl, request, null);
	}

	public static String curl(String curl, String request, String bearer) {
		System.out.println("Reading " + curl);

		try {
			URL url = new URL(curl);

			URLConnection urlConnection = url.openConnection();

			if (urlConnection instanceof HttpURLConnection) {
				HttpURLConnection httpURLConnection =
					(HttpURLConnection)urlConnection;

				httpURLConnection.setRequestMethod("GET");

				if (bearer != null) {
					httpURLConnection.setRequestProperty("Authorization", "Bearer " + bearer);
				}

				if (request != null) {
					httpURLConnection.setRequestMethod("POST");

					httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

					httpURLConnection.setDoOutput(true);

					try (OutputStream outputStream =
							httpURLConnection.getOutputStream()) {

						outputStream.write(request.getBytes("UTF-8"));

						outputStream.flush();
					}
				}
			}

			InputStreamReader inputStreamReader = new InputStreamReader(
				urlConnection.getInputStream());

			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String line = null;

			StringBuilder sb = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}

			bufferedReader.close();

			return sb.toString();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private final static String _GOOGLE_KEY = EnvironmentUtil.get("GOOGLE_KEY");
	public final static String _GOOGLE_CLIENT_SECRET = EnvironmentUtil.get("GOOGLE_CLIENT_SECRET");
	public final static String _GOOGLE_CLIENT_ID = EnvironmentUtil.get("GOOGLE_CLIENT_ID");

}