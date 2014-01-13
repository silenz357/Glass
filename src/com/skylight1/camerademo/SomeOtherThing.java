package com.skylight1.camerademo;



import java.io.File;
import java.io.IOException;

import com.google.android.glass.media.CameraManager;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SomeOtherThing extends Activity implements SurfaceHolder.Callback{
SurfaceView surfaceView;
SurfaceHolder surfaceHolder;
Camera camera;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_some_other_thing);
			surfaceView = (SurfaceView)findViewById(R.id.cpPreview);
		
		
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
	}
	 private static final int TAKE_PICTURE_REQUEST = 1;

	 private void takePicture() {
	     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	     startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	 }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.some_other_thing, menu);
		return true;
	}
	public Camera getCamera()
	{

	    for(int i = 0; i < Camera.getNumberOfCameras(); i++)
	        return Camera.open(i);

	    return null;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
              //openOptionsMenu();
              return true;
          }else if (keyCode == KeyEvent.KEYCODE_CAMERA){
        	  
        	  camera.stopPreview();
        	  camera.release();
        	  camera = null;
        	  
        	  takePicture();
        	  return false;
        	  
          }
          return super.onKeyDown(keyCode, event);
          //return false;
    }
	

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
        String picturePath = data.getStringExtra(
                CameraManager.EXTRA_PICTURE_FILE_PATH);
        processPictureWhenReady(picturePath);
    }

    super.onActivityResult(requestCode, resultCode, data);
}

private void processPictureWhenReady(final String picturePath) {
    final File pictureFile = new File(picturePath);

    if (pictureFile.exists()) {
        // The picture is ready; process it.
    } else {
        // The file does not exist yet. Before starting the file observer, you
        // can update your UI to let the user know that the application is
        // waiting for the picture (for example, by displaying the thumbnail
        // image and a progress indicator).

        final File parentDirectory = pictureFile.getParentFile();
        FileObserver observer = new FileObserver(parentDirectory.getPath()) {
            // Protect against additional pending events after CLOSE_WRITE is
            // handled.
            private boolean isFileWritten;

            @Override
            public void onEvent(int event, String path) {
                if (!isFileWritten) {
                    // For safety, make sure that the file that was created in
                    // the directory is actually the one that we're expecting.
                    File affectedFile = new File(parentDirectory, path);
                    isFileWritten = (event == FileObserver.CLOSE_WRITE
                            && affectedFile.equals(pictureFile));

                    if (isFileWritten) {
                        stopWatching();

                        // Now that the file is ready, recursively call
                        // processPictureWhenReady again (on the UI thread).
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processPictureWhenReady(picturePath);
                            }
                        });
                    }
                }
            }
        };
        observer.startWatching();}
    }
	
	
	public void surfaceCreated(SurfaceHolder holder) {
		
		
		camera = getCamera();
		
		
		Camera.Parameters params = camera.getParameters();
		params.setPreviewFpsRange(30000,  30000);
		params.setPreviewSize(640,  360);
		camera.setParameters(params);
		
		
		
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//camera.setPreviewDisplay(surfaceHolder);
		camera.startPreview();
		
		
	}
	// TODO Auto-generated method stub
	
	
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
		
	}

		
		public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		}
}
