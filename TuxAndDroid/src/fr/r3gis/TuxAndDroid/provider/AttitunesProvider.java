package fr.r3gis.TuxAndDroid.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class AttitunesProvider extends ContentProvider {
	private static final String DATABASE_NAME = "tuxanddroid.attitunes.db";
	private static final int DATABASE_VERSION = 1;
	private static final String ATTITUNES_TABLE_NAME = "attitunes";

	private static final int ATTITUNES = 1;
	private static final int ATTITUNE_ID = 2;

	private static final UriMatcher MATCHER;
	private static HashMap<String, String> ATTITUNES_LIST_PROJECTION;

	public static final class Attitunes implements BaseColumns {
		public static final Uri CONTENT_URI = Uri
				.parse("content://fr.r3gis.TuxAndDroid.provider/Attitunes");
		public static final String DEFAULT_SORT_ORDER = "name";
		public static final String FIELD_NAME = "name";
		public static final String FIELD_URL = "url";
	}

	static {
		MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		MATCHER.addURI("fr.r3gis.TuxAndDroid.provider",
				"Attitunes", ATTITUNES);
		MATCHER.addURI("fr.r3gis.TuxAndDroid.provider",
				"Attitunes/#", ATTITUNE_ID);

		ATTITUNES_LIST_PROJECTION = new HashMap<String, String>();
		ATTITUNES_LIST_PROJECTION.put(AttitunesProvider.Attitunes._ID,
				AttitunesProvider.Attitunes._ID);
		ATTITUNES_LIST_PROJECTION.put(AttitunesProvider.Attitunes.FIELD_NAME,
				AttitunesProvider.Attitunes.FIELD_NAME);
		ATTITUNES_LIST_PROJECTION.put(AttitunesProvider.Attitunes.FIELD_URL,
				AttitunesProvider.Attitunes.FIELD_URL);
	}

	public String getDbName() {
		return (DATABASE_NAME);
	}

	public int getDbVersion() {
		return (DATABASE_VERSION);
	}

	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Cursor c = db.rawQuery(
					"SELECT name FROM sqlite_master WHERE type='table' AND name='"
							+ ATTITUNES_TABLE_NAME + "'", null);

			try {
				// No table found, should be init state, create database and
				// populate it
				if (c.getCount() == 0) {
					db.execSQL("CREATE TABLE " + getTableName() + " ("
							+ Attitunes._ID
							+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
							+ Attitunes.FIELD_NAME + " TEXT,"
							+ Attitunes.FIELD_URL + " REAL" + ");");

					//Populate defaults 
					ContentValues cv = new ContentValues();

					cv.put(Attitunes.FIELD_NAME, "Joke 1");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/joke1.att");
					db.insert(getTableName(), getNullColumnHack(), cv);

					cv.put(Attitunes.FIELD_NAME, "Joke 2");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/joke2.att");
					db.insert(getTableName(), getNullColumnHack(), cv);

					cv.put(Attitunes.FIELD_NAME, "Joke 3");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/joke3.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					cv.put(Attitunes.FIELD_NAME, "Seal");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/seal.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					
					cv.put(Attitunes.FIELD_NAME, "Yoga");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/yoga.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					cv.put(Attitunes.FIELD_NAME, "Strike");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/strike.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					cv.put(Attitunes.FIELD_NAME, "Unwanted mail");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/unwantedmail.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					
					cv.put(Attitunes.FIELD_NAME, "Aerobics");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/aero.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					
					cv.put(Attitunes.FIELD_NAME, "Mc Hammer");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/hammer.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					
					cv.put(Attitunes.FIELD_NAME, "Lady Bug");
					cv.put(Attitunes.FIELD_URL,
							"http://www.livewithapenguin.com/attitunes/attitunes/ladybug.att");
					db.insert(getTableName(), getNullColumnHack(), cv);
					
					
				}
			} finally {
				c.close();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			android.util.Log.w("AttitunesProvider",
					"Upgrading database, which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + getTableName());
			onCreate(db);
		}
	}

	private SQLiteDatabase db;

	@Override
	public boolean onCreate() {
		db = (new DatabaseHelper(getContext())).getWritableDatabase();
		return (db == null) ? false : true;
	}

	@Override
	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(getTableName());

		if (isCollectionUri(url)) {
			qb.setProjectionMap(getDefaultProjection());
		} else {
			qb.appendWhere(getIdColumnName() + "="
					+ url.getPathSegments().get(1));
		}

		String orderBy;

		if (TextUtils.isEmpty(sort)) {
			orderBy = getDefaultSortOrder();
		} else {
			orderBy = sort;
		}

		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	@Override
	public String getType(Uri url) {
		if (isCollectionUri(url)) {
			return (getCollectionType());
		}

		return (getSingleType());
	}

	@Override
	public Uri insert(Uri url, ContentValues initialValues) {
		long rowID;
		ContentValues values;

		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		if (!isCollectionUri(url)) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		for (String colName : getRequiredColumns()) {
			if (values.containsKey(colName) == false) {
				throw new IllegalArgumentException("Missing column: " + colName);
			}
		}

		rowID = db.insert(getTableName(), getNullColumnHack(), values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(getContentUri(), rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}

		throw new SQLException("Failed to insert row into " + url);
	}

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		int count;

		if (isCollectionUri(url)) {
			count = db.delete(getTableName(), where, whereArgs);
		} else {
			String segment = url.getPathSegments().get(1);
			count = db.delete(getTableName(),
					getIdColumnName()
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	@Override
	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		int count;

		if (isCollectionUri(url)) {
			count = db.update(getTableName(), values, where, whereArgs);
		} else {
			String segment = url.getPathSegments().get(1);
			count = db.update(getTableName(), values,
					getIdColumnName()
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	private boolean isCollectionUri(Uri url) {
		return (MATCHER.match(url) == ATTITUNES);
	}

	private HashMap<String, String> getDefaultProjection() {
		return (ATTITUNES_LIST_PROJECTION);
	}

	private String getTableName() {
		return (ATTITUNES_TABLE_NAME);
	}

	private String getIdColumnName() {
		return (Attitunes._ID);
	}

	private String getDefaultSortOrder() {
		return (Attitunes.FIELD_NAME);
	}

	private String getCollectionType() {
		return ("vnd.r3gis.cursor.dir/vnd.tuxanddroid.attitune");
	}

	private String getSingleType() {
		return ("vnd.r3gis.cursor.item/vnd.tuxanddroid.attitune");
	}

	private String[] getRequiredColumns() {
		return (new String[] { Attitunes.FIELD_NAME, Attitunes.FIELD_URL });
	}

	private String getNullColumnHack() {
		return (Attitunes.FIELD_NAME);
	}

	private Uri getContentUri() {
		return (Attitunes.CONTENT_URI);
	}
}
