package com.dmdev.junit.extension;

import com.dmdev.junit.service.UserService;
import lombok.Setter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        System.out.println("post processing extension");
        var declaredFields = testInstance.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields
        ) {
            if (declaredField.isAnnotationPresent(Setter.class))
                declaredField.set(testInstance, new UserService(null));
        }
    }
}
