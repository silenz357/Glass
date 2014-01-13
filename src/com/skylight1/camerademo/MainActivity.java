package com.skylight1.camerademo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.google.android.glass.app.Card;
import com.google.android.glass.media.CameraManager;
import com.google.android.glass.timeline.TimelineManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Process;


public class MainActivity extends Activity {

	Camera camera;
	Bitmap image;
	
	
	public Bitmap getBitmapFromURL(String src){
		
		try{
			URL url = new URL(src);
			Log.v(TAG, "URLGood");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			Log.v(TAG, "URLGood");
			connection.setDoInput(true);
			Log.v(TAG, "hello");
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			Log.v(TAG, "clear");
			return myBitmap;
			
			
		}catch (IOException ex){
			
			Log.v(TAG, ex.getMessage());
			//ex.printStackTrace();
			return null;
		}
		
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//android.os.Debug.waitForDebugger();
		setContentView(R.layout.activity_main);
		
		
		//Camera.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
	              openOptionsMenu();
	              return true;
	          }else if (keyCode == KeyEvent.KEYCODE_CAMERA){
	        	  
	        	  
	          }
	          return super.onKeyDown(keyCode, event);
	          //return false;
	    }
	 
	 private static final int TAKE_PICTURE_REQUEST = 1;

	 private void takePicture() {
	     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	     startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	 }
	 
	 @Override
	    public boolean  onOptionsItemSelected (MenuItem item) {
	    	
	    	int id = item.getItemId();
	    	
	    	TextView t = (TextView)findViewById(R.id.mytextbox); 
	    	
	    	switch(id){
	    	
	    	case R.id.lastPicture:
	    		
	    		
	    		
	    	   
	    	    String[] projection = new String[]{MediaStore.Images.ImageColumns._ID,MediaStore.Images.ImageColumns.DATA,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.MIME_TYPE};     
	    	    final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"); 
	    	    if(cursor != null){
	    	    	cursor.moveToFirst();
	    	    	String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
	    	    	t.setText(path);
	    	    	
	    	    	ImageView imageView = (ImageView)findViewById(R.id.imageView1);
	    	    	
	    	    	image = BitmapFactory.decodeFile(path);
	    	    	
	    	    	
	    	    	
	    	    	int nh = (int) ( image.getHeight() * (512.0 / image.getWidth()) );
	    	    	Bitmap scaled = Bitmap.createScaledBitmap(image, 512, nh, true);
	    	    	imageView.setImageBitmap(scaled);
	    	    	
	    	    	
	    	    }


	    	    //displaySpeechRecognizer();
	    		break;
	    		
	    	case R.id.staticCard:
	    		
	    		Card card1 = new Card(getBaseContext());
	    		card1.setText("This card has a footer.");
	    		card1.setFootnote("I'm the footer!");
	    		View card1View = card1.toView();
	    		
	    		TimelineManager tm = TimelineManager.from(this);
	    	     tm.insert(card1);
	    		t.setText("Static Card Added to timeline");
	    		break;
				    		
			case R.id.downloadImage:
				
				if (VERBOSE) Log.v(TAG, "- Tony -");
				
				new LongOperation().execute("");
    	    	
    	    	//int nh = (int) ( image.getHeight() * (512.0 / image.getWidth()) );
    	    	//Bitmap scaled = Bitmap.createScaledBitmap(image, 512, nh, true);
    	    	//imageView.setImageBitmap(scaled);
				t.setText("Operation Started");
				    		break;
				    		
			case R.id.speechRecognizer:
				t.setText("Launch Speech Intent ");
				displaySpeechRecognizer();
				break;
	    		
	    	}	
	    	
	    	return true;
	    	
	    }
	 private static final int SPEECH_REQUEST = 0;

	    private void displaySpeechRecognizer() {
	        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        startActivityForResult(intent, SPEECH_REQUEST);
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode,
	            Intent data) {
	        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
	            List<String> results = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	            String spokenText = results.get(0);
	            
	            TextView t =(TextView)findViewById(R.id.mytextbox); 
	    	    t.setText(spokenText);
	    	   
	            // Do something with spokenText.
	        }
	        
	       
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	    
	    private static final String TAG = "SampleActivity";

	    /**
	     * Toggle this boolean constant's value to turn on/off logging
	     * within the class. 
	     */
	    private static final boolean VERBOSE = true;
	    
	    @Override
	    public void onResume() {
	        super.onResume();
	        if (VERBOSE) Log.v(TAG, "+ ON RESUME +");
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        if (VERBOSE) Log.v(TAG, "- ON PAUSE -");
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	        if (VERBOSE) Log.v(TAG, "-- ON STOP --");
	    }

	   @Override
	    public void onDestroy() {
	        super.onDestroy();
	        if (VERBOSE) Log.v(TAG, "- ON DESTROY -");
	        Process.killProcess(Process.myPid());
	        //finish();
	    }
	   
	   private class LongOperation extends AsyncTask<String, Void, String> {

	        @Override
	        protected String doInBackground(String... params) {
	            
	                //try {
	                	//ImageView imageView = (ImageView)findViewById(R.id.imageView1);
	        	    	//imageView.setImageBitmap(bImage);
	        	    	//LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        	    	image = getBitmapFromURL("http://www.noisecontrol.com/wp-content/uploads/2013/04/noise11.jpg");
	        	    	//image = getBitmapFromURL("glass://map?w=320&h=240&marker=0;latitude,longitude&marker=1;latitude,longitude&polyline=;latitude,longitude,latitude,longitude");
	        	    	//http://www.todayifoundout.com/wp-content/uploads/2010/10/milk.jpg
	        	    	
	                	
	                	
	                    //Thread.sleep(1000);
	                //} catch (InterruptedException e) {
	                //    e.printStackTrace();
	                //}
	            
	            return "Executed";
	        }

	        @Override
	        protected void onPostExecute(String result) {
	            //TextView txt = (TextView) findViewById(R.id.output);
	           // txt.setText("Executed"); // txt.setText(result);
	            // might want to change "executed" for the returned string passed
	            // into onPostExecute() but that is upto you
	        	if (VERBOSE) Log.v(TAG, "- Get Bitmap from url -");
	        	
	        	TextView t = (TextView)findViewById(R.id.mytextbox);
	        	ImageView imageView = (ImageView)findViewById(R.id.imageView1);
	        	imageView.setImageBitmap(image);
	        	
	        	t.setText("Image Download Complete");
	        }

	        @Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
	   
	   String mCurrentPhotoPath;

	   private File createImageFile() throws IOException {
	       // Create an image file name
	       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	       String imageFileName = "JPEG_" + timeStamp + "_";
	       File storageDir = Environment.getExternalStoragePublicDirectory(
	               Environment.DIRECTORY_PICTURES);
	       File image = File.createTempFile(
	           imageFileName,  /* prefix */
	           ".jpg",         /* suffix */
	           storageDir      /* directory */
	       );

	       // Save a file: path for use with ACTION_VIEW intents
	       mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	       return image;
	   }
}
