package egovframework.example.sample.web;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

@RestController
@RequestMapping("/restapi")
public class restapitest {
	
	@GetMapping("/{name}")
	public String sayHello(@PathVariable String name) {
		String result="Hello eGovFramework!! name : " + name;  
		return result;
	}
	
	@PostMapping("/postMethod")
    public ResponseEntity<?> sendQuestion(@RequestBody Map<String, String> list) {
        OpenAiService service = new OpenAiService("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",Duration.ofMinutes(9999));
        CompletionRequest completionRequest = CompletionRequest.builder()
            .prompt(list.get("Q"))
            .model("text-davinci-003") 
            .echo(true) 
            .maxTokens(1000)
            .temperature((double) 1.0f)
            .build();
        //service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
        return ResponseEntity.ok(service.createFineTuneCompletion(completionRequest).getChoices());
        //return ResponseEntity.ok(service.createCompletion(completionRequest).getChoices());
    }


}
