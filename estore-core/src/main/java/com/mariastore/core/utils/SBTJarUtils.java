package com.mariastore.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SBTJarUtils {
	private static final Pattern JAR_NAME_PATTERN = Pattern.compile("^([a-zA-Z\\.\\-_]+)(\\d)*[\\.\\-_]*(\\d)*[\\.\\-_]*(\\d)*[\\.\\-_]*([a-zA-Z0-9]*)\\.jar");
	
	public interface JarInfo {
		public String getName();
		public int getMajor();
		public int getMinor();
		public int getRevision();
		public String getBuild();
	}
	
	
	public static JarInfo parseJarVersion(final String jarPath) {
		
		String name = jarPath;
		if (name.contains("\\") || name.contains("/")) {
			int pos = Math.max(name.lastIndexOf("\\"), name.lastIndexOf("/"));
			name =  name.substring(pos + 1);
		}
		final String jarName = name.substring(0, name.length() - ".jar".length());
		final Matcher m = JAR_NAME_PATTERN.matcher(name);
		return new JarInfo() {

			@Override
			public String getName() {
				String name = m.matches() && m.groupCount() > 0 ? m.group(1) : jarName;
				if (name.endsWith(".") || name.endsWith("_") || name.endsWith("-")) {
					name = name.substring(0, name.length() - 1);
				}
				return name;
			}

			@Override
			public int getMajor() {
				return m.matches() && m.groupCount() > 1 ? Integer.parseInt(m.group(2)) : 0;
			}

			@Override
			public int getMinor() {
				return m.matches() && m.groupCount() > 2 ? Integer.parseInt(m.group(3)) : 0;
			}

			@Override
			public int getRevision() {
				return m.matches() && m.groupCount() > 3 ? Integer.parseInt(m.group(4)) : 0;
			}

			@Override
			public String getBuild() {
				return m.matches() && m.groupCount() > 4 ? m.group(5) : "";
			}
		};
	}
}
