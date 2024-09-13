/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author munkhochir
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertiesHolder {

    String file();

    boolean autoLoad() default false;
}
