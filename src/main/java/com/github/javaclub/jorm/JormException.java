package com.github.javaclub.jorm;

import java.io.Serializable;

/**
 * Root exception for all runtime exceptions.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: JormException.java 271 2011-08-30 10:50:28Z gerald.chen.hz $
 */
public class JormException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 8093585396636106475L;

	/**
     * Creates a new JRuntimeException.
     */
    public JormException() {
        super();
    }

    /**
     * Constructs a new JRuntimeException.
     *
     * @param message the reason for the exception
     */
    public JormException(String message) {
        super(message);
    }

    /**
     * Constructs a new JRuntimeException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public JormException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JRuntimeException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public JormException(String message, Throwable cause) {
        super(message, cause);
    }

}
