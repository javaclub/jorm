// $Id: NoColumn.java 85 2011-07-09 10:49:54Z gerald.chen.hz@gmail.com $

package com.github.javaclub.jorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets the related field to not be mapped to a column. Must be added to a
 * getter or a setter associated with a field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoColumn {

}
