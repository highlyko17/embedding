package egovframework.example.sample.web;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.finetune.FineTuneEvent;
import com.theokanning.openai.finetune.FineTuneRequest;
import com.theokanning.openai.finetune.FineTuneRequest.FineTuneRequestBuilder;
import com.theokanning.openai.finetune.FineTuneResult;

import lombok.var;

@RestController
@RequestMapping("/restapi_ft3")
public class restapitest3 {
	static String fileId;
	static String fineTuneId;
	
	@GetMapping("/{name}")
	public String sayHello(@PathVariable String name) {
		String result="Hello eGovFramework!! name : " + name;  
		return result;
	}
	
	@PostMapping("/postMethod")
    public static String sendQuestion2() {
        OpenAiService service = new OpenAiService("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");
        //OpenAiService service = new OpenAiService("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", Duration.ofMinutes(100));
        fileId = service.uploadFile("fine-tune", "/Users/Hyeli/Desktop/egov/NNEW/src/main/resources/fine-tuning-data.jsonl").getId();
        FineTuneRequest fineTuneRequest = FineTuneRequest.builder()
                .trainingFile(fileId)
                .model("davinci")
                .suffix("text-james-003")
                .nEpochs(5)
                .batchSize(3)
                .build();
        
        FineTuneResult finalRequest = service.createFineTune(fineTuneRequest);
        fineTuneId = finalRequest.getId();
        
        Date createDate = new Date(finalRequest.getCreatedAt());
        
        String ret = finalRequest.getFineTunedModel() + " was created at " + createDate + " with status: "+ finalRequest.getStatus() + " ID: " + fineTuneId;
        
        System.out.println(ret);
        
        return ret;
    }


}
