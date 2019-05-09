package com.groupoffive.listapp.util;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AmazonQueue implements Queue {

    public static String    QUEUE_PRODUCT_ANALYSE = "https://sqs.us-east-1.amazonaws.com/079032154478/will-list-product-analyse";
    private       AmazonSQS sqs                   = AmazonSQSClientBuilder.standard().withRegion("us-east-1").build();

    public void sendMessageToQueue(String message, String queueUrl) {
        sqs.sendMessage(new SendMessageRequest(queueUrl, queueUrl));
    }

}
