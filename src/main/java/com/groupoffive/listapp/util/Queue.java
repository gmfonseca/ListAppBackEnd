package com.groupoffive.listapp.util;

public interface Queue {

    void sendMessageToQueue(String message, String queueUrl);

}
