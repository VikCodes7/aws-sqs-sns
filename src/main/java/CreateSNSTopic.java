import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

public class CreateSNSTopic {

    public static final String TOPIC_NAME = "cars_email_notification";

    public static void main(String[] args) {
        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1)
                        .build();
        createTopic(snsClient);
    }

    private static void createTopic(SnsClient snsClient) {
        CreateTopicResponse createTopicResponse = null;
        try {
            CreateTopicRequest createTopicRequest = CreateTopicRequest.builder()
                    .name(TOPIC_NAME).build();
            createTopicResponse = snsClient.createTopic(createTopicRequest);
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}
