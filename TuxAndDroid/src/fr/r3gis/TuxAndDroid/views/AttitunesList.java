package fr.r3gis.TuxAndDroid.views;

import fr.r3gis.TuxAndDroid.R;
import fr.r3gis.TuxAndDroid.provider.AttitunesProvider;
import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AttitunesList extends ListActivity {
	private static final int ADD_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private static final int DELETE_ID = Menu.FIRST + 3;
	//private static final int LAUNCH_ID = Menu.FIRST + 4;
	private static final int CLOSE_ID = Menu.FIRST + 5;

	
	private static final String[] PROJECTION = new String[] {
			AttitunesProvider.Attitunes._ID,
			AttitunesProvider.Attitunes.FIELD_NAME,
			AttitunesProvider.Attitunes.FIELD_URL };
	private Cursor constantsCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		constantsCursor = managedQuery(AttitunesProvider.Attitunes.CONTENT_URI,
				PROJECTION, null, null, null);

		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.row,
				constantsCursor, new String[] {
						AttitunesProvider.Attitunes.FIELD_NAME,
						AttitunesProvider.Attitunes.FIELD_URL }, new int[] {
						R.id.row_name, R.id.row_url });

		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, ADD_ID, Menu.NONE, "Add").setIcon(
				android.R.drawable.ic_menu_add).setAlphabeticShortcut('a');
		menu.add(Menu.NONE, CLOSE_ID, Menu.NONE, "Close").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel)
				.setAlphabeticShortcut('c');

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADD_ID:
			add();
			return (true);

		case CLOSE_ID:
			finish();
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, EDIT_ID, Menu.NONE, "Edit").setIcon(
				android.R.drawable.ic_menu_edit).setAlphabeticShortcut('e');
		menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Delete").setIcon(
				android.R.drawable.ic_menu_delete).setAlphabeticShortcut('d');
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		switch (item.getItemId()) {
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
		constantsCursor.requery();
	}

	private void processUpdate(long rowId, DialogWrapper wrapper){
		ContentValues values = new ContentValues(2);

		values.put(AttitunesProvider.Attitunes.FIELD_NAME, wrapper.getName());
		values.put(AttitunesProvider.Attitunes.FIELD_URL, wrapper.getUrl());

		getContentResolver().update(Uri.withAppendedPath(AttitunesProvider.Attitunes.CONTENT_URI, ""+rowId),
				values, null, null);
		constantsCursor.requery();
	}
	
	private void processDelete(long rowId) {
		Uri uri = ContentUris.withAppendedId(
				AttitunesProvider.Attitunes.CONTENT_URI, rowId);
		getContentResolver().delete(uri, null, null);
		constantsCursor.requery();
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