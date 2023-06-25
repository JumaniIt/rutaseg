package com.jumani.rutaseg.handler;

import com.jumani.rutaseg.dto.response.UserSessionInfo;
import com.jumani.rutaseg.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.jumani.rutaseg.filter.SessionFilter.AUTHORIZATION_HEADER;
import static com.jumani.rutaseg.filter.SessionFilter.BEARER_SUFFIX;

@Component
@AllArgsConstructor
public class UserSessionInfoHandler implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(USI.class) != null;
    }

    @Override
    public UserSessionInfo resolveArgument(@NonNull MethodParameter methodParameter,
                                           ModelAndViewContainer modelAndViewContainer,
                                           NativeWebRequest nativeWebRequest,
                                           WebDataBinderFactory webDataBinderFactory) {

        final HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        final String token = authorizationHeader.substring(BEARER_SUFFIX.length());

        final long userId = Long.parseLong(jwtService.extractSubject(token));
        final boolean admin = jwtService.isAdminToken(token);

        return new UserSessionInfo(userId, admin);
    }
}
