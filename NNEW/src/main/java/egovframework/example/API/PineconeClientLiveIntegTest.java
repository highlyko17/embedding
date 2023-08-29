package egovframework.example.API;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Floats;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import io.pinecone.*;
import io.pinecone.proto.*;

public class PineconeClientLiveIntegTest {
	public static class Args{
		public String projectName = "Starter";
		public String indexName = "openai";
		public String environment = "us-west1-gcp-free";
		public String apiKey = Keys.PINECONE_KEY;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PineconeClientLiveIntegTest.class);
	private Args args;
	private PineconeClient pineconeClient;
	
    public void setUp() throws Exception {
        args = new ObjectMapper()
                .readValue(System.getenv("PINECONE_TEST_ARGS"), Args.class);

        PineconeClientConfig configuration = new PineconeClientConfig()
                .withApiKey(args.apiKey)
                .withEnvironment(args.environment)
                .withProjectName(args.projectName)
                .withServerSideTimeoutSec(10);

        pineconeClient = new PineconeClient(configuration);
    }
    
    public void sanity() {
    	String ns = "temp_namespace";
        PineconeConnection conn = pineconeClient.connect(
                new PineconeConnectionConfig()
                        .withIndexName("java-test"));
        
        // query
        float[] rawVector = {1.0F, 2.0F, 3.0F};
        QueryVector queryVector = QueryVector.newBuilder()
                .addAllValues(Floats.asList(rawVector))
                .setFilter(Struct.newBuilder()
                        .putFields("some_field", Value.newBuilder()
                                .setStructValue(Struct.newBuilder()
                                        .putFields("$lt", Value.newBuilder()
                                                .setNumberValue(3)
                                                .build()))
                                .build())
                        .build())
                .setNamespace(ns)
                .build();

        QueryRequest queryRequest = QueryRequest.newBuilder()
                .addQueries(queryVector)
                .setNamespace(ns)
                .setTopK(2)
                .setIncludeMetadata(true)
                .build();
//        Query by id example
//        QueryRequest queryRequest = QueryRequest.newBuilder()
//                .setId("v2")
//                .setNamespace("temp_namespace")
//                .setTopK(2)
//                .setIncludeMetadata(true)
//                .build();

        QueryResponse queryResponse = conn.getBlockingStub().query(queryRequest);
        assertThat(queryResponse, notNullValue());
        assertThat(queryResponse.getResultsList(), notNullValue());
        assertThat(queryResponse.getResultsCount(), equalTo(1));
//        When querying by id and single vector, we get matches instead of results. hence use this assert
//        When using them.
//        assertThat(queryResponse.getMatchesCount(), equalTo(1));
        logger.info("got query result ids: "
                + queryResponse.getResultsList().get(0).getMatchesList());
        assertThat(queryResponse.getResultsList().get(0).getMatchesList().size(), equalTo(2));
    }
}
