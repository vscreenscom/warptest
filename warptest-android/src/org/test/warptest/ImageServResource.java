package org.test.warptest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import com.ericsson.research.warp.api.AbstractMessageListener;
import com.ericsson.research.warp.api.HeaderName;
import com.ericsson.research.warp.api.Logger;
import com.ericsson.research.warp.api.Message;
import com.ericsson.research.warp.api.OutputStreamMessage;
import com.ericsson.research.warp.api.WarpException;
import com.ericsson.research.warp.api.resources.Resource;
import com.ericsson.research.warp.api.resources.StreamMessageResource;
import com.ericsson.research.warp.util.WarpLogger;

public class ImageServResource extends AbstractMessageListener
{
	
	private BootstrapActivity	parent;
	private final StreamMessageResource	streamRes;

	public ImageServResource(BootstrapActivity parent, StreamMessageResource streamRes)
	{
		this.parent = parent;
		this.streamRes = streamRes;
	}
	
	@Override
	public boolean receiveMessage(final Message message, Resource resource)
	{
		
		// Locate an IS buffer
		// Resource name is /** so -2 to remove the catchall phrase.
		String imagePath = message.getTo().toString().substring(resource.getURI().toString().length()-2);
		InputStream is;
		try
		{
			is = parent.getAssets().open(imagePath);
		}
		catch (IOException e1)
		{
			throw new RuntimeException(new WarpException("No such image", 404));
		}
		
		try
		{
			Message m = new Message("POST", message.getFrom());
			
			// Probably some lookup available. This took seconds to make
			if (imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg"))
				m.setHeader(HeaderName.Content_Type, "image/jpeg");
			
			if (imagePath.endsWith(".png"))
				m.setHeader(HeaderName.Content_Type, "image/png");
			
			if (imagePath.endsWith(".gif"))
				m.setHeader(HeaderName.Content_Type, "image/gif");
			
			OutputStreamMessage reply = streamRes.createOutputStreamMessage(m);
			OutputStream os = reply.getOutputStream();
			byte[] buffer = new byte [64000];
			int length = 0;
			
			// Welp... I have a buffer, an InputStream and an OutputStream. BEAM ME UP SNOTTY
			
			while ((length = is.read(buffer)) > -1)
			{
				Thread.sleep(100);
				os.write(buffer, 0, length);
				WarpLogger.log(Logger.Level.ERROR, "Wrote ", length, " bytes on the stream!");
			}
			
			os.close();
			WarpLogger.log(Logger.Level.ERROR, "Closed the stream!");
			// TN: Just because the OutputStream has closed doesn't mean the bits have left the device
			// There is still some buffering beyond this point!
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ericsson.research.warp.api.AbstractMessageListener#getWeight()
	 */
	@Override
	public int getWeight()
	{
		// Throw on a weight to prevent this resource from intercepting some messages...
		return 1000;
	}
	
}
