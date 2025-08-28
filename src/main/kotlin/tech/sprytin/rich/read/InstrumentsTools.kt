package tech.sprytin.rich.read

import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.tinkoff.piapi.contract.v1.*
import ru.ttech.piapi.core.InvestApi
import tech.sprytin.rich.api.McpToolService

@Service
@Profile(value = ["read-mcp"])
class InstrumentsTools(private val investApi: InvestApi) : McpToolService {

	@Tool(description = "Get brand information by brand UID (InstrumentsService.GetBrandBy). Returns brand metadata for grouping instruments under the same brand.")
	fun getBrandBy(uid: String): Brand =
		investApi.instrumentsServiceSync.getBrandBy(
			GetBrandRequest.newBuilder().setId(uid).build()
		)

	@Tool(description = "List brands available in the catalog (InstrumentsService.GetBrands). Supports mapping instruments to their brands.")
	fun getBrands(): GetBrandsResponse =
		investApi.instrumentsServiceSync.getBrands(GetBrandsRequest.getDefaultInstance())

	@Tool(description = "Get fundamental metrics for assets by a list of asset UIDs (InstrumentsService.GetAssetFundamentals). Returns financial indicators for analysis.")
	fun getAssetFundamentals(assetUids: List<String>): GetAssetFundamentalsResponse =
		investApi.instrumentsServiceSync.getAssetFundamentals(
			GetAssetFundamentalsRequest.newBuilder().addAllAssets(assetUids).build()
		)

	@Tool(description = "Get issuer reports for an instrument within an optional period [from,to] epoch seconds (InstrumentsService.GetAssetReports). Returns financial and corporate reports.")
	fun getAssetReports(instrumentId: String, fromEpochSeconds: Long?, toEpochSeconds: Long?): GetAssetReportsResponse =
		investApi.instrumentsServiceSync.getAssetReports(
			GetAssetReportsRequest.newBuilder()
				.setInstrumentId(instrumentId)
				.apply {
					if (fromEpochSeconds != null) setFrom(com.google.protobuf.Timestamp.newBuilder().setSeconds(fromEpochSeconds).build())
					if (toEpochSeconds != null) setTo(com.google.protobuf.Timestamp.newBuilder().setSeconds(toEpochSeconds).build())
				}
				.build()
		)

	@Tool(description = "Get analyst consensus forecasts with optional pagination (InstrumentsService.GetConsensusForecasts). Returns target prices and recommendations summary.")
	fun getConsensusForecasts(limit: Int?, pageNumber: Int?): GetConsensusForecastsResponse =
		investApi.instrumentsServiceSync.getConsensusForecasts(
			GetConsensusForecastsRequest.newBuilder()
				.apply {
					if (limit != null || pageNumber != null) {
						paging = Page.newBuilder().apply {
							if (limit != null) setLimit(limit)
							if (pageNumber != null) setPageNumber(pageNumber)
						}.build()
					}
				}
				.build()
		)

	@Tool(description = "Get detailed analyst forecasts for a specific instrument (InstrumentsService.GetForecastBy).")
	fun getForecastBy(instrumentId: String): GetForecastResponse =
		investApi.instrumentsServiceSync.getForecastBy(
			GetForecastRequest.newBuilder().setInstrumentId(instrumentId).build()
		)

	@Tool(description = "Get exchange risk rates and parameters (InstrumentsService.GetRiskRates). Useful for margin requirements and risk calculations.")
	fun getRiskRates(): RiskRatesResponse =
		investApi.instrumentsServiceSync.getRiskRates(RiskRatesRequest.getDefaultInstance())
}


