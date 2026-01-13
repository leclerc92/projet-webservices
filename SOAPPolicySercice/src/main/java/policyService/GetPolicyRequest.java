package policyService;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "getPolicyRequest", namespace = "http://www.example.com/soap-api")
public class GetPolicyRequest {

    @XmlElement(namespace = "http://www.example.com/soap-api")
    private String policyNumber;

    @XmlElement(namespace = "http://www.example.com/soap-api")
    private String claimType;
}