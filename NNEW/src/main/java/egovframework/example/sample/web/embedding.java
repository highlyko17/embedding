package egovframework.example.sample.web;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.primitives.Floats;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import egovframework.example.API.*;
import io.pinecone.PineconeClient;
import io.pinecone.PineconeClientConfig;
import io.pinecone.PineconeConnection;
import io.pinecone.PineconeConnectionConfig;
import io.pinecone.PineconeException;
import io.pinecone.proto.QueryRequest;
import io.pinecone.proto.QueryResponse;
import io.pinecone.proto.QueryVector;
import io.pinecone.proto.UpsertRequest;
import io.pinecone.proto.UpsertResponse;
import io.pinecone.proto.Vector;

@RestController
@RequestMapping("/embedding")
public class embedding {
	
	@PostMapping("/postEmbedd")
	public ResponseEntity<?> sendEmbedding(@RequestBody Map<String,String> list) {
		OpenAiService service = new OpenAiService("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",Duration.ofMinutes(9999));
//		List<String> inpStr = <"Sample document text goes here", "there will be several phrases in each batch">;
		List<String> inpStr = new ArrayList<String>();
		inpStr.add(list.get("Q"));
		inpStr.add(list.get("R"));
		inpStr.add(list.get("S"));

		new EmbeddingRequest();
		EmbeddingRequest emb = EmbeddingRequest.builder()
				.input(inpStr)
				.model("text-embedding-ada-002")
				.build();
		
		service.createEmbeddings(emb).getData().forEach(System.out::println);
		
		return ResponseEntity.ok(service.createEmbeddings(emb).getData());
	}

	@PostMapping("/postEmbedd2")
	public QueryResponse fromSingleton2(@RequestBody Map<String,String> list, Model model){
		List<String> input = new ArrayList<String>();
		input.add(list.get("Q"));
		ResponseEntity<?> result = ResponseEntity.ok(OAISingleton.getInstance().getEmbeddingData("text-embedding-ada-002", input));
		
		//List<Double> xqList = OAISingleton.getInstance().getEmbeddingData("text-embedding-ada-002", input).get(0).getEmbedding();
		//float[] xq = convertDoubleListToFloatArray(xqList);
		
		List<Double> xqList = OAISingleton.getInstance().getEmbeddingData("text-embedding-ada-002", input).get(0).getEmbedding();
	    List<Float> xqFloatList = xqList.stream()
	            .map(Double::floatValue)
	            .collect(Collectors.toList());
	    
	    //System.out.println(xqFloatList);
		
		///////////
		String apiKey = "106018db-f9fb-4675-8bb1-3c3cfcecfc7c";
        String indexName = "semantic-search-openai";
        String environment = "gcp-starter";
        String projectName = "a1b00f5";
        String namespace = "embedded";
        int topK = 1;
        
        System.out.println("Starting application...");

        PineconeClientConfig configuration = new PineconeClientConfig()
                .withApiKey(apiKey)
                .withEnvironment(environment)
                .withProjectName(projectName);

        PineconeClient pineconeClient = new PineconeClient(configuration);


        PineconeConnectionConfig connectionConfig = new PineconeConnectionConfig()
                .withIndexName(indexName);
        
        System.out.println(connectionConfig.getIndexName());
        
        /*
        Vector v1 = Vector.newBuilder()
                .setId("v1")
                .addAllValues(Floats.asList(1F, 3F, 5F))
                .build();

        Vector v2 = Vector.newBuilder()
                .setId("v2")
                .addAllValues(Floats.asList(5F, 3F, 1F))
                .build();

        UpsertRequest upsertRequest = UpsertRequest.newBuilder()
                .addVectors(v1)
                .addVectors(v2)
                //.setNamespace(args.namespace)
                .build();
        
        System.out.println("Sending upsert request:");
        System.out.println(upsertRequest);
        
        PineconeConnection conn = pineconeClient.connect(new PineconeConnectionConfig().withIndexName(indexName));
        
        UpsertResponse upsertResponse = conn.getBlockingStub().upsert(upsertRequest);

        System.out.println("Got upsert response:");
        System.out.println(upsertResponse);
        */
        
        QueryVector qv = QueryVector.newBuilder()
        		.addAllValues(xqFloatList)
        		//.addAllValues(Floats.asList(1F, 2F, 2F))
        		.setTopK(topK)
        		//.setNamespace(namespace)
        		.build();
        
        System.out.println("1");
        
        QueryRequest qr = QueryRequest.newBuilder()
        		.addQueries(qv)
        		//.setNamespace(namespace)
        		.setTopK(topK)
        		.setIncludeMetadata(true)
        		.build();
        
        System.out.println("2");
        
        PineconeConnection conn = pineconeClient.connect(connectionConfig);
        QueryResponse qRes = conn.getBlockingStub().query(qr);
        
        System.out.println("3");
        
        System.out.println("result: " + qRes);
        
        return qRes;

        //return result;
	}
	
	private static float[] convertDoubleListToFloatArray(List<Double> dl) {
		float[] f = new float[dl.size()];
		
		for(int i = 0; i < dl.size(); i++) {
			f[i] = dl.get(i).floatValue();
		}
		
		return f;
	}
}