package com.dmdev.junit.extension;

import com.dmdev.junit.dao.UserDAO;
import com.dmdev.junit.service.UserService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class UserTestParamResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == UserService.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        var store = extensionContext.getStore(ExtensionContext.Namespace.create(extensionContext.getTestMethod()));
        return store.getOrComputeIfAbsent(extensionContext.getTestMethod(), it -> new UserService(new UserDAO()));
    }
}
