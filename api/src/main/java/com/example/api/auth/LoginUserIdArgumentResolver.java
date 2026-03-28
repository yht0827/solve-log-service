package com.example.api.auth;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserIdArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String HEADER_NAME = "X-User-Id";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoginUserId.class)
			&& parameter.getParameterType().equals(Long.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws MissingRequestHeaderException {
		String value = webRequest.getHeader(HEADER_NAME);
		if (value == null || value.isBlank()) {
			throw new MissingRequestHeaderException(HEADER_NAME, parameter);
		}
		return Long.parseLong(value);
	}
}
