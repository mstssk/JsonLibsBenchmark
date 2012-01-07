package com.example.jpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import net.arnx.jsonic.JSON;
import net.vvakame.util.jsonpullparser.JsonFormatException;

import org.codehaus.jackson.JsonFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JppActivity extends Activity implements OnClickListener {

	TextView textRap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		findViewById(R.id.buttonJpp).setOnClickListener(this);
		findViewById(R.id.buttonGson).setOnClickListener(this);
		findViewById(R.id.buttonJsonic).setOnClickListener(this);
		findViewById(R.id.buttonJsonSimple).setOnClickListener(this);
		findViewById(R.id.buttonJsonLib).setOnClickListener(this);
		findViewById(R.id.buttonJackson).setOnClickListener(this);
		textRap = (TextView) findViewById(R.id.textRap);
		textRap.setText("");
	}

	@Override
	public void onClick(View v) {
		new ParseTask().execute(v.getId());
	}

	class ParseTask extends AsyncTask<Integer, Void, List<Long>> {

		List<Tweet> parse(int which, InputStream is) throws IOException,
				JsonFormatException, ParseException {
			switch (which) {
				case R.id.buttonJpp:
					// JsonArray array = JsonArray.fromParser(JsonPullParser
					// .newParser(is));
					return TweetGen.getList(is);
				case R.id.buttonGson:
					Type type = new TypeToken<List<Tweet>>() {}.getType();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "UTF-8"));
					Object fromJson = new Gson().fromJson(reader, type);
					return Arrays.asList(new GsonBuilder()
					// .setDateFormat("EE MMM dd HH:mm:ss Z yyyy")
					// .setDateFormat(DateFormat.FULL, DateFormat.FULL)
							.create().fromJson(reader, Tweet[].class));
				case R.id.buttonJsonic:
					// Object decode = JSON.decode(is);
					return Arrays.asList(JSON.decode(is, Tweet[].class));
				case R.id.buttonJsonSimple:
					new JSONParser().parse(new StreamSource(is).getReader());
					break;
				case R.id.buttonJsonLib:
					// Toast.makeText(getApplicationContext(), "よくわからん",
					// Toast.LENGTH_SHORT).show();
					break;
				case R.id.buttonJackson:
					Iterator<Tweet> values = new JsonFactory()
							.createJsonParser(is).readValuesAs(Tweet.class);
					List<Tweet> list = new ArrayList<Tweet>();
					while (values.hasNext()) {
						list.add(values.next());
					}
					return list;
				default:
					break;
			}
			return null;
		}

		String encode(int which, List<Tweet> list) throws IOException {
			String result = null;

			switch (which) {
				case R.id.buttonJpp:
					StringWriter sw = new StringWriter();
					TweetGen.encodeListNullToNull(sw, list);
					result = sw.toString();
					break;
				case R.id.buttonGson:
					Type type = new TypeToken<List<Tweet>>() {}.getType();
					result = new Gson().toJson(list, Tweet[].class);
					// result = new Gson().toJson(list);
					break;
				case R.id.buttonJsonic:
					result = JSON.encode(list);
					break;
				case R.id.buttonJsonSimple:
					break;
				case R.id.buttonJsonLib:
					break;
				case R.id.buttonJackson:
					break;
				default:
					break;
			}

			return result;

		}

		@Override
		protected List<Long> doInBackground(Integer... params) {

			InputStream is = getResources().openRawResource(
					R.raw.user_timeline_mini);

			int which = params[0];
			// List<Tweet> list = null;
			Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
			Object decode = null;

			Date start = new Date();
			try {
				// list = parse(which, is);
				// list = JsonArray.fromParser(JsonPullParser.newParser(is));
				// decode = JSON.decode(is);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				decode = new Gson().fromJson(reader, type);
				// if (list != null) {
				// Log.i("jpp", "size " + Integer.toString(list.size()));
				// }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long pasetime = new Date().getTime() - start.getTime();
			start = new Date();
			// JSON.encode(decode);
			new Gson().toJson(decode);

			// StringWriter writer = new StringWriter();
			// list.toJson(writer);
			// writer.toString();
			// encoded = encode(which, list);
			// if (encoded == null) {}

			// Log.i("jpp", encoded);

			long serializedtime = new Date().getTime() - start.getTime();

			return Arrays.asList(pasetime, serializedtime);
		}

		@Override
		protected void onPostExecute(List<Long> result) {
			StringBuilder str = new StringBuilder();
			str.append("\nparsed time  : " + result.get(0).toString()
					+ " \nserialize time : " + result.get(1).toString()
					+ "\n----------------");
			str.append(textRap.getText());
			textRap.setText(str.toString());
			// Log.i("jpp", "parsed time (sec) : " + result.toString());
		}
	}

}