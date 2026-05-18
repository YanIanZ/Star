package dev.yanianz.star.commands;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Command {
    String name() default "";
    String[] aliases() default {};
    String permission() default "";
    String usage() default "";
    String description() default "";
    boolean defaultCommand() default false;
}
