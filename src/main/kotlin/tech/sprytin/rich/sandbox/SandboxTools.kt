package tech.sprytin.rich.sandbox

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.tinkoff.piapi.contract.v1.*
import ru.ttech.piapi.core.InvestApi
import tech.sprytin.rich.api.McpToolService
import tech.sprytin.rich.config.ProtobufAwareToolCallResultConverter

@Service
@Profile(value = ["sandbox-mcp"])
class SandboxTools(private val investApi: InvestApi) : McpToolService {

    @Tool(description = "Open a new sandbox account (SandboxService.OpenSandboxAccount). Returns the created account identifier for simulated trading and testing.",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun openSandboxAccount(): OpenSandboxAccountResponse =
        investApi.sandboxServiceSync.openSandboxAccount(OpenSandboxAccountRequest.getDefaultInstance())

    @Tool(description = "Close an existing sandbox account by accountId (SandboxService.CloseSandboxAccount). All active orders will be canceled.",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun closeSandboxAccount(@ToolParam(description = "Account identifier (account_id).") accountId: String): CloseSandboxAccountResponse =
        investApi.sandboxServiceSync.closeSandboxAccount(
            CloseSandboxAccountRequest.newBuilder().setAccountId(accountId).build()
        )

    @Tool(description = "List all sandbox accounts for the current token (SandboxService.GetSandboxAccounts).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxAccounts(): GetAccountsResponse =
        investApi.sandboxServiceSync.getSandboxAccounts(GetAccountsRequest.getDefaultInstance())

    @Tool(description = "Deposit virtual funds to a sandbox account (SandboxService.SandboxPayIn). Specify amount as MoneyValue (currency, units, nano).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun sandboxPayIn(
        @ToolParam(description = "Account identifier (account_id).") accountId: String,
        @ToolParam(description = "Integer part of the amount (MoneyValue.units).") amountUnits: Long,
        @ToolParam(description = "Fractional part of the amount (MoneyValue.nano).") amountNano: Int,
        @ToolParam(description = "ISO currency code (MoneyValue.currency).") currency: String
    ): SandboxPayInResponse =
        investApi.sandboxServiceSync.sandboxPayIn(
            SandboxPayInRequest.newBuilder()
                .setAccountId(accountId)
                .setAmount(MoneyValue.newBuilder().setCurrency(currency).setUnits(amountUnits).setNano(amountNano).build())
                .build()
        )

    @Tool(description = "Place a sandbox order (SandboxService.PostSandboxOrder). Supports MARKET/BESTPRICE/LIMIT types; for LIMIT provide price units/nano. Returns PostOrderResponse with order id.",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun postSandboxOrder(
        @ToolParam(description = "Account identifier (account_id).") accountId: String,
        @ToolParam(description = "Instrument identifier (instrument_id): FIGI or instrument_uid.") instrumentId: String,
        @ToolParam(description = "Number of lots (quantity).") quantity: Long,
        @ToolParam(description = "Operation direction (direction).") direction: OrderDirection,
        @ToolParam(description = "Order type (order_type).") orderType: OrderType,
        @ToolParam(description = "Price per 1 instrument (Quotation.units) for a limit order; ignored for market orders (price).") limitPriceUnits: Long?,
        @ToolParam(description = "Fractional price part (Quotation.nano) for a limit order; ignored for market orders (price).") limitPriceNano: Int?
    ): PostOrderResponse {
        val builder = PostOrderRequest.newBuilder()
            .setAccountId(accountId)
            .setInstrumentId(instrumentId)
            .setQuantity(quantity)
            .setDirection(direction)
            .setOrderType(orderType)
        if (limitPriceUnits != null && limitPriceNano != null) {
            builder.price = Quotation.newBuilder().setUnits(limitPriceUnits).setNano(limitPriceNano).build()
        }
        val postSandboxOrder = investApi.sandboxServiceSync.postSandboxOrder(builder.build())

        return postSandboxOrder
    }

    @Tool(description = "Replace an existing sandbox order (SandboxService.ReplaceSandboxOrder). Update quantity and optionally price for LIMIT orders.",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun replaceSandboxOrder(
        @ToolParam(description = "Account identifier (account_id).") accountId: String,
        @ToolParam(description = "Exchange order ID (order_id).") orderId: String,
        @ToolParam(description = "Number of lots (quantity).") newQuantity: Long,
        @ToolParam(description = "New price per 1 instrument (Quotation.units) for a limit order (price).") priceUnits: Long?,
        @ToolParam(description = "Fractional price part (Quotation.nano) for a limit order (price).") priceNano: Int?
    ): PostOrderResponse {
        val builder = ReplaceOrderRequest.newBuilder()
            .setAccountId(accountId)
            .setOrderId(orderId)
            .setQuantity(newQuantity)
        if (priceUnits != null && priceNano != null) {
            builder.price = Quotation.newBuilder().setUnits(priceUnits).setNano(priceNano).build()
        }
        return investApi.sandboxServiceSync.replaceSandboxOrder(builder.build())
    }

    @Tool(description = "Cancel a sandbox order by accountId and orderId (SandboxService.CancelSandboxOrder).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun cancelSandboxOrder(
        @ToolParam(description = "Account identifier (account_id).") accountId: String,
        @ToolParam(description = "Order ID (order_id).") orderId: String
    ): CancelOrderResponse =
        investApi.sandboxServiceSync.cancelSandboxOrder(
            CancelOrderRequest.newBuilder().setAccountId(accountId).setOrderId(orderId).build()
        )

    @Tool(description = "Get all active sandbox orders for an account (SandboxService.GetSandboxOrders).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxOrders(@ToolParam(description = "Account identifier (account_id).") accountId: String): GetOrdersResponse =
        investApi.sandboxServiceSync.getSandboxOrders(GetOrdersRequest.newBuilder().setAccountId(accountId).build())

    @Tool(description = "Get the state of a sandbox order by accountId and orderId (SandboxService.GetSandboxOrderState).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxOrderState(
        @ToolParam(description = "Account identifier (account_id).") accountId: String,
        @ToolParam(description = "Order ID (order_id).") orderId: String
    ): OrderState =
        investApi.sandboxServiceSync.getSandboxOrderState(
            GetOrderStateRequest.newBuilder().setAccountId(accountId).setOrderId(orderId).build()
        )

    @Tool(description = "Get sandbox portfolio snapshot for the account (SandboxService.GetSandboxPortfolio).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxPortfolio(@ToolParam(description = "User account identifier (account_id).") accountId: String): PortfolioResponse =
        investApi.sandboxServiceSync.getSandboxPortfolio(PortfolioRequest.newBuilder().setAccountId(accountId).build())

    @Tool(description = "Get sandbox positions for the account (SandboxService.GetSandboxPositions).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxPositions(@ToolParam(description = "User account identifier (account_id).") accountId: String): PositionsResponse =
        investApi.sandboxServiceSync.getSandboxPositions(PositionsRequest.newBuilder().setAccountId(accountId).build())

    @Tool(description = "Get sandbox operations for a time range [from,to] in epoch seconds (SandboxService.GetSandboxOperations).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxOperations(
        @ToolParam(description = "Account identifier (account_id).") accountId: String,
        @ToolParam(description = "Start of the period in UTC as UNIX seconds (from).") fromEpochSeconds: Long,
        @ToolParam(description = "End of the period in UTC as UNIX seconds (to).") toEpochSeconds: Long
    ): OperationsResponse =
        investApi.sandboxServiceSync.getSandboxOperations(
            OperationsRequest.newBuilder()
                .setAccountId(accountId)
                .setFrom(com.google.protobuf.Timestamp.newBuilder().setSeconds(fromEpochSeconds).build())
                .setTo(com.google.protobuf.Timestamp.newBuilder().setSeconds(toEpochSeconds).build())
                .build()
        )

    @Tool(description = "Get sandbox operations using cursor pagination, optionally providing cursor and time range (SandboxService.GetSandboxOperationsByCursor).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxOperationsByCursor(
        @ToolParam(description = "Account identifier (account_id). Other parameters are optional.") accountId: String,
        @ToolParam(description = "Cursor to start the response from (cursor). Optional.") cursor: String?,
        @ToolParam(description = "Start of the period in UTC as UNIX seconds (from). Optional.") fromEpochSeconds: Long?,
        @ToolParam(description = "End of the period in UTC as UNIX seconds (to). Optional.") toEpochSeconds: Long?
    ): GetOperationsByCursorResponse =
        investApi.sandboxServiceSync.getSandboxOperationsByCursor(
            GetOperationsByCursorRequest.newBuilder()
                .setAccountId(accountId)
                .apply {
                    if (cursor != null) setCursor(cursor)
                    if (fromEpochSeconds != null) setFrom(com.google.protobuf.Timestamp.newBuilder().setSeconds(fromEpochSeconds).build())
                    if (toEpochSeconds != null) setTo(com.google.protobuf.Timestamp.newBuilder().setSeconds(toEpochSeconds).build())
                }
                .build()
        )

    @Tool(description = "Get sandbox withdraw limits for the account (SandboxService.GetSandboxWithdrawLimits).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxWithdrawLimits(@ToolParam(description = "User account identifier (account_id).") accountId: String): WithdrawLimitsResponse =
        investApi.sandboxServiceSync.getSandboxWithdrawLimits(WithdrawLimitsRequest.newBuilder().setAccountId(accountId).build())

    @Tool(description = "Calculate the maximum number of lots available in sandbox for a potential order at optional price (SandboxService.GetSandboxMaxLots).",
        resultConverter = ProtobufAwareToolCallResultConverter::class)
    fun getSandboxMaxLots(
        @ToolParam(description = "Account identifier (account_id).") accountId: String,
        @ToolParam(description = "Instrument identifier (instrument_id): FIGI or instrument_uid.") instrumentId: String,
        @ToolParam(description = "Instrument price (Quotation.units). Optional.") priceUnits: Long?,
        @ToolParam(description = "Instrument price fractional part (Quotation.nano). Optional.") priceNano: Int?
    ): GetMaxLotsResponse =
        investApi.sandboxServiceSync.getSandboxMaxLots(
            GetMaxLotsRequest.newBuilder()
                .setAccountId(accountId)
                .setInstrumentId(instrumentId)
                .apply {
                    if (priceUnits != null && priceNano != null) {
                        price = Quotation.newBuilder().setUnits(priceUnits).setNano(priceNano).build()
                    }
                }
                .build()
        )
}

