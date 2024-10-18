package com.paymentchain.customer.common;

@Schema(description="This model is used to return errors in RFC 7307 which created a generalized error-handling schema composed by five parts")
@NoArgsConstructor
@Data
public class StandarizedApiExceptionResponse {
    @Schema(description="The unique uri identifier that categorizes the error", name="type",
            requireMode=Schema.RequireMode.REQUIRED, example="/errors/authentication/not-authorized")
    private String type;
    
    @Schema(description="A brief human-readable message about the error", name="title",
            requireMode=Schema.RequireMode.REQUIRED, example="The user does not have autorization")
    private String title;
    
    @Schema(description="The unique error code", name="code",
            required=false, example="192")
    private String code;
    
    @Schema(description="A human-readable explanation of the error", name="detail",
            required=Schema.RequireMode.REQUIRED, example="The user does not have the properly permissions to access the "
            + "resource, please contact with us https://sotobotero.com")
    private String detail;
    
    @Schema(description="A URI that identifies the specific occurrence of the error", name="instance",
            required=Schema.RequireMode.REQUIRED, example="/errors/authentication/not-authorized/01")
    private String instance;
    
    public StandarizedApiExceptionResponse(String aType, String aTitle, String aCode, 
            String aDetail, String anInstance)
    {
        super();
        type = aType;
        title = aTitle;
        code = aCode;
        detail = aDetail;
        instance = anInstance;
        
    }
}
