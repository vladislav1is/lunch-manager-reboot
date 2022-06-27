package com.redfox.restaurantvoting.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.redfox.restaurantvoting.util.DateTimeUtil.*;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@OpenAPIDefinition(
        info = @Info(
                title = "REST API documentation",
                version = "1.0",
                description = """
                        Graduation project <a href='https://github.com/vladislav1is/restaurant-voting'>Lunch Manager</a>
                        <p><b>Test credentials:</b><br>
                        - user@yandex.ru / password<br>
                        - admin@gmail.com / admin</p>
                        """,
                contact = @Contact(url = "https://github.com/vladislav1is", name = "Vladislav Lisin", email = "vladislav1isin@mail.ru")
        ),
        security = @SecurityRequirement(name = "basicAuth")
)
public class OpenApiConfig {
    // https://ru.stackoverflow.com/a/1276885/209226
    static {
        var dateSchema = new Schema<LocalDate>();
        dateSchema.example(LocalDate.now().format(DATE_FORMATTER));
        var timeSchema = new Schema<LocalTime>();
        timeSchema.example(LocalTime.now().format(TIME_FORMATTER));
        SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class, timeSchema);
        var dateTimeSchema = new Schema<LocalDateTime>();
        dateTimeSchema.example(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        SpringDocUtils.getConfig().replaceWithSchema(LocalDateTime.class, dateTimeSchema);
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("REST API")
                .pathsToMatch("/api/**")
                .build();
    }
}
