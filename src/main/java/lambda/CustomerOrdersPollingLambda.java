package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.HashMap;
import java.util.Map;

public class CustomerOrdersPollingLambda implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        String vehicleType = "";
        String vehicle = "";
        String futureDate = "";
        for (SQSEvent.SQSMessage msg: sqsEvent.getRecords()) {
            vehicleType = new String(String.valueOf(msg.getMessageAttributes().get("vehicleType").getStringValue()));
            vehicle = new String(String.valueOf(msg.getMessageAttributes().get("vehicle").getStringValue()));
            futureDate = new String(String.valueOf(msg.getMessageAttributes().get("deliveryDate").getStringValue()));
        }
        StringBuilder messageToDeliver = new StringBuilder();
        messageToDeliver.append("Vehicle Type : ").append(vehicleType).append("\n");
        messageToDeliver.append("Vehicle to deliver : ").append(vehicle).append("\n");
        messageToDeliver.append("Delivery Date : ").append(futureDate).append("\n").append("\n").append("Above are the details for delivery.");
        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1)
                .build();
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("Vehicle_Type", MessageAttributeValue.builder().dataType("String").stringValue("SUV").build());
        try {
//            PublishRequest request1 = PublishRequest.builder()
            PublishRequest request = PublishRequest.builder().subject("Car Booking Confirmation").message(messageToDeliver.toString()).messageAttributes(attributes).
                    topicArn("arn:aws:sns:us-east-1:844691826815:cars_email_notification").build();
            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());
        } catch (SnsException ex) {
            System.out.println(ex);
        }
        return null;
    }
}
