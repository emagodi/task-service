package za.co.datacentrix.taskservice.config;


import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import za.co.datacentrix.taskservice.handlers.ErrorResponse;


//@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Datacentrix Task Management System",
                        email = "emagodi@datacentrix.co.za"
                ),
                title = "Datacentrix Task Management System",
                description = "Endpoints for task creation, update, user preferences and real time notifications",
                version = "0.0.1-SNAPSHOT"
        ),
        servers = {
                @Server(
                        description = "Development",
                        url = "http://localhost:8080"

                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class OpenAPIConfiguration {
    //    @Bean
        public OpenApiCustomizer schemaCustomizer() {
                ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                        .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class));
                return openApi -> openApi
                        .schema(resolvedSchema.schema.getName(), resolvedSchema.schema);
        }
}
