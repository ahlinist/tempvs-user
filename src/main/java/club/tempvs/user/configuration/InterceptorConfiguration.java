package club.tempvs.user.configuration;

import club.tempvs.user.interceptor.AuthInterceptor;
import club.tempvs.user.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@RequiredArgsConstructor
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
        registry.addInterceptor(tokenInterceptor);
    }
}
