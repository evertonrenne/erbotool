package br.ufpe.cin.erbotool.entity.github;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"total_count",
"incomplete_results",
"items"
})
public class ApiResult {

	@JsonProperty("total_count")
	public Integer totalCount;
	@JsonProperty("incomplete_results")
	public Boolean incompleteResults;
	@JsonProperty("items")
	public List<Item> items = null;
}
