package com.groupoffive.listapp.exceptions;

public class NotUserCommentException extends Exception {

    public NotUserCommentException() {
        super("O comentario nao foi feito por este usuario.");
    }
}
