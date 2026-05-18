package dev.yanianz.star.commands;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TabComplete {
    String[] suggestions() default {};
    String completer() default "";
}
