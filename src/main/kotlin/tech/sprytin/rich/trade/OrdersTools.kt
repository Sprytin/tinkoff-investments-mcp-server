package tech.sprytin.rich.trade

import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.tinkoff.piapi.contract.v1.*
import ru.ttech.piapi.core.InvestApi
import tech.sprytin.rich.api.McpToolService

@Service
@Profile(value = ["trade-mcp"])
class OrdersTools(private val investApi: InvestApi) : McpToolService {

    @Tool(description = "Create a trading order with parameters (OrdersService.PostOrder). Supports BUY/SELL and various order types. For LIMIT orders, provide price (units,nano). Returns orderId and status.")
    fun postOrder(
        accountId: String,
        instrumentId: String,
        quantity: Long,
        direction: OrderDirection,
        orderType: OrderType,
        limitPriceUnits: Long?,
        limitPriceNano: Int?
    ): PostOrderResponse {
        val requestBuilder = PostOrderRequest.newBuilder()
            .setAccountId(accountId)
            .setInstrumentId(instrumentId)
            .setQuantity(quantity)
            .setDirection(direction)
            .setOrderType(orderType)
        if (limitPriceUnits != null && limitPriceNano != null) {
            requestBuilder.price = Quotation.newBuilder().setUnits(limitPriceUnits).setNano(limitPriceNano).build()
        }
        return investApi.ordersServiceSync.postOrder(requestBuilder.build())
    }

    @Tool(description = "Create a trading order asynchronously (OrdersService.PostOrderAsync). Returns immediate acceptance status; check order state later for execution details.")
    fun postOrderAsync(
        accountId: String,
        instrumentId: String,
        quantity: Long,
        direction: OrderDirection,
        orderType: OrderType,
        limitPriceUnits: Long?,
        limitPriceNano: Int?
    ): PostOrderAsyncResponse {
        val requestBuilder = PostOrderAsyncRequest.newBuilder()
            .setAccountId(accountId)
            .setInstrumentId(instrumentId)
            .setQuantity(quantity)
            .setDirection(direction)
            .setOrderType(orderType)
        if (limitPriceUnits != null && limitPriceNano != null) {
            requestBuilder.price = Quotation.newBuilder().setUnits(limitPriceUnits).setNano(limitPriceNano).build()
        }
        return investApi.ordersServiceSync.postOrderAsync(requestBuilder.build())
    }
}


