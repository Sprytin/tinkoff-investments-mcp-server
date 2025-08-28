package tech.sprytin.rich.trade

import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.tinkoff.piapi.contract.v1.*
import ru.ttech.piapi.core.InvestApi
import tech.sprytin.rich.api.McpToolService

@Service
@Profile(value = ["trade-mcp"])
class StopOrdersTools(private val investApi: InvestApi) : McpToolService {

    @Tool(description = "Place a stop order (StopOrdersService.PostStopOrder) with direction, type and expiration. Provide both trigger stopPrice and execution price as Quotation (units,nano). Returns stopOrderId.")
    fun postStopOrder(
        accountId: String,
        instrumentId: String,
        direction: StopOrderDirection,
        stopOrderType: StopOrderType,
        expirationType: StopOrderExpirationType,
        priceUnits: Long,
        priceNano: Int,
        stopPriceUnits: Long,
        stopPriceNano: Int,
        quantity: Long
    ): PostStopOrderResponse =
        investApi.stopOrdersServiceSync.postStopOrder(
            PostStopOrderRequest.newBuilder()
                .setAccountId(accountId)
                .setInstrumentId(instrumentId)
                .setDirection(direction)
                .setStopOrderType(stopOrderType)
                .setExpirationType(expirationType)
                .setPrice(Quotation.newBuilder().setUnits(priceUnits).setNano(priceNano).build())
                .setStopPrice(Quotation.newBuilder().setUnits(stopPriceUnits).setNano(stopPriceNano).build())
                .setQuantity(quantity)
                .build()
        )

    @Tool(description = "List all active stop orders for the account (StopOrdersService.GetStopOrders).")
    fun getStopOrders(accountId: String): GetStopOrdersResponse =
        investApi.stopOrdersServiceSync.getStopOrders(
            GetStopOrdersRequest.newBuilder().setAccountId(accountId).build()
        )

    @Tool(description = "Cancel a stop order by accountId and stopOrderId (StopOrdersService.CancelStopOrder).")
    fun cancelStopOrder(accountId: String, stopOrderId: String): CancelStopOrderResponse =
        investApi.stopOrdersServiceSync.cancelStopOrder(
            CancelStopOrderRequest.newBuilder()
                .setAccountId(accountId)
                .setStopOrderId(stopOrderId)
                .build()
        )
}


