import software.amazon.awssdk.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
//import com.amazonaws.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.time.LocalDate;
import java.util.*;

public class CreateSQSQueue {

    static String[] vehicleType = new String[]{"SUV", "MINI", "LARGE", "SEDAN", "COMPACT"};
    static String[] vehicle = new String[]{"Toyota", "Hyundai", "Kia", "Lamborghini","Porsche"};
    public static final String QUEUE_NAME = "customer_orders";

    public static void main(String[] args) throws InterruptedException {
//        AmazonSQS sqsQueue = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
//        createSQSQueue(sqsQueue);
        for (int i=0; i<5; i++) {
            sendRequestToSQS(sqsClient);
            Thread.sleep(4000);
        }
    }

    private static void sendRequestToSQS(SqsClient sqs) {
        final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        Random random = new Random();
        int index = random.nextInt(vehicleType.length);
        System.out.println(vehicleType[index]);

        messageAttributes.put("vehicleType", MessageAttributeValue.builder().
                dataType("String").stringValue(vehicleType[index]).build());
        messageAttributes.put("vehicle", MessageAttributeValue.builder()
                .dataType("String").stringValue(vehicle[index]).build());
        int day = generateRandomBetweenTwo(1, 28);
        int month = generateRandomBetweenTwo(1, 12);
        int year = generateRandomBetweenTwo(2022, 2023);
        String randomFutureDate = LocalDate.of(year, month, day).toString();
        messageAttributes.put("deliveryDate", MessageAttributeValue.builder().dataType("String").stringValue(randomFutureDate).build());

        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl("https://sqs.us-east-1.amazonaws.com/844691826815/customer_orders")
                .messageBody("some request")
                .messageAttributes(messageAttributes)
                .delaySeconds(5)
                .build();
        sqs.sendMessage(sendMsgRequest);
    }

    private static int generateRandomBetweenTwo(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    private static AmazonSQS createSQSQueue(AmazonSQS sqsQueue) {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "10")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqsQueue.createQueue(createQueueRequest);
        } catch (AmazonSQSException amazonSQSException) {
            throw amazonSQSException;
        }
        return sqsQueue;
    }
}
