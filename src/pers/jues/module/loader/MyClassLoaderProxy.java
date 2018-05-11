package pers.jues.module.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MyClassLoaderProxy extends Thread{
	protected String m_in_path;
	protected String m_out_path;
	protected Map<String,MyClassLoader> m_loaders = new HashMap<String,MyClassLoader>();

	public MyClassLoaderProxy( String fifo_in, String fifo_out ) throws FileNotFoundException{
		this.m_in_path = fifo_in;
		this.m_out_path = fifo_out;
	}




	//add
	public void add( String name,MyClassLoader loader ) {
		this.m_loaders.put(name, loader);
	}

	//remove
	public void remove( String name ) {
		this.m_loaders.remove(name);
	}

	//get
	public MyClassLoader get( String name ) {
		return this.m_loaders.get(name);
	}



	@Override
	public void run() {
		InputStream in;

		try {
			while ( false == this.isInterrupted() ) {
				in = new FileInputStream(new File(this.m_in_path));

				//check 
				byte[] b = new byte[1];
				in.read(b);

				//read
				int size = in.available();
				byte[] bs = new byte[size];
				in.read(bs);

				//string
				String cmd = new String(b) + new String(bs);

				//parse
				String[] param = this.parse(cmd.trim());
				if ( 2 != param.length ) {
					continue;
				}

				int i = 0;
				String name = param[i]; i++;
				String action = param[i]; i++;

				//action
				boolean res = this.action(name, action);

				//reply
				String value = ( true == res ) ? "OK" : "NO";
				this.replyAction(name, action, value+"\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//parse
	String[] parse( String cmd ) {
		// name,cmd
		String[] param = cmd.split(",");
		//
		return param;
	}

	//action
	boolean action( String name,String action ) {
		boolean res;
		//check name
		if ( false == this.m_loaders.containsKey(name) ) {
			return false;
		}
		//
		MyClassLoader loader = this.m_loaders.get(name);

		//execute
		if ( true == action.equals("reload") ) {
			res = this.actionReload(loader);
		}
		else {
			res = false;
		}

		//
		return res;
	}

	//actionReload
	boolean actionReload( MyClassLoader loader ) {
		//reload configure
		try {
			loader.reloadConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		//
		return true;
	}

	//replyAction
	void replyAction( String name,String action,String value ) {
		String data = String.format("%s,%s,%s", name,action,value);
		OutputStream out;
		//
		try {
			out = new FileOutputStream(new File(this.m_out_path)); 
			//
			out.write(data.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
