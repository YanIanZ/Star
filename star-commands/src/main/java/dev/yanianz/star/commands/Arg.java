package dev.yanianz.star.commands;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {
    String value();
    boolean optional() default false;
}
