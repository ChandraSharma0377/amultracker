package com.amul.dc.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Commons {

	private static final String BASE_URL = "http://13.126.111.240/public/api/";
	public static final String LOGIN_URL = BASE_URL + "login/sign";
	public static final String GET_ALL_CITIES = BASE_URL + "city/all";
	public static final String SEARCH_CITIES = BASE_URL + "city/search";
	public static final String ADD_CITY = BASE_URL + "city/add";

	public static final String SEARCH_ROUTES = BASE_URL + "route/search";
	public static final String ADD_ROUTE = BASE_URL + "route/add";
	public static final String ADD_DC_DETAILS = BASE_URL + "poi/add";
	public static final String GET_ROUTE_DC_DETAILS = BASE_URL + "poi/route";

	//http://13.126.111.240/public/api/poi/route

	//http://13.126.111.240/public/api/poi/add

	//http://13.126.111.240/public/api/route/search	POST	city_id,route_name
	//Add route to a specific city	http://13.126.111.240/public/api/route/add	POST	city_id, route_name, user_id


//	To login	http://13.126.111.240/public/api/login/sign	POST	email , password
//	To all city list	http://13.126.111.240/public/api/city/all	GET
//	Search city	http://13.126.111.240/public/api/city/search	POST	city_name
//	Add city	http://13.126.111.240/public/api/city/add	POST	city_name

	public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}
}
