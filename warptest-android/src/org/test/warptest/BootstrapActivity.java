package org.test.warptest;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ericsson.research.warp.api.Logger;
import com.ericsson.research.warp.api.WarpApplication;
import com.ericsson.research.warp.api.WarpClient;
import com.ericsson.research.warp.api.WarpConfiguration;
import com.ericsson.research.warp.api.WarpException;
import com.ericsson.research.warp.api.WarpServiceLifecycleListener;
import com.ericsson.research.warp.api.resources.InorderResource;
import com.ericsson.research.warp.api.resources.Resource;
import com.ericsson.research.warp.api.resources.StreamMessageResource;
import com.ericsson.research.warp.util.WarpLogger;

public class BootstrapActivity extends Activity {
	private TextView mText;
	private ScrollView mScroll;
	private StringBuilder mScrollContent = new StringBuilder();
	private WarpClient	myapp;
	private Object	id;
	
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
		
		WarpLogger.setDefaultLogLevel(Logger.Level.INFO);
		
		WarpConfiguration c = WarpConfiguration.getDefault();
		c.setGatewayConfigURI("http://85.224.231.3:8080/Core-Gateway-View/warp/config.properties");
		c.setGUID("warp://erlabs:auth/vlad");
		c.setPassword("secret");
		
		c.disableTransport("http");
		final BootstrapActivity parent = this;
		id = null;
		
		if (myapp == null)
		{
			myapp = new WarpClient("st");
			
			myapp.addLifecycleListener(new WarpServiceLifecycleListener() {
				
				public void serviceUnregistered(WarpApplication warpApplication)
				{
					// Disconnected state. Should generally not happen.
					// If it does, reconnect should happen.
					// If it doesn't... oops?
					log("Disconnected. Oops!");
				}
				
				public void serviceRegistered(WarpApplication warpApplication)
				{
					synchronized(myapp)
					{
						id = "hello";
						myapp.notifyAll();
					}
					Resource resource = myapp.resources.addResource("/**");
					try
					{
						// Apply inorder delivery. This prevents the sender from re-requesting
						resource.getAsResource(InorderResource.class);					
						resource.addIncomingMessageListener(new ImageServResource(parent, (StreamMessageResource)myapp.resources.getResource("/_send", StreamMessageResource.class)));
					}
					catch (WarpException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
					
				}
			});
			try
			{
				myapp.register();
			}
			catch (WarpException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		synchronized(myapp)
		{
			while(id == null)
				try
				{
					myapp.wait();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				log("I have an ID now! See email on how to use it!");
				log(myapp.descriptor.getBaseURI().toString());
		}
//		
//		InputStream is = null;
//		String assetName = "moorwen_textured.jpg";
//		try {
//			is = getAssetInputStream(assetName);
//			log("got input stream for asset: " + assetName);
//		} catch (IOException e) {
//			log("failed to get input stream for asset: " + assetName);
//		} finally {
//			if (is != null) {
//				try {
//					is.close();
//				} catch (IOException e) {
//				}
//			}
//		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// put cleanup / shutdown code here
		
		// The following two lines tear down the tunnel. Bad news is this happens on screen idle
		// which happens after 10 seconds
		// which is FREAKING ANNOYING
		// (Hello little idle screen... I WILL DESTROY YOU)
		//myapp.unregister();
		//myapp = null;
	}
	
//	private InputStream getAssetInputStream(String name) throws IOException {
//		InputStream is = this.getAssets().open(name);
//		return is;
//	}
	
	private void log(String msg) {
		mScrollContent.append(msg);
		mScrollContent.append("\n");
		mText.setText(mScrollContent.toString());
	}
}