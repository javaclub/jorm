/*
 * @(#)PK.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明了一个 POJO 的主键。
 * <p>
 * 本注解声明在某一个 POJO 类上，例如：
 * 
 * <pre>
 * &#064;Table("t_abc")
 * &#064;PK({"id", "type"})
 * public class Abc{
 * ...
 * </pre>
 * 
 * 其中， "id" 和 "type" 必须是这个 POJO 的 Java 字段名
 * 
 * <p>
 * 这个注解主要应用在复合主键的情况，如果一个 POJO 是复合主键的话，你需要通过
 * <ul>
 * <li>list(Class<?>,Object ...) 来获取一个对象
 * <li>delete(Class<?>,Object ...) 来删除一个对象
 * </ul>
 * 变参给入的顺序，需要按照本注解声明的顺序，否则会发生不可预知的错误。
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: PK.java 85 2011-07-09 10:49:54Z gerald.chen.hz@gmail.com $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PK {
	String[] value();
}
