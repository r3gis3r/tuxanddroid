package fr.r3gis.TuxAndDroid.views;

import fr.r3gis.TuxAndDroid.R;
import fr.r3gis.TuxAndDroid.provider.AttitunesProvider;
import fr.r3gis.TuxAndDroid.service.ApiConnector;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class AttitunesList extends Activity {
	private static final int ADD_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private static final int DELETE_ID = Menu.FIRST + 3;
	private static final int LAUNCH_ID = Menu.FIRST + 4;
	private static final int CLOSE_ID = Menu.FIRST + 5;

	
	private static final String[] PROJECTION = new String[] {
			AttitunesProvider.Attitunes._ID,
			AttitunesProvider.Attitunes.FIELD_NAME,
			AttitunesProvider.Attitunes.FIELD_URL };
	private Cursor attituneCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.attitunes);
		
		attituneCursor = managedQuery(AttitunesProvider.Attitunes.CONTENT_URI,
				PROJECTION, null, null, null);

		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.row,
				attituneCursor, new String[] {
						AttitunesProvider.Attitunes.FIELD_NAME,
						AttitunesProvider.Attitunes.FIELD_URL }, new int[] {
						R.id.row_name, R.id.row_url });
		
		ListView list_view = (ListView) findViewById(R.id.AttitunesList);
		
		list_view.setAdapter(adapter);
		registerForContextMenu(list_view);
		
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				attituneCursor.moveToPosition(position);
				Log.i("Attitune list", attituneCursor.getString(2));
				processLaunch(attituneCursor.getString(2));
				
			}
		});
		
		
		Button cancel_btn = (Button) findViewById(R.id.CancelAttitunes);
		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, ADD_ID, Menu.NONE, "Add").setIcon(
				android.R.drawable.ic_menu_add).setAlphabeticShortcut('a');
		menu.add(Menu.NONE, CLOSE_ID, Menu.NONE, "Stop current attitune").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel)
				.setAlphabeticShortcut('s');

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADD_ID:
			add();
			return (true);

		case CLOSE_ID:
			processStopAtt();
			return (true);
			
		}

		return (super.onOptionsItemSelected(item));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, EDIT_ID, Menu.NONE, "Edit").setIcon(
				android.R.drawable.ic_menu_edit).setAlphabeticShortcut('e');
		menu.add(Menu.NONE, LAUNCH_ID, Menu.NONE, "Launch").setIcon(
				android.R.drawable.ic_menu_view).setAlphabeticShortcut('l');
		menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Delete").setIcon(
				android.R.drawable.ic_menu_delete).setAlphabeticShortcut('d');
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		switch (item.getItemId()) {
		case LAUNCH_ID:
			info = (AdapterContextMenuInfo) item.getMenuInfo();

			launch(info.id);
			return true;
		case EDIT_ID:
			info = (AdapterContextMenuInfo) item.getMenuInfo();

			edit(info.id);
			return true;
		case DELETE_ID:
			info = (AdapterContextMenuInfo) item.getMenuInfo();

			delete(info.id);
			return true;
		}

		return (super.onOptionsItemSelected(item));
	}

	private void add() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View addView = inflater.inflate(R.layout.add_edit, null);
		final DialogWrapper wrapper = new DialogWrapper(addView);

		new AlertDialog.Builder(this).setTitle(R.string.add_attitune_title)
				.setView(addView).setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								processAdd(wrapper);
							}
						}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// ignore, just dismiss
							}
						}).show();
	}
	
	private void edit(final long rowId){
		LayoutInflater inflater = LayoutInflater.from(this);
		View addView = inflater.inflate(R.layout.add_edit, null);
		final DialogWrapper wrapper = new DialogWrapper(addView);
		

		new AlertDialog.Builder(this).setTitle(R.string.edit_attitune_title)
				.setView(addView).setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								processUpdate(rowId, wrapper);
							}
						}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// ignore, just dismiss
							}
						}).show();
		//Fill fields with good vals
		Cursor c = getContentResolver().query(Uri.withAppendedPath(AttitunesProvider.Attitunes.CONTENT_URI, ""+rowId), PROJECTION, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			wrapper.nameField.setText(c.getString(1));
			wrapper.urlField.setText(c.getString(2));
		}else{
			Log.w("AttituneList", "Error a improbable attitune was clicked");
		}
		c.close();
		
	}
	
	private void launch(final long rowId){
		Cursor c = getContentResolver().query(Uri.withAppendedPath(AttitunesProvider.Attitunes.CONTENT_URI, ""+rowId), PROJECTION, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			String url = c.getString(2);
			processLaunch(url);
		}else{
			Log.w("AttituneList", "Error a improbable attitune was clicked");
		}
		c.close();
	}

	private void delete(final long rowId) {
		if (rowId > 0) {
			new AlertDialog.Builder(this).setTitle(
					R.string.delete_attitune_title).setPositiveButton(
					R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							processDelete(rowId);
						}
					}).setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// ignore, just dismiss
						}
					}).show();
		}
	}

	private void processAdd(DialogWrapper wrapper) {
		ContentValues values = new ContentValues(2);

		values.put(AttitunesProvider.Attitunes.FIELD_NAME, wrapper.getName());
		values.put(AttitunesProvider.Attitunes.FIELD_URL, wrapper.getUrl());

		getContentResolver().insert(AttitunesProvider.Attitunes.CONTENT_URI,
				values);
		attituneCursor.requery();
	}

	private void processLaunch(String url){
		ApiConnector.tuxPlayAttitune(url);
	}
	
	private void processStopAtt(){
		ApiConnector.tuxStopAttitune();
	}
	
	private void processUpdate(long rowId, DialogWrapper wrapper){
		ContentValues values = new ContentValues(2);

		values.put(AttitunesProvider.Attitunes.FIELD_NAME, wrapper.getName());
		values.put(AttitunesProvider.Attitunes.FIELD_URL, wrapper.getUrl());

		getContentResolver().update(Uri.withAppendedPath(AttitunesProvider.Attitunes.CONTENT_URI, ""+rowId),
				values, null, null);
		attituneCursor.requery();
	}
	
	private void processDelete(long rowId) {
		Uri uri = ContentUris.withAppendedId(
				AttitunesProvider.Attitunes.CONTENT_URI, rowId);
		getContentResolver().delete(uri, null, null);
		attituneCursor.requery();
	}

	class DialogWrapper {
		EditText nameField = null;
		EditText urlField = null;
		View base = null;

		DialogWrapper(View base) {
			this.base = base;
			nameField = (EditText) base.findViewById(R.id.edit_name);
			urlField = (EditText) base.findViewById(R.id.edit_url);
		}

		String getName() {
			return (nameField.getText().toString());
		}

		String getUrl() {
			return (urlField.getText().toString());
		}

	}
}