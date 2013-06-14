package com.otaserver.client;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.otaserver.client.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
//filemanagement-通过listview显示文件目录结构
public class FileManagarActivity extends Activity{
	private List<Map<String, Object>> mData;
	private String rootPath = Environment.getExternalStorageDirectory().getPath();
	private String mDir = rootPath ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filemanagar) ;
		
		Intent intent = this.getIntent();
		Uri uri = intent.getData();
		mDir = uri.getPath();
		mData = getData();
		final ListView listview = (ListView)findViewById(R.id.sdcard_file_lv) ;
		MyAdapter adapter = new MyAdapter(this);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if ((Integer) mData.get(position).get("img") == R.drawable.icon_folder) {
					mDir = (String) mData.get(position).get("info");
					mData = getData();
					MyAdapter adapter = new MyAdapter(FileManagarActivity.this);
					listview.setAdapter(adapter);
				} else {
				
					finishWithResult((String) mData.get(position).get("info"));
				}
			}
			
		}) ;
	}
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(mDir);
		File[] files = f.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if(pathname.isDirectory())
					return true ;
			    else if(pathname.isFile()&&pathname.getName().endsWith(".zip"))
			    	return true ;

				return false;
			}
		});
		
		if (!mDir.equals(rootPath)) {
			map = new HashMap<String, Object>();
			map.put("title", "Back to ../");
			map.put("info", f.getParent());
			map.put("img", R.drawable.icon_folder);
			list.add(map);
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				map = new HashMap<String, Object>();
				map.put("title", files[i].getName());
				map.put("info", files[i].getPath());
				if (files[i].isDirectory())
					map.put("img", R.drawable.icon_folder);
				else
					map.put("img", R.drawable.icon_zip);
				list.add(map);
			}
		}
		return list;
	}
	

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_sdcardupate_file_tv, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get(
					"img"));
			holder.title.setText((String) mData.get(position).get("title"));
			holder.info.setText((String) mData.get(position).get("info"));
			return convertView;
		}
	}

	private void finishWithResult(String path) {
		Bundle conData = new Bundle();
		conData.putString("results", "Thanks Thanks");
		Intent intent = new Intent();
		intent.putExtras(conData);
		Uri startDir = Uri.fromFile(new File(path));
		intent.setDataAndType(startDir,
				"vnd.android.cursor.dir/lysesoft.andexplorer.file");
		setResult(RESULT_OK, intent);
		finish();
	}
	
}
