package net.jsrbc.frame.annotation.client;

import net.jsrbc.frame.enumeration.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZZZ on 2017-11-30.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    /** 路径 */
    String path();

    /** 方法 */
    RequestMethod method() default RequestMethod.GET;
}
