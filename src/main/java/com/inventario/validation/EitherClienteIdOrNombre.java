package com.inventario.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotación personalizada para validar que se proporcione
 * al menos uno de los campos: clienteId o nombreCliente
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EitherClienteIdOrNombreValidator.class)
@Documented
public @interface EitherClienteIdOrNombre {

    String message() default "Debe proporcionar al menos uno de los siguientes: clienteId o nombreCliente";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
