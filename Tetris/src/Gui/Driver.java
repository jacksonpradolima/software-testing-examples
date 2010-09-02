package Gui;

import java.io.*;
import java.awt.event.*;
import java.util.Random;

public class Driver implements Runnable {

	public static final int NOTAG = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;
	public static final int TIMER = 5;
	public static final int PAINT = 6;

	public static final String[] tags = {"None","Left","Right","Up","Down","Timer","Paint"} ;

	// set to indicate that replay should begin
	private volatile boolean init = false;
	// replay data
	private ByteIntVector biv = new ByteIntVector();

	// default values set by the AspectTetris main function
	// random object
	public static Random rand;
	// filename for replay
	public static String fileName;
	// slowdown factor
	public static int factor = 1;

	//	public static void main(String[] args) {
	//new Driver();
	//}

	public Driver() {
		if (fileName==null)
			return;
		setup();
		new Thread(this).start();
		//firstPaint();
	}

	// read in replay data
	public void setup() {
		File f = new File(fileName);
		try {
			long cur = 0;
			BufferedReader br = new BufferedReader(new FileReader(f));
			String s;
			s = br.readLine(); 
			while(s!=null) {
				long l = 0;
				byte tag = NOTAG;
				if (s.startsWith("lt:")) {
					tag = LEFT;
				} else if (s.startsWith("rt:")) {
					tag = RIGHT;
				} else if (s.startsWith("up:")) {
					tag = UP;
				} else if (s.startsWith("dn:")) {
					tag = DOWN;
				} else if (s.startsWith("tm:")) {
					tag = TIMER;
				} else if (s.startsWith("pt:") && cur==0) {
					tag = PAINT;
				} else if (s.startsWith("Seed=")) {
					try {
						rand = new Random(Integer.parseInt(s.substring(5)));
					} catch(NumberFormatException nfe) {
						nfe.printStackTrace();
					}
				} 
				if (tag != NOTAG) {
					try {
						l = Long.parseLong(s.substring(3));
					} catch(NumberFormatException nfe) {
						nfe.printStackTrace();
					}

					int diff = 0;
					if (cur!=0) {
						diff = (int)(l - cur);
					}
					cur = l;
					if (tag!=PAINT)
						biv.add(tag,diff);

					//System.out.println(tags[tag]+": "+diff);
				}
				s = br.readLine(); 
			}
			br.close();
		} catch(IOException ie) {
			ie.printStackTrace();
			System.exit(1);
		}
	}

	// called by another thread when first paint happens
	public synchronized void firstPaint() {
		init = true;
		notify();
	}

	public void run() {
		// wait for first paint to happen
		synchronized(this) {
			while (!init) {
				try {
					wait();
				} catch(InterruptedException ie) {
				}
			}
		}
		// then start feeding the events...
		int i;
		long cur = System.currentTimeMillis();
		for(i=0;i<biv.length();i++) {
			byte tag = biv.bAt(i);
			int diff = biv.iAt(i);
			if (diff*factor>5) {
				try {
					Thread.sleep(diff*factor);
				} catch(InterruptedException ie) {
				}
			}

			TetrisGUI.aa[tag].actionPerformed(new ActionEvent(TetrisGUI.theFrame,tag,""));

			//System.out.println(tags[tag]+": "+diff);
		}
		// wait just a bit to let other threads go
		try {
			Thread.sleep(10);
		} catch(InterruptedException ie) {
		}
		// then exit
		System.exit(0);
	}
}


// for storing replay data
// (tag,time-delay) pairs
final class ByteIntVector {
	private byte[] b;
	private int[] v;
	private int free;
	ByteIntVector() {
		b = new byte[100];
		v = new int[100];
		free = 0;
	}
	void add(byte bx,int x) {
		if (free>=v.length) {
			int[] newv = new int[v.length*2];
			System.arraycopy(v,0,newv,0,v.length);
			v = newv;
			byte[] newb = new byte[b.length*2];
			System.arraycopy(b,0,newb,0,b.length);
			b = newb;
		}
		b[free] = bx;
		v[free++] = x;
	}
	int length() {
		return free;
	}

	byte bAt(int i) { 
		return b[i];
	}
	int iAt(int i) { 
		return v[i];
	}
}
