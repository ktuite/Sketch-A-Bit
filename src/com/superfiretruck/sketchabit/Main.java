package com.superfiretruck.sketchabit;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.provider.*;
import android.content.*;
import android.net.*;
import android.util.*;

import java.io.*;
import java.net.*;


public class Main extends Activity {
	SketchView sketchView;
	String parentId;
	Handler handler;
	
	static final int PICK_IMAGE_REQUEST = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        
        setContentView(R.layout.sketch_layout);
        sketchView = (SketchView)findViewById(R.id.sketch);
        
        Button light = (Button)findViewById(R.id.light);
        Button dark = (Button)findViewById(R.id.dark);
        
        light.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sketchView.useLighterColor();
			}
		});
        
        dark.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { sketchView.useDarkerColor(); }
		});
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	parentId = savedInstanceState.getString("parentId");
    	Bitmap bitmap = savedInstanceState.getParcelable("bitmap");
    	sketchView.importBitmap(bitmap);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(parentId == null) {
    		downloadImageFromServer();
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putString("parentId", parentId);
    	savedInstanceState.putParcelable("bitmap", sketchView.exportBitmap());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.sketch_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	switch(menuItem.getItemId()) {
    	case R.id.revert:
    		sketchView.clear();
    		return true;
    	case R.id.fetch:
    		downloadImageFromServer();
    		return true;
    	case R.id.upload:
    		uploadImageToServer();
    		return true;
    	case R.id.export:
    		exportImageToGallery();
    		return true;
    	}
    	
    	return false;
    }
    
    public void exportImageToGallery(){
    	final ProgressDialog progress = new ProgressDialog(Main.this);
    	progress.setIndeterminate(true);
    	progress.setMessage("Saving!");
    	progress.show();
    	
    	new Thread(){
    		public void run(){
    			Bitmap exported = sketchView.exportBitmap();
    			MediaStore.Images.Media.insertImage(getContentResolver(), exported, "myimage", "imagedesc");
    			progress.dismiss();
    		}
    	}.start();
    }
    
    public void loadImageFromGallery(){
    	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    	photoPickerIntent.setType("image/*");
    	startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if (requestCode == PICK_IMAGE_REQUEST){
    		if (resultCode == RESULT_OK){
    			Uri uri = data.getData();
    			try {
					InputStream inStream = getContentResolver().openInputStream(uri);
	    			Bitmap imported = BitmapFactory.decodeStream(inStream);
	    			inStream.close();
	    			sketchView.importBitmap(imported.copy(Bitmap.Config.ARGB_8888, true));
	    			parentId = "";
	    			
				} catch (Exception e) {
					Log.e("panda","failed to import!!",e);
				}
    			
    		}
    	}
    }
    
    public void uploadImageToServer(){
    	final ProgressDialog progress = new ProgressDialog(Main.this);
    	progress.setIndeterminate(true);
    	progress.setMessage("Uploading Image...");
    	progress.show();
    	
    	new Thread(){
    		public void run(){
    			try {
					Util.uploadBitmap(sketchView.exportBitmap(),parentId);
					handler.post(new Runnable() { public void run() { downloadImageFromServer();}});
				} catch (Exception e) {
					Log.e("panda", "problem uploading", e);
				} finally {
	    			progress.dismiss();
				}
       		}
    	}.start();
    }
    
    public void downloadImageFromServer(){
    	final ProgressDialog progress = new ProgressDialog(Main.this);
    	progress.setIndeterminate(true);
    	progress.setMessage("Downloading Random Image...");
    	progress.show();
    	
    	new Thread(){
    		public void run(){
    			try {
					InputStream inStream = null;
					inStream = (InputStream)new URL("http://superfiretruck.com/sketchabit/download.php").getContent();
					String imageLoc = new BufferedReader(new InputStreamReader(inStream)).readLine();
					
					Log.d("panda", "imLsd: "+imageLoc);
					inStream = (InputStream)new URL(imageLoc).getContent();
					Bitmap imported = BitmapFactory.decodeStream(inStream);
	    			inStream.close();
	    			sketchView.importBitmap(imported.copy(Bitmap.Config.ARGB_8888, true));
	    			parentId = imageLoc.substring(imageLoc.lastIndexOf('/')+1, imageLoc.lastIndexOf('.'));
				} catch (Exception e) {
					Log.e("panda", "problem downloading", e);
				} finally {
	    			progress.dismiss();
				}
       		}
    	}.start();
    }
}

