/*
 * @(#)Criterion.java	2011-7-22
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.criterion;

import java.io.Serializable;

/**
 * Criterion
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Criterion.java 2011-7-22 下午01:34:26 Exp $
 */
public interface Criterion extends Serializable {

	public String toSqlString();
}
