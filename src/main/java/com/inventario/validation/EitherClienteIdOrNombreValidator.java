package com.inventario.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.inventario.dto.VentaDto;

/**
 * Validador para la anotación @EitherClienteIdOrNombre
 * Verifica que al menos uno de los campos clienteId o nombreCliente sea proporcionado
 */
public class EitherClienteIdOrNombreValidator implements ConstraintValidator<EitherClienteIdOrNombre, Object> {

    @Override
    public void initialize(EitherClienteIdOrNombre annotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // Si el objeto es null, no validar (otros validadores como @NotNull se encargarán)
        if (value == null) {
            return true;
        }

        // El objeto debe ser una instancia de VentaDto.Post
        if (!(value instanceof VentaDto.Post)) {
            return true;
        }

        VentaDto.Post ventaPost = (VentaDto.Post) value;
        Integer clienteId = ventaPost.getClienteId();
        String nombreCliente = ventaPost.getNombreCliente();

        // Validar que al menos uno de los dos campos sea proporcionado
        boolean isClienteIdValid = clienteId != null && clienteId > 0;
        boolean isNombreClienteValid = nombreCliente != null && !nombreCliente.trim().isEmpty();

        if (!isClienteIdValid && !isNombreClienteValid) {
            // Personalizar el mensaje de error
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Debe proporcionar al menos uno de los siguientes: clienteId (válido) o nombreCliente (no vacío)")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
