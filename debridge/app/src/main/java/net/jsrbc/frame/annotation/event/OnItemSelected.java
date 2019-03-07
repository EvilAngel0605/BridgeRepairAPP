package net.jsrbc.frame.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZZZ on 2017-12-14.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@AndroidEvent
public @interface OnItemSelected {}
