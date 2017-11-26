package com.tikalk.antsfirehose;

public class Constants {
    public static final int DEFAULT_PORT = 8083;
    //    Kafka Config
    public static final String KAFKA_ANTS_TOPIC = "firehose_topic";
    public static final String KAFKA_GROUP_ID = "ants_stream";


    //    Game Config
    public static final int ANT_SPEED = 50;
    public static final int KEEP_SMASHED_MOVES = 50; // after ant is smashed, wait 50 moves before removing from game
    public static final int ANT_REGENRATION = 50;
    public static final int ANTS_PER_TEAM = 10;
}
