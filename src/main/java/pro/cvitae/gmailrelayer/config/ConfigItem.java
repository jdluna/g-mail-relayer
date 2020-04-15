package pro.cvitae.gmailrelayer.config;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({ "forFrom", "forApplicationId", "forMessageType", "overrideFrom", "overrideFromAddress", "authType",
        "username", "password", "domain", "host", "port", "starttls" })
public class ConfigItem extends DefaultConfigItem {

    @Getter
    @Setter
    private String forFrom;

    @Getter
    @Setter
    private String forApplicationId;

    @Getter
    @Setter
    private String forMessageType;

}
