package policyService;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "getPolicyResponse", namespace = "http://www.example.com/soap-api")
public class GetPolicyResponse {

    @XmlElement(namespace = "http://www.example.com/soap-api")
    private boolean valid;

    @XmlElement(namespace = "http://www.example.com/soap-api")
    private double coverageAmount;

    @XmlElement(namespace = "http://www.example.com/soap-api")
    private String description;
}