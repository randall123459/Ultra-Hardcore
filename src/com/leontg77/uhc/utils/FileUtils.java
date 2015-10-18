package com.leontg77.uhc.utils;

import java.io.File;

/**
 * File utilities class.
 * <p>
 * Contains file related methods.
 * 
 * @author D4mnX
 */
public class FileUtils {

	/**
	 * Deletes the given file and it's subfiles.
	 * 
	 * @return True if sucess, false otherwise.
	 */
	public static boolean deleteWorld(File path) {
		if (path.exists()) {
			File files[] = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteWorld(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}