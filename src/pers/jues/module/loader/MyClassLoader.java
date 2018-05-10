package pers.jues.module.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

public class MyClassLoader {
	protected Properties m_config;
	protected String m_name;
	protected URLClassLoader m_loader = null;
	
	
	public MyClassLoader( String name, String filename ) throws IOException {
		//load configure file
		InputStream in = new FileInputStream(filename);
		this.m_config = new Properties();
		this.m_config.load(in);
		
		File dir = new File(filename);
		String path = dir.getCanonicalPath();
		String root_path = path.substring(0,path.length()-dir.getName().length());
		
		//load jar file
		String jar_path = this.m_config.getProperty(name,name);
		
		//check has root
		String root_flag = jar_path.substring(0, 1);
		if ( false == root_flag.equals("/") && false == root_flag.equals("\\") ) {
			jar_path = root_path + jar_path;
		}
		
		File file = new File(jar_path);
		URL url = file.toURI().toURL();
		this.m_loader = new URLClassLoader(new URL[] {url});
	}
	
	
	//loadClass
	public Class<?> loadClass( String name ) throws ClassNotFoundException {
		String class_name = this.m_config.getProperty(name,name);
		//
		return this.m_loader.loadClass(class_name);
	}
	
	
	//newInstance
	public Object newInstance( String name ) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> c = this.loadClass(name);
		//
		return c.newInstance();
	}

}
