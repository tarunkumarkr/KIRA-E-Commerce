package com.tekpyramid.kira.Authservice.exception;

public class DuplicateRecordException extends RuntimeException{
    public DuplicateRecordException(String message){
        super(message);
    }
    public DuplicateRecordException(){
        super("Duplicate record already exist!");
    }
}
