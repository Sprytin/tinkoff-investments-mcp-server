package tech.sprytin.rich.read

import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.GetAccountsRequest
import ru.tinkoff.piapi.contract.v1.GetUserTariffRequest
import ru.tinkoff.piapi.contract.v1.GetUserTariffResponse
import ru.ttech.piapi.core.InvestApi
import tech.sprytin.rich.api.McpToolService

@Service
@Profile(value = ["read-mcp"])
class UsersTools(private val investApi: InvestApi) : McpToolService {

    @Tool(description = "Retrieve the current user tariff and rate limits for methods/streams (UsersService.GetUserTariff). Useful to understand available quotas and features.")
    fun getUserTariff(): GetUserTariffResponse =
        investApi.usersServiceSync.getUserTariff(GetUserTariffRequest.getDefaultInstance())

    @Tool(description = "List all user accounts accessible by the current token (UsersService.GetAccounts). Use the result to obtain accountId values.")
    fun getAccounts(): List<Account> =
        investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance()).accountsList
}


