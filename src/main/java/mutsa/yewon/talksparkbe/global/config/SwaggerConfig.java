package mutsa.yewon.talksparkbe.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.swagger.ApiErrorCode;
import mutsa.yewon.talksparkbe.global.swagger.ApiErrorCodes;
import mutsa.yewon.talksparkbe.global.swagger.ErrorResponseDTO;
import mutsa.yewon.talksparkbe.global.swagger.ExampleHolder;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OpenAPIDefinition(
    servers = {
            @Server(url = "http://localhost:8080", description = "로컬 테스트 서버"),
            @Server(url = "https://talkspark-dev-api.p-e.kr", description = "배포된 개발 서버"),
            @Server(url = "https://talkspark.site", description = "배포된 메인 서버"),
    }
)
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("TalkSpark Backend API") // API의 제목
                .description("Talk Spark API SERVICE") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiErrorCodes apiErrorCodeExamples = handlerMethod.getMethodAnnotation(
                    ApiErrorCodes.class);

            // @ApiErrorCodeExamples 어노테이션이 붙어있다면
            if (apiErrorCodeExamples != null) {
                generateErrorCodeResponseExample(operation, apiErrorCodeExamples.value());
            } else {
                ApiErrorCode apiErrorCodeExample = handlerMethod.getMethodAnnotation(
                        ApiErrorCode.class);

                // @ApiErrorCodeExamples 어노테이션이 붙어있지 않고
                // @ApiErrorCodeExample 어노테이션이 붙어있다면
                if (apiErrorCodeExample != null) {
                    generateErrorCodeResponseExample(operation, apiErrorCodeExample.value());
                }
            }

            return operation;
        };
    }

    // 여러 개의 에러 응답값 추가
    private void generateErrorCodeResponseExample(Operation operation, ErrorCode[] errorCodes) {
        ApiResponses responses = operation.getResponses();

        // ExampleHolder(에러 응답값) 객체를 만들고 에러 코드별로 그룹화
        Map<Integer, List<ExampleHolder>> statusWithExampleHolders = Arrays.stream(errorCodes)
                .map(
                        errorCode -> ExampleHolder.builder()
                                .holder(getSwaggerExample(errorCode))
                                .code(errorCode.getHttpStatus().value())
                                .name(errorCode.name())
                                .build()
                )
                .collect(Collectors.groupingBy(ExampleHolder::getCode));

        // ExampleHolders를 ApiResponses에 추가
        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    // 단일 에러 응답값 예시 추가
    private void generateErrorCodeResponseExample(Operation operation, ErrorCode errorCode) {
        ApiResponses responses = operation.getResponses();

        // ExampleHolder 객체 생성 및 ApiResponses에 추가
        ExampleHolder exampleHolder = ExampleHolder.builder()
                .holder(getSwaggerExample(errorCode))
                .name(errorCode.name())
                .code(errorCode.getHttpStatus().value())
                .build();
        addExamplesToResponses(responses, exampleHolder);
    }

    // ErrorResponseDto 형태의 예시 객체 생성
    private Example getSwaggerExample(ErrorCode errorCode) {
        ErrorResponseDTO errorResponseDto = ErrorResponseDTO.from(errorCode);
        Example example = new Example();
        example.setValue(errorResponseDto);

        return example;
    }

    // exampleHolder를 ApiResponses에 추가
    private void addExamplesToResponses(ApiResponses responses,
                                        Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();

                    v.forEach(
                            exampleHolder -> mediaType.addExamples(
                                    exampleHolder.getName(),
                                    exampleHolder.getHolder()
                            )
                    );
                    content.addMediaType("application/json", mediaType);
                    apiResponse.setContent(content);
                    responses.addApiResponse(String.valueOf(status), apiResponse);
                }
        );
    }

    private void addExamplesToResponses(ApiResponses responses, ExampleHolder exampleHolder) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        ApiResponse apiResponse = new ApiResponse();

        mediaType.addExamples(exampleHolder.getName(), exampleHolder.getHolder());
        content.addMediaType("application/json", mediaType);
        apiResponse.content(content);
        responses.addApiResponse(String.valueOf(exampleHolder.getCode()), apiResponse);
    }
}
