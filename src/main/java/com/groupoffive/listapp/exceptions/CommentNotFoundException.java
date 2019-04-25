package com.groupoffive.listapp.exceptions;

public class CommentNotFoundException extends Exception {

    public CommentNotFoundException() {
        super("Não foi encontrado nenhum comentário correspondente");
    }
}
