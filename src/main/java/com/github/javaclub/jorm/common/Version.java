/*
 * @(#)Version.java  2009-2-19
 *
 * Copyright (c) 2009 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.common;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * A utility class for Version.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Version.java 525 2011-10-08 10:30:18Z gerald.chen.hz $
 */
@SuppressWarnings("unchecked")
public class Version implements Comparable {
	private final int			major;
	private final int			minor;
	private final int			micro;
	private final String		qualifier;
	private static final String	SEPARATOR		= ".";

	/**
	 * The empty version "0.0.0". Equivalent to calling
	 * <code>new Version(0,0,0)</code>.
	 */
	public static final Version	emptyVersion	= new Version(0, 0, 0);

	public Version(int major, int minor, int micro) {
		this(major, minor, micro, null);
	}

	public Version(int major, int minor, int micro, String qualifier) {
		if (qualifier == null) {
			qualifier = "";
		}

		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
		validate();
	}

	public Version(String version) {
		int major = 0;
		int minor = 0;
		int micro = 0;
		String qualifier = "";

		try {
			StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
			major = Integer.parseInt(st.nextToken());

			if (st.hasMoreTokens()) {
				st.nextToken(); // consume delimiter
				minor = Integer.parseInt(st.nextToken());

				if (st.hasMoreTokens()) {
					st.nextToken(); // consume delimiter
					micro = Integer.parseInt(st.nextToken());

					if (st.hasMoreTokens()) {
						st.nextToken(); // consume delimiter
						qualifier = st.nextToken();

						if (st.hasMoreTokens()) {
							throw new IllegalArgumentException("invalid format"); //$NON-NLS-1$
						}
					}
				}
			}
		}
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("invalid format");
		}

		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
		validate();
	}

	private void validate() {
		if (major < 0) {
			throw new IllegalArgumentException("negative major");
		}
		if (minor < 0) {
			throw new IllegalArgumentException("negative minor");
		}
		if (micro < 0) {
			throw new IllegalArgumentException("negative micro");
		}
		int length = qualifier.length();
		for (int i = 0; i < length; i++) {
			if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".indexOf(qualifier.charAt(i)) == -1) { //$NON-NLS-1$
				throw new IllegalArgumentException("invalid qualifier");
			}
		}
	}

	public static Version parseVersion(String version) {
		if (version == null) {
			return emptyVersion;
		}

		version = version.trim();
		if (version.length() == 0) {
			return emptyVersion;
		}

		return new Version(version);
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getMicro() {
		return micro;
	}

	public String getQualifier() {
		return qualifier;
	}

	public String toString() {
		String base = major + SEPARATOR + minor + SEPARATOR + micro;
		if (qualifier.length() == 0) { //$NON-NLS-1$
			return base;
		} else {
			return base + SEPARATOR + qualifier;
		}
	}

	public int hashCode() {
		return (major << 24) + (minor << 16) + (micro << 8)
				+ qualifier.hashCode();
	}

	public boolean equals(Object object) {
		if (object == this) { // quicktest
			return true;
		}

		if (!(object instanceof Version)) {
			return false;
		}

		Version other = (Version) object;
		return (major == other.major) && (minor == other.minor)
				&& (micro == other.micro) && qualifier.equals(other.qualifier);
	}

	public int compareTo(Object object) {
		if (object == this) { // quicktest
			return 0;
		}

		Version other = (Version) object;

		int result = major - other.major;
		if (result != 0) {
			return result;
		}

		result = minor - other.minor;
		if (result != 0) {
			return result;
		}

		result = micro - other.micro;
		if (result != 0) {
			return result;
		}

		return qualifier.compareTo(other.qualifier);
	}
	
	public static void main(String[] args) {
		Version ver1 = new Version("08.00");
		Version ver2 = new Version("08.01");
		System.out.println(ver1.compareTo(ver2));
	}
}