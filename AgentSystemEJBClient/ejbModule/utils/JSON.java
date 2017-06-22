package utils;

import java.lang.reflect.Type;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSON {

	public static Object parse(String o, Class klass) {
		Gson gson = new GsonBuilder().create();
		try {
			return gson.fromJson(o, klass);
		} catch (Exception e) {
			Log.out("parse exception - " + e.getMessage());
			return null;
		}
	}

	public static Object parse(String o, Type klass) {
		Gson gson = new GsonBuilder().create();
		try {
			return gson.fromJson(o, klass);
		} catch (Exception e) {
			Log.out("parse exception - " + e.getMessage());
			return null;
		}
	}

	public static String stringify(Object o, MsgType type) {
		try {
			JSONObject obj = new JSONObject(o);

			JSONObject msg = new JSONObject();
			msg.put("type", type);
			msg.put("data", obj);
			return msg.toString();
		} catch (Exception e) {
			return null;
		}

		/*
		
		
		*/
	}
}
