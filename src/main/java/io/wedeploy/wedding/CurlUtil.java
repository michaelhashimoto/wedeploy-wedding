package io.wedeploy.wedding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CurlUtil {

	public static String curl(String curl) {
		curl += "?key=" + _GOOGLE_KEY;

		try {
			URL url = new URL(curl);

			URLConnection urlConnection = url.openConnection();

			urlConnection.setRequestProperty("X-Requested-With", "Curl");

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

}