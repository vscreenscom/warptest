package org.test.warptest;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class BootstrapActivity extends Activity {
	private TextView mText;
	private ScrollView mScroll;
	private StringBuilder mScrollContent = new StringBuilder();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mText = (TextView) findViewById(R.id.text);
        mScroll = (ScrollView) findViewById(R.id.scroll);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		// put bootstrap code here,

		// use getAssetInputStream() to get the bytes 
		// from objects in the "assets" folder
		// you can put other images there as needed
		
		// use log() to write a message to the screen, for debugging
		// purposes. you can also use android.os.Log to write to the
		// android log in stead, if you like
		
		// for example,
		
		InputStream is = null;
		String assetName = "moorwen_textured.jpg";
		try {
			is = getAssetInputStream(assetName);
			log("got input stream for asset: " + assetName);
		} catch (IOException e) {
			log("failed to get input stream for asset: " + assetName);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// put cleanup / shutdown code here
	}

	private InputStream getAssetInputStream(String name) throws IOException {
    	InputStream is = this.getAssets().open(name);
    	return is;
    }
    
    private void log(String msg) {
    	mScrollContent.append(msg);
    	mScrollContent.append("\n");
    	mText.setText(mScrollContent.toString());
    }
}