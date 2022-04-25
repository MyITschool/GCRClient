package com.elseboot3909.GCRClient.Entities;

public class CommitInfo {
    private String commit;
    private CommitInfo parent;
    private GitPersonInfo author;
    private GitPersonInfo committer;
    private String subject;
    private String message;

    public String getMessage() { return message; }
}
