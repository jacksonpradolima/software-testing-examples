/* Description: This class loads the images and caches them in a list.
 * 
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Logic;

import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;
import java.net.URL;
import java.net.URLClassLoader;


public class TetrisImages extends Component {

	private String[] names = {	"background.gif", "red.gif", "darkblue.gif", "cyan.gif",
										"green.gif", "gray.gif", "yellow.gif", "magenta.gif"};
	private Hashtable cache;

	private static TetrisImages instance;

	public static void preLoad() {
		setInstance();	
	}

	private TetrisImages() {
		cache = new Hashtable(names.length);
		for (int i = 0; i < names.length; i++) {
			cache.put(names[i], loadImage(names[i]));
		}
	}

	private static void setInstance() {
		if(instance == null)
			instance = new TetrisImages();
	}

	public static Image getImage(String name) {
        setInstance();
        
        if (instance.cache != null) {
				if(instance.cache.containsKey(name))
            	return (Image)instance.cache.get(name);
            else {
            	Image img = instance.loadImage(name);
            	instance.cache.put(name, img);
            	return img;  	
            }
        }
        return null;
	}
    
	protected Image loadImage(String name) {
		URLClassLoader urlLoader = (URLClassLoader)this.getClass().getClassLoader();
		URL fileLoc = urlLoader.findResource("images/" + name);
		Image img = this.getToolkit().createImage(fileLoc);
		
		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(img, 0);
		try {
			tracker.waitForID(0);
			if (tracker.isErrorAny()) {
			System.out.println("Error loading image " + name);
			}
		} catch (Exception ex) { ex.printStackTrace(); }
		return img;
	}
}
