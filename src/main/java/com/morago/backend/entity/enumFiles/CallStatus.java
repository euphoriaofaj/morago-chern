package com.morago.backend.entity.enumFiles;

public enum CallStatus {
    CONNECT_NOT_SET,            //0
    SUCCESSFUL,                 //1
    MISSED,                     //2
    ERROR,                      //3
    TRANSLATOR_NOT_ONLINE,      //4
    TRANSLATOR_NOT_AVAILABLE,   //5
    FCM_AND_APN_TOKEN_NULL,     //6
    PUSH_NOT_SENT               //7
}
