package com.epam.crs.parsing;

import java.util.List;

public class LexemeBuffer {
    private int pos;

    public List<Lexeme> lexemes;

    public LexemeBuffer(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
    }

    public Lexeme next() {
        if (!hasNext()){
            throw new RuntimeException("There's no next lexeme in the Buffer.");
        }
        return lexemes.get(pos++);
    }

    public boolean hasNext() {
        return pos < lexemes.size();
    }

    public void back() {
        if (pos == 0) {
            throw new RuntimeException("Buffer can't step back. It's already at first position.");
        }
        pos--;
    }

    public int getPos() {
        return pos;
    }
}
