/**
 * Created by yim sarakoudom for Kravanh Eco base on Firebase Cloud Messaging.
 */

package kravanhFirebaseMessage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;


public class kravanhFirebaseMessage {

	private String projectId;
	private String authKey;

	public kravanhFirebaseMessage setAuthKey(String authKey) {
		this.authKey = authKey;
		return this;
	}

	public kravanhFirebaseMessage setProjectId(String projectId) {
		this.projectId = projectId;
		return this;
	}


	public boolean sendNotification(String title, String message, int userId) {
		try {
			String success = pushMessage(title, message, getToken(userId));
			return success != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean registerDevice(String token, int userId) {
		try {
			boolean success = createDeviceGroup(token, userId);
			if (!success) {
				success = addUserToDeviceGroup(token, userId, getToken(userId));
			}
			if (success)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}


	public boolean createDeviceGroup(String registrationTokens, int userId) {
		try {
			URL obj = new URL("https://android.googleapis.com/gcm/notification");
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//System.out.print("Auth key = " +this.authKey);
			//System.out.print("project Id = " +this.projectId);
			String basicAuth = "key=" + this.authKey;
			conn.setRequestProperty("project_id", this.projectId);
			conn.setRequestProperty("Authorization", basicAuth);
			String data = "{  \"operation\":\"create\",  \"notification_key_name\":\"dg" + userId + "\",  \"registration_ids\":[  \"" + registrationTokens + "\" ] }";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
//			Gson g = new Gson();
//			Token t = g.fromJson(readFullyAsString(conn.getInputStream(), "UTF-8"), Token.class);
//			System.out.println(t.getNotification_key());
//			
			JSONObject jsonObject = new JSONObject(readFullyAsString(conn.getInputStream(), "UTF-8"));
			String key = jsonObject.getString("notification_key");
			System.out.println(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addUserToDeviceGroup(String registrationTokens, int userId, String notificationKey) {
		try {
			URL obj = new URL("https://android.googleapis.com/gcm/notification");
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("project_id", this.projectId);
			conn.setRequestProperty("Authorization", "key=" + this.authKey);
			String data = "{  \"operation\":\"add\", \"notification_key\": \"" + notificationKey + "\" , \"notification_key_name\":\"dg" + userId + "\",  \"registration_ids\":[  \"" + registrationTokens + "\" ] }";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			JSONObject jsonObject = new JSONObject(readFullyAsString(conn.getInputStream(), "UTF-8"));
			String key = jsonObject.getString("notification_key");
			System.out.println(key);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public String pushMessage(String title, String message, String receiver) {
		try {
			String url = "https://fcm.googleapis.com/fcm/send";
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// System.out.print("Auth key = " +this.authKey);
			conn.setRequestProperty("Authorization", "Bearer " + this.authKey);
			String data = "{     \"notification\": {         \"title\": \"" + title + "\",         \"body\": \"" + message + "\",         \"icon\": \"/alarm.png\"     },     \"to\":          \"" + receiver + "\" }";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			System.out.println(readFullyAsString(conn.getInputStream(), "UTF-8"));
			return readFullyAsString(conn.getInputStream(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getToken(int userId) {
		try {
			URL obj = new URL("https://android.googleapis.com/gcm/notification?notification_key_name=dg" + userId);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			String myToken = "key=" + this.authKey;
			con.setRequestProperty("project_id", this.projectId);
			con.setRequestProperty("Authorization", myToken);
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				JSONObject jsonObject = new JSONObject(response.toString());
				return jsonObject.getString("notification_key");
			} else {
				return "Error :" + responseCode;
			}
		} catch (Exception ignored) {
		}
		return null;
	}

//	public void getTokenAlpha(int userId) {
//		try {
//			URL obj = new URL("https://android.googleapis.com/gcm/notification?notification_key_name=dg" + userId);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//			con.setRequestMethod("GET");
//			con.setRequestProperty("Content-Type", "application/json");
//			String myToken = "key=" + this.authKey;
//			con.setRequestProperty("project_id", this.projectId);
//			con.setRequestProperty("Authorization", myToken);
//			int responseCode = con.getResponseCode();
//			if (responseCode == HttpURLConnection.HTTP_OK) {
//				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//				//System.out.print(i);
//				String inputLine;
//				StringBuilder response = new StringBuilder();
//				while ((inputLine = in.readLine()) != null) {
//					//	System.out.print(inputLine);
//					response.append(inputLine);
//
//				}
//				in.close();
//				//	System.out.print(response);
//				JSONObject jsonObject = new JSONObject(response.toString());
//				String key = jsonObject.getString("notification_key");
//				System.out.print(key);
//
//			} else {
//				//	return "Error :"+responseCode;
//			}
//		} catch (Exception ignored) {
//		}
//		//	return null;
//	}


	public static String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
		return readFully(inputStream).toString(encoding);
	}

	private static ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		return byteArrayOutputStream;
	}

}
	 
