package tech.sprytin.rich.config

import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import io.grpc.ManagedChannel
import mu.KotlinLogging
import org.springframework.ai.tool.ToolCallback
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.execution.DefaultToolCallResultConverter
import org.springframework.ai.tool.execution.ToolCallResultConverter
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import ru.ttech.piapi.core.InvestApi
import tech.sprytin.rich.api.McpToolService
import tech.sprytin.rich.sandbox.SandboxTools

private val logger = KotlinLogging.logger {}

@ConfigurationProperties(prefix = "tinkoff")
data class TinkoffProperties(
    var token: String = "",
    var appName: String? = null,
    var target: String = "invest-public-api.tinkoff.ru:443"
)

@Configuration
@EnableConfigurationProperties(TinkoffProperties::class)
class TinkoffMcpConfig {

    @Bean
    fun investApiChannel(props: TinkoffProperties): ManagedChannel =
        InvestApi.defaultChannel(
            token = props.token,
            appName = props.appName ?: "rich-mcp-server",
            target = props.target
        )

    @Bean
    fun investApi(channel: ManagedChannel): InvestApi = InvestApi.createApi(channel)

    @Bean
    fun toolsProvider(sandboxTools: SandboxTools): ToolCallbackProvider =
        MethodToolCallbackProvider.builder().toolObjects(sandboxTools).build();
}

@Configuration
class ProtobufAwareToolCallResultConverter : ToolCallResultConverter {
    private val defaultConverter = DefaultToolCallResultConverter()
    private val printer: JsonFormat.Printer =
        JsonFormat.printer().includingDefaultValueFields().preservingProtoFieldNames()

    override fun convert(result: Any?, resultType: java.lang.reflect.Type?): String {
        if (result == null) return "null"
        logger.info { "converter resultType: $resultType result: $result" }
        return if (result is Message) {
            printer.print(result)
        } else {
            defaultConverter.convert(result, resultType)
        }
    }
}

