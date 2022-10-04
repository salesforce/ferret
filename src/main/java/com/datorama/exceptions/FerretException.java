package com.datorama.exceptions;

public class FerretException extends RuntimeException {
    private int exitCode;
    private String stage;


    public FerretException(String message,int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public FerretException(String message,int exitCode,String stage) {
        super(message);
        this.exitCode = exitCode;
    }

    public String getStage() {
        return stage;
    }

    public int getExitCode() {
        return exitCode;
    }
}
