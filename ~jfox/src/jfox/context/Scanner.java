package jfox.context;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import jakarta.inject.Named;


public class Scanner {
	
	
	private static final String JAVA_CLASS_PATH = System.getProperty( "java.class.path" );
	private static final String PATH_SEPARATOR = System.getProperty( "path.separator" );
	private static final String CLASS_EXTENSION = ".class";
	
	
	//-------
	// Champs
	//-------
	
	private List<String>	resourceNames	= new ArrayList<>();
	private Set<File>		scannedUris		= new HashSet<>();
	

	public List<Class<?>> getClasses( String[] packageNames, String[] excludes ) {
		
		List<Class<?>> classes = new ArrayList<>();
		boolean flagSelected;;
		
		if ( excludes == null ) {
			excludes = new String[0];
		}
		
		for ( String nom : scan() ) {
			
			flagSelected = false;;
			String packageName = getPackageName( nom );
			for ( String item : packageNames ) {
				if ( packageName.startsWith( item ) && ( packageName.length() == item.length() || packageName.charAt( item.length() ) == '.' ) )  {
					flagSelected = true;
					break;
				}
			}
			for ( String exclude : excludes ) {
				if ( packageName.startsWith( exclude ) && ( packageName.length() == exclude.length() || packageName.charAt( exclude.length() ) == '.' ) )  {
					flagSelected = false;
					break;
				}
			}
			
			if ( flagSelected ) {
				try {
					Class<?> c = Class.forName( getClassName( nom ) );
					if ( c.isAnnotationPresent( Named.class)  ) {
						classes.add( c );
					}
				} catch ( NoClassDefFoundError | ClassNotFoundException   e ) {
				} catch ( Throwable e ) {
				}
			}
		}
		
		return classes;
	}
	
	
	public List<String> scan() {

		resourceNames.clear();
		scannedUris.clear();

		List<File> classpathElements = new ArrayList<>();
		for ( String entry : JAVA_CLASS_PATH.split( PATH_SEPARATOR  ) ) {
			classpathElements.add( new File(entry) );
		}
		
		for ( File file : classpathElements ) {
			if ( ! file.exists() ) {
				continue;
			}
			if ( file.isDirectory() ) {
				scanDirectory( file );
			} else {
				scanJar( file );
			}
		}
		return resourceNames;
	}

	
	private final void scan( File file ) {

		try {
			if ( scannedUris.add( file.getCanonicalFile() ) ) {
				
				if (!file.exists()) {
					return;
				}
				if ( file.isDirectory() ) {
					scanDirectory( file );
				} else {
					scanJar (file );
				}
			}
		} catch (IOException e) {
		}
		
	}

	
	private void scanJar( File file ) {

		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			for ( File path : getClassPathFromManifest( file, jarFile.getManifest() ) ) {
				scan( path );
			}
			scanJarFile( jarFile );
		} catch ( IOException e ) {
			return;
		} finally {
			try {
				if ( jarFile != null ) {
					jarFile.close();
				}
			} catch (IOException ignored) {
			}
		}
	}
    

	private void scanJarFile( JarFile file) {
		
		Enumeration<JarEntry> entries = file.entries();
		while ( entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.isDirectory() || entry.getName().equals(JarFile.MANIFEST_NAME)) {
				continue;
			}
			resourceNames.add(entry.getName());
		}
		
	}

	private void scanDirectory( File directory )  {
		Set<File> currentPath = new HashSet<>();
		try {
			currentPath.add( directory.getCanonicalFile() );
			scanDirectory( directory, "", currentPath );
		} catch (IOException e) {
		}
	}
	
	private void scanDirectory(
			File directory, String packagePrefix, Set<File> currentPath)
			{
		
		File[] files = directory.listFiles();
		for (File f : files) {
			String name = f.getName();
			if (f.isDirectory()) {
				try {
					File deref = f.getCanonicalFile();
					scanDirectory(deref, packagePrefix + name + "/", currentPath);
				} catch (IOException e) {
				}
			} else {
				String resourceName = packagePrefix + name;
				if ( ! resourceName.equals( JarFile.MANIFEST_NAME ) ) {
					resourceNames.add(resourceName);
				}
			}
		}
	}
    
    
	static List<File> getClassPathFromManifest (File jarFile, Manifest manifest ) {

		List<File> files = new ArrayList<>();
		if (manifest == null) {
			return files;
		}

		String classpathAttribute =
				manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
		if ( classpathAttribute != null ) {
			for (String path :  classpathAttribute.split( " ") ) {
				URL url;
				try {
					url = new URL( jarFile.toURI().toURL(), path );
				} catch ( MalformedURLException e ) {
					continue;
				}
				if ( url.getProtocol().equals("file") ) {
					try {
						files.add( new File( url.toURI() ) );
					} catch (URISyntaxException e) {
					}
				}
			}
       	}
       	return files;
	}
	 
	
	public static String getClassName( String filename ) throws ClassNotFoundException {
		if ( ! filename.endsWith( CLASS_EXTENSION) 
				|| filename.contains( "$" ) ) {
			throw new ClassNotFoundException();
		}
		int classNameEnd = filename.length() - CLASS_EXTENSION.length();
		return filename.substring(0, classNameEnd).replace('/', '.');
	}

	public static String getPackageName( String filename ) {
		int lastSeparator = filename.lastIndexOf('/');
		return (lastSeparator < 0) ? "" : filename.substring(0, lastSeparator).replace('/', '.');
	}

}
