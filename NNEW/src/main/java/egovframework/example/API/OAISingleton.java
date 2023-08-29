package egovframework.example.API;

import java.time.Duration;
import java.util.List;


import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

public class OAISingleton {
	//create and manage OpenAiService singleton. 
	public static OAISingleton instance = new OAISingleton();
	private static OpenAiService service;
	private OAISingleton() {
		try {
			service = new OpenAiService(Keys.OPENAPI_KEY,Duration.ofMinutes(9999));
		}
		catch(Exception e) {
			throw new RuntimeException("Error While creating class: OAISingleton\n");
		}
	}
	public static OAISingleton getInstance() {
		return instance;
	}

	private EmbeddingRequest emb = new EmbeddingRequest();
	
	private EmbeddingRequest getEmbeddingModel(String model, List<String> input) {
		new EmbeddingRequest();
		return EmbeddingRequest.builder()
				.input(input)
				.model(model)
				.build();
	}
	
	//build and return data
	public List<Embedding> getEmbeddingData(String model, List<String> input){
		OpenAiService s = service;
		emb = getEmbeddingModel(model,input);
		return s.createEmbeddings(emb).getData();
	}
}