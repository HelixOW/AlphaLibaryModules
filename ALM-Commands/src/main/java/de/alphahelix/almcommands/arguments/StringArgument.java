package de.alphahelix.almcommands.arguments;

public class StringArgument extends Argument<String> {
    @Override
    public boolean matches() {
        return true;
    }

    @Override
    public String fromArgument() {
        return getEnteredArgument();
    }
}
