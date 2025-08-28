package tech.sprytin.rich.read

import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.tinkoff.piapi.contract.v1.*
import ru.ttech.piapi.core.InvestApi
import tech.sprytin.rich.api.McpToolService

@Service
@Profile(value = ["read-mcp"])
class MarketDataTools(private val investApi: InvestApi) : McpToolService {

    @Tool(description = "Get historical candles for an instrument over [from,to] epoch seconds with the specified interval (MarketDataService.GetCandles). Time is UTC. Returns OHLCV series.")
    fun getCandles(
        instrumentId: String,
        fromEpochSeconds: Long,
        toEpochSeconds: Long,
        interval: CandleInterval
    ): GetCandlesResponse =
        investApi.marketDataServiceSync.getCandles(
            GetCandlesRequest.newBuilder()
                .setInstrumentId(instrumentId)
                .setFrom(com.google.protobuf.Timestamp.newBuilder().setSeconds(fromEpochSeconds).build())
                .setTo(com.google.protobuf.Timestamp.newBuilder().setSeconds(toEpochSeconds).build())
                .setInterval(interval)
                .build()
        )

    @Tool(description = "Get the order book (depth N) for an instrument (MarketDataService.GetOrderBook). Returns bids/asks snapshots at the requested depth.")
    fun getOrderBook(instrumentId: String, depth: Int): GetOrderBookResponse =
        investApi.marketDataServiceSync.getOrderBook(
            GetOrderBookRequest.newBuilder()
                .setInstrumentId(instrumentId)
                .setDepth(depth)
                .build()
        )

    @Tool(description = "Get trading statuses for multiple instruments (MarketDataService.GetTradingStatuses). Useful to check if instruments are currently tradable.")
    fun getTradingStatuses(instrumentIds: List<String>): GetTradingStatusesResponse =
        investApi.marketDataServiceSync.getTradingStatuses(
            GetTradingStatusesRequest.newBuilder()
                .addAllInstrumentId(instrumentIds)
                .build()
        )

    @Tool(description = "Get last trades (tick trades) for an instrument over [from,to] epoch seconds (MarketDataService.GetLastTrades).")
    fun getLastTrades(
        instrumentId: String,
        fromEpochSeconds: Long,
        toEpochSeconds: Long
    ): GetLastTradesResponse =
        investApi.marketDataServiceSync.getLastTrades(
            GetLastTradesRequest.newBuilder()
                .setInstrumentId(instrumentId)
                .setFrom(com.google.protobuf.Timestamp.newBuilder().setSeconds(fromEpochSeconds).build())
                .setTo(com.google.protobuf.Timestamp.newBuilder().setSeconds(toEpochSeconds).build())
                .build()
        )

    @Tool(description = "Get close prices for the instrument over the specified period (MarketDataService.GetClosePrices). Returns InstrumentClosePrice series.")
    fun getClosePrices(
        instrumentId: String,
        fromEpochSeconds: Long,
        toEpochSeconds: Long
    ): GetClosePricesResponse =
        investApi.marketDataServiceSync.getClosePrices(
            GetClosePricesRequest.newBuilder()
                .addInstruments(
                    InstrumentClosePriceRequest.newBuilder()
                        .setInstrumentId(instrumentId)
                        .build()
                )
                .build()
        )

    // getMarketValues отсутствует в артефакте SDK

    @Tool(description = "Get trading status for a single instrument (MarketDataService.GetTradingStatus).")
    fun getTradingStatus(instrumentId: String): GetTradingStatusResponse =
        investApi.marketDataServiceSync.getTradingStatus(
            GetTradingStatusRequest.newBuilder().setInstrumentId(instrumentId).build()
        )
}


