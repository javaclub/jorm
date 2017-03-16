/*
 * @(#)SeqenceDAO.java	2017年3月16日
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.seqence;

/**
 * SeqenceDAO
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SeqenceDAO.java 2017年3月16日 下午10:00:36 Exp $
 */
public interface SequenceDAO {

	public SequenceRange nextRange(String paramString) throws SequenceException;
}
