/*
 * Created on 03/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.model.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.sejda.model.validation.validator.HasTransitionsValidator;

/**
 * Constraint to validate a parameter making sure that some valid transition has been set (default or not).
 * 
 * @author Andrea Vacondio
 * 
 */
@Target({ FIELD, TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { HasTransitionsValidator.class })
@Documented
public @interface HasTransitions {

    String message() default "No transition has been set.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
