package br.ufpe.cin.erbotool.entity.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"key",
"name",
"spdx_id",
"url",
"node_id"
})
public class License {

	@JsonProperty("key")
	public String key;
	@JsonProperty("name")
	public String name;
	@JsonProperty("spdx_id")
	public String spdxId;
	@JsonProperty("url")
	public String url;
	@JsonProperty("node_id")
	public String nodeId;

}
