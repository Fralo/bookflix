package dev.fralo.bookflix.easyj.annotations.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) //allow accesso to this annotation at runtime
@Target(ElementType.FIELD) //restrict usage on class fields only
public @interface Id {}