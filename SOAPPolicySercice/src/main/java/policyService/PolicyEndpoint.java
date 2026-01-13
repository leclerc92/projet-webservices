package policyService;

import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Component
public class PolicyEndpoint {

    private static final String NAMESPACE_URI = "http://www.example.com/soap-api";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPolicyRequest")
    @ResponsePayload
    public GetPolicyResponse getPolicy(@RequestPayload GetPolicyRequest request) {
        GetPolicyResponse response = new GetPolicyResponse();

        System.out.println("SOAP Request reçue pour : " + request.getPolicyNumber());

        // Si le contrat commence par "A", c'est valide
        if (request.getPolicyNumber() != null && request.getPolicyNumber().startsWith("A")) {
            response.setValid(true);
            response.setCoverageAmount(5000.00);
            response.setDescription("Policy Valid. Coverage active.");
        } else {
            // Sinon rejeté
            response.setValid(false);
            response.setCoverageAmount(0.0);
            response.setDescription("Policy Not Found or Expired.");
        }

        return response;
    }
}