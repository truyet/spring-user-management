package com.tericcabrel.authorization.constraints.validators;

import com.tericcabrel.authorization.constraints.IsUnique;
import com.tericcabrel.authorization.utils.Helpers;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

@Component
public class IsUniqueValidator implements ConstraintValidator <IsUnique, Object> {

    private String repositoryName;
    private String propertyName;

    private final ApplicationContext applicationContext;

    public IsUniqueValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(final IsUnique constraintAnnotation) {
        repositoryName = constraintAnnotation.repository();
        propertyName = constraintAnnotation.property();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        Object result;
        String finalRepositoryName = "com.tericcabrel.authorization.repositories.mongo." + repositoryName;

        try {
            Class<?> type = Class.forName(finalRepositoryName);
            Object instance = this.applicationContext.getBean(finalRepositoryName);

            final Object propertyObj = BeanUtils.getProperty(value, propertyName);

            String finalPropertyName = Helpers.capitalize(propertyName);

            result = type.getMethod("findBy"+finalPropertyName, String.class).invoke(instance, propertyObj.toString());
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();

            return false;
        }

        return result == null;
    }
}