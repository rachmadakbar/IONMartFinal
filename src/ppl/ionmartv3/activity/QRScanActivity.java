package ppl.ionmartv3.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.IOException;

import ppl.ionmartv3.R;
import ppl.ionmartv3.activity.helper.Global;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;
import android.app.Activity;
import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.Toast;

import android.widget.TextView;
import android.graphics.ImageFormat;

public class QRScanActivity extends Activity {
	private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private boolean enableCapture =false;

    TextView scanText;
    Button scanButton;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.qrscan_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanText = (TextView)findViewById(R.id.scanText);

        scanButton = (Button)findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    //if (barcodeScanned) {
                        //barcodeScanned = false;
                        scanText.setText("Scanning...");
                        enableCapture = true;
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        previewing = true;
                        mCamera.autoFocus(autoFocusCB);
                    //}
                }
            });
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

    PreviewCallback previewCb = new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                if(enableCapture)
                {   	
	                Image barcode = new Image(size.width, size.height, "Y800");
	                barcode.setData(data);
	                
	                int result = scanner.scanImage(barcode);
	                
	                if (result != 0) {
	                    previewing = false;
	                    mCamera.setPreviewCallback(null);
	                    mCamera.stopPreview();
	                    
	                    SymbolSet syms = scanner.getResults();
	                    Symbol sym = (Symbol)syms.toArray()[0];
	                    //barcodeScanned = true;
	                    new Transite().execute(sym.getData());
	                }
	                enableCapture = false;
                }
            }
        };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };
	
	
	
	
	
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	    private SurfaceHolder mHolder;
	    private Camera mCamera;
	    private PreviewCallback previewCallback;
	    private AutoFocusCallback autoFocusCallback;

	    public CameraPreview(Context context, Camera camera,
	                         PreviewCallback previewCb,
	                         AutoFocusCallback autoFocusCb) {
	        super(context);
	        mCamera = camera;
	        previewCallback = previewCb;
	        autoFocusCallback = autoFocusCb;

	        /* 
	         * Set camera to continuous focus if supported, otherwise use
	         * software auto-focus. Only works for API level >=9.
	         */
	        /*
	        Camera.Parameters parameters = camera.getParameters();
	        for (String f : parameters.getSupportedFocusModes()) {
	            if (f == Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
	                mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
	                autoFocusCallback = null;
	                break;
	            }
	        }
	        */

	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        mHolder.addCallback(this);

	        // deprecated setting, but required on Android versions prior to 3.0
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }

	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, now tell the camera where to draw the preview.
	        try {
	            mCamera.setPreviewDisplay(holder);
	        } catch (IOException e) {
	            Log.d("DBG", "Error setting camera preview: " + e.getMessage());
	        }
	    }

	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // Camera preview released in activity
	    }

	    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	        /*
	         * If your preview can change or rotate, take care of those events here.
	         * Make sure to stop the preview before resizing or reformatting it.
	         */
	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	            mCamera.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }

	        try {
	            // Hard code camera surface rotation 90 degs to match Activity view in portrait
	            mCamera.setDisplayOrientation(90);

	            mCamera.setPreviewDisplay(mHolder);
	            mCamera.setPreviewCallback(previewCallback);
	            mCamera.startPreview();
	            mCamera.autoFocus(autoFocusCallback);
	        } catch (Exception e){
	            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
	        }
	    }
	}
	//ASYNC TASK TO AVOID CHOKING UP UI THREAD DOWNLOAD STRING
		private class Transite extends AsyncTask<String, String, String> {
			String selectedid = "";

		    @Override
		    protected void onPreExecute() {
		    }

		    protected String doInBackground(String... param) {
	        	Log.e("test", Global.sendCommand(Global.server+"services.php?ct=check_product&id="+param[0]));
		        	String response = Global.sendCommand(Global.server+"services.php?ct=check_product&id="+param[0]);
		        	selectedid = param[0];
		        	return response;
		    }

		    protected void onProgressUpdate(String... progress) {
		    }

		    protected void onPostExecute(String ret) {
		    	if(ret.contains("true")){
                    Intent intent = new Intent(getApplicationContext(), DetailProductActivity.class);
                    intent.putExtra("selectedid", selectedid);
                    startActivity(intent);
                    finish();
		    	}
		    	if(ret.contains("false"))
		    	{
                    previewing = true;
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
		    		Toast.makeText(getApplicationContext(), "Product not found",Toast.LENGTH_SHORT).show();
		    	}
		    }
		}

}
